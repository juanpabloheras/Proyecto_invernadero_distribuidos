package itson.mx.alarmevaluatorservice.messaging;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;


@ApplicationScoped
public class LecturaConsumer {

    @Inject
    AlarmaPublisher alarmaPublisher;

    @Incoming("sensor-readings")
    public void consumir(String mensaje) {
        System.out.println("\n==============================");
        System.out.println("[ALARM-EVALUATOR] Lectura recibida:");
        System.out.println(mensaje);
        System.out.println("==============================\n");

        if (mensaje.contains("temperatura:43.0°C")) {
            alarmaPublisher.publicarAlarmaMock();
            System.out.println("[ALARM-EVALUATOR] Alarma disparada!");
        }
    }
}
