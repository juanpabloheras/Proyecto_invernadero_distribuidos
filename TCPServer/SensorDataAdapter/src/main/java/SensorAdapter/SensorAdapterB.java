package SensorAdapter;

import org.itson.adapter.model.SensorEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Adapter para Protocolo B - Int16 escalado con deviceId
 * Formato binario: !BBBH IhhH (15 bytes)
 *
 * Estructura del paquete:
 * [0]      version (u8)
 * [1]      comando (u8)
 * [2]      tipo_sensor (u8) = 0x02
 * [3-4]    longitud (u16 big-endian)
 * [5-8]    deviceId (u32 big-endian)
 * [9-10]   temperatura (i16 big-endian, escalado ×10)
 * [11-12]  humedad (i16 big-endian, escalado ×10)
 * [13-14]  checksum (u16 big-endian)
 *
 * Decodificación: valor_real = raw / 10.0
 *
 * @author Sistema Invernadero Distribuido
 */
public class SensorAdapterB implements ISensorAdapter {

    private static final int TAMANIO_PAQUETE = 15;
    private static final byte TIPO_SENSOR_B = 0x02;

    @Override
    public SensorEvent adaptar(byte[] paquete) {
        if (paquete == null || paquete.length < TAMANIO_PAQUETE) {
            throw new IllegalArgumentException("Paquete inválido para Protocolo B. Esperado: " + TAMANIO_PAQUETE + " bytes, Recibido: " + (paquete != null ? paquete.length : 0));
        }

        try {
            ByteBuffer buffer = ByteBuffer.wrap(paquete).order(ByteOrder.BIG_ENDIAN);

            // Leer header
            byte version = buffer.get();        // [0]
            byte comando = buffer.get();        // [1]
            byte tipoSensor = buffer.get();     // [2]
            int longitud = buffer.getShort() & 0xFFFF;  // [3-4] unsigned short

            // Verificar tipo de sensor
            if (tipoSensor != TIPO_SENSOR_B) {
                System.out.println("[ADVERTENCIA] Tipo de sensor no coincide. Esperado: 0x02, Recibido: 0x" + String.format("%02X", tipoSensor));
            }

            // Leer deviceId (u32)
            long deviceId = buffer.getInt() & 0xFFFFFFFFL;  // [5-8] unsigned int
            String sensorId = String.valueOf(deviceId);

            // Leer temperatura (i16 escalado ×10)
            short temperaturaRaw = buffer.getShort();       // [9-10]
            double temperatura = temperaturaRaw / 10.0;

            // Leer humedad (i16 escalado ×10)
            short humedadRaw = buffer.getShort();           // [11-12]
            double humedad = humedadRaw / 10.0;

            // Leer checksum
            int checksumRecibido = buffer.getShort() & 0xFFFF;  // [13-14]

            // Validar checksum
            int checksumCalculado = calcularChecksum(paquete, paquete.length - 2);
            if (checksumRecibido != checksumCalculado) {
                System.out.println("[ADVERTENCIA] Checksum no coincide. Recibido: " + checksumRecibido + ", Calculado: " + checksumCalculado);
            }

            // Crear evento canónico
            SensorEvent event = new SensorEvent();
            event.setVersion(version);
            event.setSensorId(sensorId);
            // Protocolo B no incluye timestamp, usar timestamp actual
            event.setTimestamp(new Timestamp(System.currentTimeMillis()));

            // Construir ArrayList de mediciones
            ArrayList<String> mediciones = new ArrayList<>();
            mediciones.add(String.format("temperatura:%.1f°C", temperatura));
            mediciones.add(String.format("humedad:%.1f%%", humedad));

            event.setMediciones(mediciones);

            return event;

        } catch (Exception e) {
            throw new RuntimeException("Error al parsear paquete del Protocolo B: " + e.getMessage(), e);
        }
    }

    @Override
    public String getTipo() {
        return "B";
    }

    /**
     * Calcula checksum simple (suma de todos los bytes mod 0xFFFF)
     * @param data Datos del paquete
     * @param len Longitud a procesar (sin incluir el checksum final)
     * @return Checksum calculado
     */
    private int calcularChecksum(byte[] data, int len) {
        int sum = 0;
        for (int i = 0; i < len; i++) {
            sum += (data[i] & 0xFF);
        }
        return sum & 0xFFFF;
    }
}
