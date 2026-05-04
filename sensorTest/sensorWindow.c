/*
 * Sensor C — Protocolo batch con uint16 offset y CRC-16
 *
 * Diferencias respecto a Sensor A y B:
 *   - Tipo sensor : 0x03 (nuevo, no usado por A ni B)
 *   - Encoding    : uint16 sin signo con offset fijo
 *                   temperatura = (valor_real + 40.0) * 100  → rango -40..+215 °C
 *                   humedad     = valor_real * 100            → rango 0..655 %
 *   - Batch       : envía N mediciones en un solo paquete (num_campos en header)
 *   - CRC-16      : polinomio 0x8005 (más robusto que suma simple)
 *   - Header      : 6 bytes (vs 4 de A, vs 5 de B)
 *
 * Layout del paquete (tamaño variable):
 *
 *   ┌─────────────────────────────────────────────────────┐
 *   │ Header (6 bytes)                                    │
 *   │   version    u8   = 0x01                            │
 *   │   comando    u8   = 0x01                            │
 *   │   tipo       u8   = 0x03  ← identificador C        │
 *   │   num_campos u8   = N     ← cantidad de mediciones │
 *   │   longitud   u16  = tamaño total del paquete        │
 *   ├─────────────────────────────────────────────────────┤
 *   │ deviceId     u32  (4 bytes)                         │
 *   │ timestamp    u64  (8 bytes)                         │
 *   ├─────────────────────────────────────────────────────┤
 *   │ Medicion[0..N-1] — cada una 4 bytes:                │
 *   │   temperatura  u16  (raw = (°C + 40) * 100)         │
 *   │   humedad      u16  (raw = % * 100)                 │
 *   ├─────────────────────────────────────────────────────┤
 *   │ CRC-16        u16  (2 bytes, polinomio 0x8005)      │
 *   └─────────────────────────────────────────────────────┘
 *
 * Tamaño total = 6 + 4 + 8 + N*4 + 2 = 20 + N*4 bytes
 * Para N=3: 32 bytes
 *
 * Compilar (Windows):
 *   gcc sensor_c.c -o sensor_c.exe -lws2_32
 *
 * Compilar (Linux/Mac — sin Winsock):
 *   gcc sensor_c.c -o sensor_c -DLINUX_BUILD
 */

#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <stdlib.h>

#ifndef LINUX_BUILD
  #include <winsock2.h>
  #include <ws2tcpip.h>
  #pragma comment(lib, "ws2_32.lib")
#else
  #include <arpa/inet.h>
  #include <sys/socket.h>
  #include <unistd.h>
  #include <sys/time.h>
  #define closesocket close
  #define SOCKET int
  #define INVALID_SOCKET -1
  #define SOCKET_ERROR   -1
#endif

/* ── Constantes protocolo ──────────────────────────────────────────────────── */
#define CMD_SENSOR_DATA   0x01
#define PROTOCOLO_VERSION 0x01
#define SENSOR_TIPO_C     0x03   /* identificador único de este protocolo */

#define MAX_MEDICIONES    8      /* máximo batch por paquete */

/* Offset y escala para uint16 sin signo:
 *   temp_raw = (temperatura_real + TEMP_OFFSET) * TEMP_ESCALA
 *   temp_real = temp_raw / TEMP_ESCALA - TEMP_OFFSET
 */
#define TEMP_OFFSET  40.0f
#define TEMP_ESCALA 100.0f
#define HUM_ESCALA  100.0f

/* ── Structs (packed, sin padding) ─────────────────────────────────────────── */

typedef struct __attribute__((packed)) {
    uint8_t  version;
    uint8_t  comando;
    uint8_t  tipo_sensor;
    uint8_t  num_campos;    /* cantidad de Medicion en este paquete */
    uint16_t longitud;      /* tamaño total del paquete en bytes    */
} HeaderC;

typedef struct __attribute__((packed)) {
    uint16_t temperatura;   /* (°C + 40) * 100, sin signo           */
    uint16_t humedad;       /* % * 100, sin signo                   */
} Medicion;

