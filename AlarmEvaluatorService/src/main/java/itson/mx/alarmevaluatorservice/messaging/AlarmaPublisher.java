/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

/**
 * Publicador del RabbitMQ para tirar un evento de Alarma. Como es un proyecto
 * con quarkus, la definición / configuración del Channel con los Exchange,
 * queue, binds estan en application.properties.
 *
 * @author Juan Pablo Heras
 */
@ApplicationScoped
public class AlarmaPublisher {

    @Inject
    @Channel("alarmas-out")
    Emitter<String> emitter;

    /**
     * Evento para publicar
     */
    public void publicarAlarmaMock() {
        String alarma = """
                        {
                                      "tipoEvento": "ALARMA_DISPARADA",
                                      "mensaje": "Temperatura muy caliente",
                                      "mediosNotificacion": ["EMAIL", "SMS", "SSE"]
                                    }
                        """;
        
        emitter.send(alarma);
    }

}
