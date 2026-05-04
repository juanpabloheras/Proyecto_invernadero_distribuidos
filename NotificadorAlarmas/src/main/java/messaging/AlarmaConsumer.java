
package messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import sse.NotificacionesSseService;

/**
 * Consumidor del evento alarma del RabbitMQ
 * @author Juan Heras
 */
@ApplicationScoped
public class AlarmaConsumer {
    
    @Inject
    NotificacionesSseService sseService;
    
    @Incoming("alarmas-in")
    public void consumir(String mensaje){
        System.out.println("Alarma recibida: " + mensaje);
        
        sseService.enviar(mensaje);
    }
}
