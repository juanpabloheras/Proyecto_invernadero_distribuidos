package org.itson.adapter.messaging;

import io.smallrye.reactive.messaging.rabbitmq.OutgoingRabbitMQMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

/**
 * Publisher de lecturas de sensores a RabbitMQ usando Topic Exchange.
 *
 * Exchange configurado en application.properties:
 *   invernadero.events
 *
 * Canal saliente Quarkus:
 *   sensor-events
 *
 * Routing key:
 *   sensor.lectura.registrada
 */
@ApplicationScoped
public class RabbitMQPublisher {

    private static final String ROUTING_KEY_SENSOR_LECTURA_REGISTRADA =
            "sensor.lectura.registrada";

    @Inject
    @Channel("sensor-events")
    Emitter<String> emitter;

    public void publicarLectura(String eventJson) {
        publicar(eventJson, ROUTING_KEY_SENSOR_LECTURA_REGISTRADA);
    }

    private void publicar(String eventJson, String routingKey) {
        if (eventJson == null || eventJson.isBlank()) {
            System.out.println("[RABBITMQ] JSON vacío, no se publica");
            return;
        }

        try {
            OutgoingRabbitMQMetadata metadata =
                    new OutgoingRabbitMQMetadata.Builder()
                            .withRoutingKey(routingKey)
                            .build();

            Message<String> message =
                    Message.of(eventJson).addMetadata(metadata);

            emitter.send(message);

            System.out.println("[RABBITMQ] Publicado → " + routingKey);

        } catch (Exception e) {
            System.err.println("[RABBITMQ] Error al publicar: " + e.getMessage());
        }
    }
}