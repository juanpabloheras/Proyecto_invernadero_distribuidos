"""
Simulador Sensor B — Protocolo con int16 escalado, deviceId uint32 y tipo_sensor en header.

Header (5 bytes): version(u8) | comando(u8) | tipo_sensor(u8) | longitud(u16 big-endian)
Payload:          deviceId(u32) | temperatura(i16 ×0.1) | humedad(i16 ×0.1) | checksum(u16)

Uso:
    python sensor_b_sim.py                       # valores por defecto
    python sensor_b_sim.py --device 99999        # otro deviceId
    python sensor_b_sim.py --loop 5              # 5 envíos
"""

import struct
import socket
import time
import argparse

# ── Constantes protocolo ────────────────────────────────────────────────────
CMD_SENSOR_DATA   = 0x01
PROTOCOLO_VERSION = 0x01
SENSOR_TIPO_B     = 0x02

# Formato big-endian, sin padding automático
# Header:   !BBBH  → version, comando, tipo_sensor, longitud
# Payload:  Ihh    → deviceId(u32), temperatura(i16), humedad(i16)
# checksum: H
FORMATO = "!BBBH IhhH"
TAMANIO = struct.calcsize(FORMATO)  # 5+4+2+2+2 = 15 bytes

def timestamp_ms() -> int:
    return int(time.time() * 1000)

def checksum(data: bytes) -> int:
    return sum(data) & 0xFFFF

def escalar(valor: float) -> int:
    """Convierte float a int16 escalado ×10 (23.4 → 234)."""
    return int(round(valor * 10))

def construir_paquete(device_id: int, temperatura: float, humedad: float) -> bytes:
    sin_checksum = struct.pack(
        "!BBBH Ihh",
        PROTOCOLO_VERSION,
        CMD_SENSOR_DATA,
        SENSOR_TIPO_B,
        TAMANIO,
        device_id,
        escalar(temperatura),
        escalar(humedad),
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
    version, cmd, tipo, longitud, dev_id, temp_raw, hum_raw, cs = struct.unpack(FORMATO, data)
    print("─" * 35)
    print(f"Protocolo   : B (int16 escalado)")
    print(f"DeviceId    : {dev_id}")
    print(f"Tipo sensor : 0x{tipo:02X}")
    print(f"Temperatura : {temp_raw / 10:.1f} °C  (raw={temp_raw})")
    print(f"Humedad     : {hum_raw / 10:.1f} %   (raw={hum_raw})")
    print(f"Checksum    : 0x{cs:04X}")
    print(f"Tamaño      : {len(data)} bytes")
    print("─" * 35)

def enviar(host: str, port: int, data: bytes) -> None:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(10)
        s.connect((host, port))
        s.sendall(data)
        print(f"✓ Enviados {len(data)} bytes a {host}:{port}")

def main():
    parser = argparse.ArgumentParser(description="Simulador Sensor B")
    parser.add_argument("--host",   default="127.0.0.1")
    parser.add_argument("--port",   type=int, default=9090)
    parser.add_argument("--device", type=int, default=12345)
    parser.add_argument("--temp",   type=float, default=23.4)
    parser.add_argument("--hum",    type=float, default=62.1)
    parser.add_argument("--loop",   type=int, default=1)
    parser.add_argument("--delay",  type=float, default=2.0)
    parser.add_argument("--dry",    action="store_true", help="No conectar, solo mostrar el paquete")
    args = parser.parse_args()

    for i in range(args.loop):
        pkt = construir_paquete(args.device, args.temp, args.hum)
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