typedef struct __attribute__((packed)) {
    HeaderC  header;                       /*  6 bytes */
    uint32_t deviceId;                     /*  4 bytes */
    uint64_t timestamp;                    /*  8 bytes */
    Medicion mediciones[MAX_MEDICIONES];   /* N*4 bytes (usamos num_campos) */
    uint16_t crc16;                        /*  2 bytes */
} PaqueteSensorC;

/* ── Timestamp en ms ────────────────────────────────────────────────────────── */
uint64_t timestamp_ms(void)
{
#ifndef LINUX_BUILD
    FILETIME ft;
    GetSystemTimeAsFileTime(&ft);
    ULARGE_INTEGER uli;
    uli.LowPart  = ft.dwLowDateTime;
    uli.HighPart = ft.dwHighDateTime;
    return (uli.QuadPart - 116444736000000000ULL) / 10000;
#else
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return (uint64_t)tv.tv_sec * 1000 + tv.tv_usec / 1000;
#endif
}

/* ── CRC-16 (polinomio 0x8005) ─────────────────────────────────────────────── */
uint16_t crc16(const uint8_t *data, size_t len)
{
    uint16_t crc = 0xFFFF;
    for (size_t i = 0; i < len; i++) {
        crc ^= (uint16_t)data[i] << 8;
        for (int j = 0; j < 8; j++) {
            if (crc & 0x8000)
                crc = (crc << 1) ^ 0x8005;
            else
                crc <<= 1;
        }
    }
    return crc;
}

/* ── Encoding uint16 ────────────────────────────────────────────────────────── */
uint16_t encode_temp(float celsius)
{
    float raw = (celsius + TEMP_OFFSET) * TEMP_ESCALA;
    if (raw < 0)      raw = 0;
    if (raw > 65535)  raw = 65535;
    return (uint16_t)raw;
}

uint16_t encode_hum(float porcentaje)
{
    float raw = porcentaje * HUM_ESCALA;
    if (raw < 0)      raw = 0;
    if (raw > 65535)  raw = 65535;
    return (uint16_t)raw;
}

float decode_temp(uint16_t raw) { return raw / TEMP_ESCALA - TEMP_OFFSET; }
float decode_hum(uint16_t raw)  { return raw / HUM_ESCALA; }

/* ── Calcular tamaño real del paquete (sin campos vacíos del batch) ─────────── */
size_t tamanio_paquete(uint8_t n)
{
    return sizeof(HeaderC) + sizeof(uint32_t) + sizeof(uint64_t)
         + n * sizeof(Medicion) + sizeof(uint16_t);
}

/* ── Construir paquete ──────────────────────────────────────────────────────── */
PaqueteSensorC construir_paquete(
    uint32_t device_id,
    const float temperaturas[],
    const float humedades[],
    uint8_t n)
{
    if (n > MAX_MEDICIONES) n = MAX_MEDICIONES;

    PaqueteSensorC pkt;
    memset(&pkt, 0, sizeof(pkt));

    size_t tam = tamanio_paquete(n);

    pkt.header.version     = PROTOCOLO_VERSION;
    pkt.header.comando     = CMD_SENSOR_DATA;
    pkt.header.tipo_sensor = SENSOR_TIPO_C;
    pkt.header.num_campos  = n;
    pkt.header.longitud    = htons((uint16_t)tam);

    pkt.deviceId  = htonl(device_id);
    pkt.timestamp = timestamp_ms();

    for (uint8_t i = 0; i < n; i++) {
        pkt.mediciones[i].temperatura = htons(encode_temp(temperaturas[i]));
        pkt.mediciones[i].humedad     = htons(encode_hum(humedades[i]));
    }

    /* CRC sobre todo excepto los últimos 2 bytes (el campo crc16 mismo) */
    uint16_t crc = crc16((uint8_t *)&pkt, tam - sizeof(uint16_t));
    pkt.crc16 = htons(crc);

    return pkt;
}

