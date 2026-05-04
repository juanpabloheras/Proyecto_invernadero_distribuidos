package consumer;

import service.SensorDataService;
import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class SensorEventConsumer {

    private static final Gson gson = new Gson();
    private final Set<String> eventosYaProcesados = new HashSet<>();

    // Inyectamos la capa de negocio, NO el repositorio directamente
    @Inject
    SensorDataService sensorService;

    @Incoming("sensor-readings")
    @Blocking
    public void procesarLecturaSensor(String json) {
        try {
            SensorEvent event = gson.fromJson(json, SensorEvent.class);

            if (eventosYaProcesados.contains(event.getEventId()) || !"sensorReadingEvent".equals(event.getEventType())) {
                return;
            }
            eventosYaProcesados.add(event.getEventId());

            System.out.println("\n[RABBITMQ] Evento recibido: " + event.getSensorId());

            for (String medicion : event.getMediciones()) {
                extraerYDelegar(medicion);
            }

        } catch (Exception e) {
            System.err.println("[ERROR] No se pudo procesar el JSON: " + e.getMessage());
        }
    }

    private void extraerYDelegar(String medicion) {
        try {
            String[] partes = medicion.split(":");
            if (partes.length != 2) return;

            String tipo = partes[0].trim();
            String valorConUnidad = partes[1].trim();
            
            String valorStr = valorConUnidad.replaceAll("[^0-9.-]", "");
            String unidadStr = valorConUnidad.replace(valorStr, "").trim();
            double valor = Double.parseDouble(valorStr);

            // Pasamos la papa caliente a la capa de Servicio
            sensorService.procesarYGuardar(tipo, valor, unidadStr);

        } catch (Exception e) {
            System.err.println("  [ERROR] Medición corrupta: " + medicion);
        }
    }

    public static class SensorEvent {
        private String eventId;
        private int version;
        private String eventType;
        private Timestamp timestamp;
        private String sensorId;
        private ArrayList<String> mediciones;
        
        public String getEventId() { return eventId; }
        public String getEventType() { return eventType; }
        public String getSensorId() { return sensorId; }
        public ArrayList<String> getMediciones() { return mediciones; }
    }
}