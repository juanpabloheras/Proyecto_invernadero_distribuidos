package org.itson.adapter.messaging;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

/**
 * Publisher de eventos a RabbitMQ usando Topic Exchange
 *
 * Exchange: sensor.events.topic (Topic Exchange)
 * Routing Key: Tipo de evento (eventType del SensorEvent)
 *
 * Ejemplos de routing keys:
 *   - "sensorReadingEvent" → Lecturas de sensores
 *   - "errorEvent" → Eventos de error
 *   - "alertEvent" → Alertas del sistema
 *
 * Consumidores se suscriben por tipo de evento con switch/pattern matching:
 *   - Queue binding "sensorReadingEvent" → Solo lecturas de sensores
 *   - Queue binding "errorEvent" → Solo errores
 *   - Queue binding "#" → Todos los eventos
 *
 * El JSON contiene toda la información (temperatura, humedad, etc.)
 * La validación de duplicados se hace en el consumidor usando eventId del JSON
 *
 * @author Sistema Invernadero Distribuido
 */
@ApplicationScoped
public class RabbitMQPublisher {

    @Inject
    @Channel("sensor-events")
    Emitter<String> emitter;

    /**
     * Publica UN mensaje a RabbitMQ con el tipo de evento como routing key
     * Fire-and-forget: No espera confirmación
     * El consumidor valida duplicados usando eventId del JSON
     *
     * @param eventJson JSON completo del SensorEvent
     * @param eventType Tipo de evento (usado como routing key)
     */
    public void publishEvent(String eventJson, String eventType) {
        if (eventJson == null || eventJson.isEmpty()) {
            System.out.println("[RABBITMQ] JSON vacío, no se publica");
            return;
        }

        String routingKey = (eventType == null || eventType.isEmpty()) ? "unknownEvent" : eventType;

        try {
            // Crear metadata con routing key = eventType
            OutgoingRabbitMQMetadata metadata = new OutgoingRabbitMQMetadata.Builder()
                    .withRoutingKey(routingKey)
                    .build();

            // Crear mensaje con metadata
            Message<String> message = Message.of(eventJson).addMetadata(metadata);

            // Publicar sin esperar confirmación (fire-and-forget)
            emitter.send(message);

            System.out.println("[RABBITMQ] Publicado → " + routingKey);

        } catch (Exception e) {
            System.err.println("[RABBITMQ] Error al publicar: " + e.getMessage());
        }
    }
}
