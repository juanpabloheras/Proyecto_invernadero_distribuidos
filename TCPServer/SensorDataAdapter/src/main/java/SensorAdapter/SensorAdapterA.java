package SensorAdapter;

import org.itson.adapter.model.SensorEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Adapter para Protocolo A - Float32 con bitmask de campos
 * Formato binario: !BBH 12s Q ffBx H (36 bytes)
 *
 * Estructura del paquete:
 * [0]      version (u8)
 * [1]      comando (u8)
 * [2-3]    longitud (u16 big-endian)packetProceso
 * [4-15]   sensorId (12 bytes string)
 * [16-23]  timestamp (u64 big-endian)
 * [24-27]  temperatura (float32 big-endian)
 * [28-31]  humedad (float32 big-endian)
 * [32]     campos_activos (u8 bitmask)
 * [33]     padding
 * [34-35]  checksum (u16 big-endian)
 *
 * @author Sistema Invernadero Distribuido
 */
public class SensorAdapterA implements ISensorAdapter {

    private static final int TAMANIO_PAQUETE = 36;
    private static final byte CAMPO_TEMPERATURA = 0x01;
    private static final byte CAMPO_HUMEDAD = 0x02;

    @Override
    public SensorEvent adaptar(byte[] paquete) {
        if (paquete == null || paquete.length < TAMANIO_PAQUETE) {
            throw new IllegalArgumentException("Paquete inválido para Protocolo A. Esperado: " + TAMANIO_PAQUETE + " bytes, Recibido: " + (paquete != null ? paquete.length : 0));
        }

        try {
            ByteBuffer buffer = ByteBuffer.wrap(paquete).order(ByteOrder.BIG_ENDIAN);

            // Leer header
            byte version = buffer.get();        // [0]
            byte comando = buffer.get();        // [1]
            int longitud = buffer.getShort() & 0xFFFF;  // [2-3] unsigned short

            // Leer sensorId (12 bytes)
            byte[] sensorIdBytes = new byte[12];
            buffer.get(sensorIdBytes);          // [4-15]
            String sensorId = new String(sensorIdBytes).trim();

            // Leer timestamp (u64)
            long timestampMillis = buffer.getLong();  // [16-23]
            Timestamp timestamp = new Timestamp(timestampMillis);

            // Leer temperatura (float32)
            float temperatura = buffer.getFloat();   // [24-27]

            // Leer humedad (float32)
            float humedad = buffer.getFloat();       // [28-31]

            // Leer campos activos (bitmask)
            byte camposActivos = buffer.get();       // [32]

            // Leer padding
            buffer.get();                            // [33]

            // Leer checksum
            int checksumRecibido = buffer.getShort() & 0xFFFF;  // [34-35]

            // Validar checksum
            int checksumCalculado = calcularChecksum(paquete, paquete.length - 2);
            if (checksumRecibido != checksumCalculado) {
                System.out.println("[ADVERTENCIA] Checksum no coincide. Recibido: " + checksumRecibido + ", Calculado: " + checksumCalculado);
            }

            // Crear evento canónico
            SensorEvent event = new SensorEvent();
            event.setVersion(version);
            event.setSensorId(sensorId);
            event.setTimestamp(timestamp);

            // Construir ArrayList de mediciones según bitmask
            ArrayList<String> mediciones = new ArrayList<>();

            if ((camposActivos & CAMPO_TEMPERATURA) != 0) {
                mediciones.add(String.format("temperatura:%.1f°C", temperatura));
            }

            if ((camposActivos & CAMPO_HUMEDAD) != 0) {
                mediciones.add(String.format("humedad:%.1f%%", humedad));
            }

            event.setMediciones(mediciones);

            return event;

        } catch (Exception e) {
            throw new RuntimeException("Error al parsear paquete del Protocolo A: " + e.getMessage(), e);
        }
    }

    @Override
    public String getTipo() {
        return "A";
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
