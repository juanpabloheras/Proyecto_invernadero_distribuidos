package SensorAdapter;

import org.itson.adapter.model.SensorEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Adapter para Protocolo C - Uint16 con offset/escala, batch mode y CRC-16
 * Formato binario: Variable, 20 + N×4 bytes
 *
 * Estructura del paquete:
 * [0]      version (u8)
 * [1]      comando (u8)
 * [2]      tipo_sensor (u8) = 0x03
 * [3]      num_campos (u8) = N
 * [4-5]    longitud (u16 big-endian)
 * [6-9]    deviceId (u32 big-endian)
 * [10-17]  timestamp (u64 big-endian)
 * Para cada medición i (N veces):
 *   [18+i*4 .. 18+i*4+1]   temperatura_i (u16 big-endian)
 *   [18+i*4+2 .. 18+i*4+3] humedad_i (u16 big-endian)
 * [...final 2 bytes] crc16 (u16 big-endian)
 *
 * Decodificación:
 * - Temperatura: °C = (raw / 100.0) - 40.0
 * - Humedad: % = raw / 100.0
 *
 * CRC-16: Polynomial 0x8005, Initial value 0xFFFF
 *
 * @author Sistema Invernadero Distribuido
 */
public class SensorAdapterC extends AbstractSensorAdapter {

    private static final int HEADER_SIZE = 6;
    private static final int DEVICE_TIMESTAMP_SIZE = 12;  // 4 (deviceId) + 8 (timestamp)
    private static final int MEDICION_SIZE = 4;           // 2 (temp) + 2 (hum)
    private static final int CRC_SIZE = 2;
    private static final byte TIPO_SENSOR_C = 0x03;

    @Override
    public SensorEvent adaptar(byte[] paquete) {
        if (paquete == null || paquete.length < HEADER_SIZE + DEVICE_TIMESTAMP_SIZE + CRC_SIZE) {
            throw new IllegalArgumentException("Paquete inválido para Protocolo C. Tamaño mínimo: "
                    + (HEADER_SIZE + DEVICE_TIMESTAMP_SIZE + CRC_SIZE) + " bytes");
        }

        try {
            ByteBuffer buffer = ByteBuffer.wrap(paquete).order(ByteOrder.BIG_ENDIAN);

            byte version = buffer.get();
            byte comando = buffer.get();
            byte tipoSensor = buffer.get();
            int numCampos = buffer.get() & 0xFF;
            int longitud = buffer.getShort() & 0xFFFF;

            if (tipoSensor != TIPO_SENSOR_C) {
                System.out.println("[ADVERTENCIA] Tipo de sensor no coincide. Esperado: 0x03, Recibido: 0x"
                        + String.format("%02X", tipoSensor));
            }

            int tamanioEsperado = HEADER_SIZE + DEVICE_TIMESTAMP_SIZE + (numCampos * MEDICION_SIZE) + CRC_SIZE;
            if (paquete.length != tamanioEsperado) {
                throw new IllegalArgumentException("Tamaño de paquete incorrecto. Esperado: "
                        + tamanioEsperado + " bytes, Recibido: " + paquete.length);
            }

            long deviceId = buffer.getInt() & 0xFFFFFFFFL;
            String sensorId = String.valueOf(deviceId);

            long timestampMillis = buffer.getLong();
            Timestamp timestamp = new Timestamp(timestampMillis);

            ArrayList<String> mediciones = new ArrayList<>();
            for (int i = 0; i < numCampos; i++) {
                int tempRaw = buffer.getShort() & 0xFFFF;
                double temperatura = (tempRaw / 100.0) - 40.0;

                int humRaw = buffer.getShort() & 0xFFFF;
                double humedad = humRaw / 100.0;

                if (numCampos > 1) {
                    mediciones.add(String.format("temperatura_%d:%.1f°C", i + 1, temperatura));
                    mediciones.add(String.format("humedad_%d:%.1f%%", i + 1, humedad));
                } else {
                    mediciones.add(String.format("temperatura:%.1f°C", temperatura));
                    mediciones.add(String.format("humedad:%.1f%%", humedad));
                }
            }

            int crcRecibido = buffer.getShort() & 0xFFFF;
            int crcCalculado = calcularCRC16(paquete, paquete.length - CRC_SIZE);
            if (crcRecibido != crcCalculado) {
                System.out.println("[ADVERTENCIA] CRC-16 no coincide. Recibido: 0x"
                        + String.format("%04X", crcRecibido) + ", Calculado: 0x"
                        + String.format("%04X", crcCalculado));
            }

            SensorEvent event = new SensorEvent();
            event.setVersion(version);
            event.setSensorId(sensorId);
            event.setTimestamp(timestamp);
            event.setMediciones(mediciones);

            return event;

        } catch (Exception e) {
            throw new RuntimeException("Error al parsear paquete del Protocolo C: " + e.getMessage(), e);
        }
    }

    @Override
    public String getTipo() {
        return "C";
    }

    /**
     * CRC-16 con polynomial 0x8005, initial value 0xFFFF.
     */
    private int calcularCRC16(byte[] data, int len) {
        int crc = 0xFFFF;
        for (int i = 0; i < len; i++) {
            crc ^= (data[i] & 0xFF) << 8;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x8005;
                } else {
                    crc <<= 1;
                }
            }
        }
        return crc & 0xFFFF;
    }
}
