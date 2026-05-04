package messaging;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;



/**
 * Consumidor del RabbitMQ cuando llega el evento de Lectura.
 * Como es un proyecto con quarkus, la definición / configuración del Channel con los Exchange, queue, binds estan en application.properties.
 * @author Juan Pablo Heras 
 */

@ApplicationScoped
public class LecturaConsumer {
    
    @Inject
    AlarmaPublisher alarmaPublisher;
    
    
    @Incoming("sensor-readings")
    public void consumir(String mensaje) {
        System.out.println("Lectura recibida: " + mensaje);
        
        //Simulación de evaluación hardcodeada por ahora
        if (mensaje.contains("\"valor\": 42")) {
            alarmaPublisher.publicarAlarmaMock();
            System.out.println("Alarma disparada!");
        }
    }
    
    
    
}
