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
public class SensorAdapterB extends AbstractSensorAdapter {

    private static final int TAMANIO_PAQUETE = 15;
    private static final byte TIPO_SENSOR_B = 0x02;

    @Override
    public SensorEvent adaptar(byte[] paquete) {
        if (paquete == null || paquete.length < TAMANIO_PAQUETE) {
            throw new IllegalArgumentException("Paquete inválido para Protocolo B. Esperado: "
                    + TAMANIO_PAQUETE + " bytes, Recibido: "
                    + (paquete != null ? paquete.length : 0));
        }

        try {
            ByteBuffer buffer = ByteBuffer.wrap(paquete).order(ByteOrder.BIG_ENDIAN);

            byte version = buffer.get();
            byte comando = buffer.get();
            byte tipoSensor = buffer.get();
            int longitud = buffer.getShort() & 0xFFFF;

            if (tipoSensor != TIPO_SENSOR_B) {
                System.out.println("[ADVERTENCIA] Tipo de sensor no coincide. Esperado: 0x02, Recibido: 0x"
                        + String.format("%02X", tipoSensor));
            }

            long deviceId = buffer.getInt() & 0xFFFFFFFFL;
            String sensorId = String.valueOf(deviceId);

            short temperaturaRaw = buffer.getShort();
            double temperatura = temperaturaRaw / 10.0;

            short humedadRaw = buffer.getShort();
            double humedad = humedadRaw / 10.0;

            int checksumRecibido = buffer.getShort() & 0xFFFF;
            int checksumCalculado = calcularChecksum(paquete, paquete.length - 2);
            if (checksumRecibido != checksumCalculado) {
                System.out.println("[ADVERTENCIA] Checksum no coincide. Recibido: "
                        + checksumRecibido + ", Calculado: " + checksumCalculado);
            }

            SensorEvent event = new SensorEvent();
            event.setVersion(version);
            event.setSensorId(sensorId);
            // Protocolo B no incluye timestamp, usar timestamp actual
            event.setTimestamp(new Timestamp(System.currentTimeMillis()));

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
}
