package consumer;

import entidades.Medicion;
import service.SensorDataService;
import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class SensorEventConsumer {

    private static final Gson gson = new Gson();
    private final Set<String> eventosYaProcesados = new HashSet<>();

    // Patrón regex robusto: captura números decimales, negativos, notación científica
    private static final Pattern NUMERO_PATTERN = Pattern.compile("([+-]?\\d+\\.?\\d*(?:[eE][+-]?\\d+)?)");

    // Inyectamos la capa de negocio, NO el repositorio directamente
    @Inject
    SensorDataService sensorService;

    @Incoming("sensor-readings")
    @Blocking
    public void procesarLecturaSensor(String json) {
        try {
            SensorEvent event = gson.fromJson(json, SensorEvent.class);

            // Deduplicación y filtro de tipo de evento
            if (eventosYaProcesados.contains(event.getEventId()) ||
                !"sensor.lectura.registrada".equals(event.getEventType())) {
                return;
            }
            eventosYaProcesados.add(event.getEventId());

            System.out.println("\n[RABBITMQ] Evento recibido: " + event.getEventId() +
                             " del sensor: " + event.getSensorId());

            // 1. Parsear todas las mediciones
            List<Medicion> mediciones = new ArrayList<>();
            for (String medicionStr : event.getMediciones()) {
                Medicion m = parsearMedicion(medicionStr);
                mediciones.add(m);
            }

            // 2. Parsear timestamp del evento
            Instant timestamp = parsearTimestamp(event.getTimestamp());

            // 3. Delegar al servicio (una sola llamada)
            sensorService.guardarLectura(
                event.getSensorId(),
                timestamp,
                mediciones
            );

        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo procesar el JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parsea una medición individual con manejo robusto de errores.
     * @param medicionStr Formato esperado: "Tipo: valor unidad" (ej: "Temperatura: 25.5°C")
     * @return Medicion con datos parseados o marcada como inválida
     */
    private Medicion parsearMedicion(String medicionStr) {
        try {
            // Validación básica
            if (medicionStr == null || medicionStr.isBlank()) {
                return Medicion.invalida("VACIO", "");
            }

            // Split por ":" (límite 2 por si el valor contiene ":")
            String[] partes = medicionStr.split(":", 2);
            if (partes.length != 2) {
                return Medicion.invalida("DESCONOCIDO", medicionStr);
            }

            String tipo = partes[0].trim();
            String valorConUnidad = partes[1].trim();

            // Extraer número usando regex robusto
            Matcher matcher = NUMERO_PATTERN.matcher(valorConUnidad);

            if (!matcher.find()) {
                // No se encontró número, pero guardamos el string original
                return Medicion.invalida(tipo, valorConUnidad);
            }

            String valorStr = matcher.group(1);
            Double valorNum = Double.parseDouble(valorStr);

            // Lo que queda después del número es la unidad
            String unidad = valorConUnidad.replace(valorStr, "").trim();

            // Medición válida
            return new Medicion(tipo, valorConUnidad, valorNum, unidad);

        } catch (NumberFormatException e) {
            System.err.println("[WARN] Error parseando número en: " + medicionStr);
            String tipo = medicionStr.contains(":") ? medicionStr.split(":")[0].trim() : "DESCONOCIDO";
            return Medicion.invalida(tipo, medicionStr);
        } catch (Exception e) {
            System.err.println("[WARN] Error inesperado parseando: " + medicionStr);
            e.printStackTrace();
            return Medicion.invalida("ERROR", medicionStr);
        }
    }

    /**
     * Parsea timestamp del evento con fallback a Instant.now()
     */
    private Instant parsearTimestamp(String timestampStr) {
        try {
            if (timestampStr == null || timestampStr.isBlank()) {
                System.err.println("[WARN] Timestamp ausente, usando Instant.now()");
                return Instant.now();
            }
            return Instant.parse(timestampStr);
        } catch (Exception e) {
            System.err.println("[WARN] Timestamp inválido '" + timestampStr + "', usando Instant.now()");
            return Instant.now();
        }
    }

    /**
     * Clase interna que mapea el JSON del evento de RabbitMQ
     */
    public static class SensorEvent {
        private String eventId;
        private int version;
        private String eventType;
        private String timestamp;  // String (formato ISO-8601 en JSON)
        private String sensorId;
        private ArrayList<String> mediciones;

        public String getEventId() { return eventId; }
        public String getEventType() { return eventType; }
        public String getTimestamp() { return timestamp; }
        public String getSensorId() { return sensorId; }
        public ArrayList<String> getMediciones() { return mediciones; }
    }
}