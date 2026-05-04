"""
Simulador Sensor A — Protocolo con float32, sensorId string y bitmask de campos.

Header (4 bytes): version(u8) | comando(u8) | longitud(u16 big-endian)
Payload:          sensorId(12s) | timestamp(u64) | temperatura(f32) | humedad(f32)
                  campos_activos(u8) | _padding(u8) | checksum(u16 big-endian)

Uso:
    python sensor_a_sim.py                       # valores por defecto
    python sensor_a_sim.py --host 192.168.1.10   # host remoto
    python sensor_a_sim.py --loop 5              # 5 envíos con 2s entre cada uno
"""

import struct
import socket
import time
import argparse

# ── Constantes protocolo ────────────────────────────────────────────────────
CMD_SENSOR_DATA   = 0x01
PROTOCOLO_VERSION = 0x01
CAMPO_TEMPERATURA = 1 << 0   # 0x01
CAMPO_HUMEDAD     = 1 << 1   # 0x02

# Formato de struct (big-endian, sin padding automático)
# Header:      !BBH      → version, comando, longitud
# sensorId:    12s
# timestamp:   Q         → uint64
# mediciones:  ffBx      → float, float, campos_activos, 1 byte padding
# checksum:    H
FORMATO = "!BBH 12s Q ffBx H"
TAMANIO = struct.calcsize(FORMATO)  # debe ser 36

def timestamp_ms() -> int:
    return int(time.time() * 1000)

def checksum(data: bytes) -> int:
    return sum(data) & 0xFFFF

def construir_paquete(sensor_id: str, temperatura: float, humedad: float) -> bytes:
    campos = CAMPO_TEMPERATURA | CAMPO_HUMEDAD

    # Empaquetamos todo excepto el checksum para calcularlo primero
    sin_checksum = struct.pack(
        "!BBH 12s Q ffBx",
        PROTOCOLO_VERSION,
        CMD_SENSOR_DATA,
        TAMANIO,
        sensor_id.encode().ljust(12, b'\x00')[:12],
        timestamp_ms(),
        temperatura,
        humedad,
        campos,
    )
    cs = checksum(sin_checksum)
    return sin_checksum + struct.pack("!H", cs)

def imprimir_hex(data: bytes) -> None:
    print(f"\nPaquete binario ({len(data)} bytes):")
    for i, b in enumerate(data):
        print(f"{b:02X}", end=" ")
        if (i + 1) % 8 == 0:
            print()
    print("\n")

def imprimir_paquete(data: bytes) -> None:
    version, cmd, longitud, sid, ts, temp, hum, campos, cs = struct.unpack(FORMATO, data)
    print("─" * 35)
    print(f"Protocolo   : A (float32)")
    print(f"SensorId    : {sid}")
    print(f"Timestamp   : {ts} ms")
    print(f"Temperatura : {temp:.1f} °C")
    print(f"Humedad     : {hum:.1f} %")
    print(f"Campos      : 0x{campos:02X}")
    print(f"Checksum    : 0x{cs:04X}")
    print(f"Tamaño      : {len(data)} bytes")
    print("─" * 35)

def enviar(host: str, port: int, data: bytes) -> None:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(10)
        s.connect((host, port))
        s.sendall(data)
        
        respuesta = s.recv(1024)
        print(f"✓ Enviados {len(data)} bytes a {host}:{port}")
        print(f"Respuesta del servidor: {respuesta.decode()}")

def main():
    parser = argparse.ArgumentParser(description="Simulador Sensor A")
    parser.add_argument("--host",   default="127.0.0.1")
    parser.add_argument("--port",   type=int, default=9090)
    parser.add_argument("--id",     default="SENS-0042")
    parser.add_argument("--temp",   type=float, default=23.4)
    parser.add_argument("--hum",    type=float, default=62.1)
    parser.add_argument("--loop",   type=int, default=1, help="Número de envíos")
    parser.add_argument("--delay",  type=float, default=2.0, help="Segundos entre envíos")
    parser.add_argument("--dry",    action="store_true", help="No conectar, solo mostrar el paquete")
    args = parser.parse_args()

    assert TAMANIO == 36, f"Tamaño inesperado: {TAMANIO} (esperado 36)"

    for i in range(args.loop):
        pkt = construir_paquete(args.id, args.temp, args.hum)
        imprimir_paquete(pkt)
        imprimir_hex(pkt)

        if not args.dry:
            try:
                enviar(args.host, args.port, pkt)
            except Exception as e:
                print(f"✗ Error: {e}")

        if i < args.loop - 1:
            time.sleep(args.delay)

if __name__ == "__main__":
    main()