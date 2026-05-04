package itson.mx.alarmevaluatorservice.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Modelo del JSON canónico que publica SensorDataAdapter al exchange invernadero.events
 * con routing key sensor.lectura.registrada.
 *
 * Ejemplo:
 * {
 *   "eventId": "uuid",
 *   "version": 1,
 *   "eventType": "sensor.lectura.registrada",
 *   "timestamp": "2026-05-03T20:24:07.131-0700",
 *   "sensorId": "12345",
 *   "mediciones": ["temperatura:23.4°C", "humedad:62.1%"]
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorEvent(
        String eventId,
        int version,
        String eventType,
        String timestamp,
        String sensorId,
        List<String> mediciones) {
}