/* ── Debug hex ──────────────────────────────────────────────────────────────── */
void imprimir_hex(const uint8_t *data, size_t len)
{
    printf("\nPaquete binario (%zu bytes):\n", len);
    for (size_t i = 0; i < len; i++) {
        printf("%02X ", data[i]);
        if ((i + 1) % 8 == 0) printf("\n");
    }
    printf("\n");
}

/* ── Debug legible ──────────────────────────────────────────────────────────── */
void imprimir_paquete(const PaqueteSensorC *pkt)
{
    uint8_t n   = pkt->header.num_campos;
    size_t  tam = tamanio_paquete(n);

    printf("─────────────────────────────────\n");
    printf("Protocolo   : C (uint16 offset+escala, batch, CRC-16)\n");
    printf("TipoSensor  : 0x%02X\n", pkt->header.tipo_sensor);
    printf("DeviceId    : %u\n",     ntohl(pkt->deviceId));
    printf("Timestamp   : %llu ms\n",(unsigned long long)pkt->timestamp);
    printf("NumCampos   : %u mediciones en este paquete\n", n);
    for (uint8_t i = 0; i < n; i++) {
        uint16_t t_raw = ntohs(pkt->mediciones[i].temperatura);
        uint16_t h_raw = ntohs(pkt->mediciones[i].humedad);
        printf("  [%u] Temp : %.2f °C  (raw=%u)\n", i, decode_temp(t_raw), t_raw);
        printf("  [%u] Hum  : %.2f %%  (raw=%u)\n", i, decode_hum(h_raw),  h_raw);
    }
    printf("CRC-16      : 0x%04X\n", ntohs(pkt->crc16));
    printf("Tamaño      : %zu bytes\n", tam);
    printf("─────────────────────────────────\n");
}

/* ── Main ───────────────────────────────────────────────────────────────────── */
int main(void)
{
    setvbuf(stdout, NULL, _IONBF, 0);
    printf("Iniciando Sensor C (protocolo batch uint16 + CRC-16)...\n");

#ifndef LINUX_BUILD
    WSADATA wsa;
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        printf("Error WSAStartup: %d\n", WSAGetLastError());
        return 1;
    }
#endif

    /* Batch de 3 mediciones tomadas en el mismo ciclo de reporte */
    float temperaturas[] = { 23.4f, 24.1f, 22.8f };
    float humedades[]    = { 62.1f, 63.5f, 61.0f };
    uint8_t n            = 3;

    PaqueteSensorC pkt = construir_paquete(99001, temperaturas, humedades, n);

    imprimir_paquete(&pkt);
    imprimir_hex((uint8_t *)&pkt, tamanio_paquete(n));

    /* Conectar y enviar */
    SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCKET) {
        printf("Error creando socket\n");
#ifndef LINUX_BUILD
        WSACleanup();
#endif
        return 1;
    }

    struct sockaddr_in servidor;
    servidor.sin_family = AF_INET;
    servidor.sin_port   = htons(9090);
    inet_pton(AF_INET, "127.0.0.1", &servidor.sin_addr);

    printf("Conectando a 127.0.0.1:9090...\n");
    if (connect(sock, (struct sockaddr *)&servidor, sizeof(servidor)) == SOCKET_ERROR) {
        printf("Error connect — verifica que el servidor este corriendo\n");
        closesocket(sock);
#ifndef LINUX_BUILD
        WSACleanup();
#endif
        return 1;
    }

    size_t tam = tamanio_paquete(n);
    int enviados = send(sock, (char *)&pkt, (int)tam, 0);
    if (enviados == SOCKET_ERROR)
        printf("Error send\n");
    else
        printf("Enviados %d bytes exitosamente!\n", enviados);

    closesocket(sock);
#ifndef LINUX_BUILD
    WSACleanup();
#endif
    printf("Conexion cerrada.\n");
    return 0;
}