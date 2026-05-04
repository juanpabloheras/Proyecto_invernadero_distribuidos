package itson.mx.alarmevaluatorservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itson.mx.alarmevaluatorservice.events.AlarmaDisparadaEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

/**
 * Publica eventos de alarma al exchange invernadero.events con routing key
 * alarma.disparada (configurado en application.properties bajo "alarmas-out").
 */
@ApplicationScoped
public class AlarmaPublisher {

    private static final Logger log = Logger.getLogger(AlarmaPublisher.class);

    @Inject
    @Channel("alarmas-out")
    Emitter<String> emitter;

    @Inject
    ObjectMapper mapper;

    public void publicarAlarma(AlarmaDisparadaEvent evento) {
        try {
            String json = mapper.writeValueAsString(evento);
            emitter.send(json);
            log.infof("[ALARM-EVALUATOR] Alarma publicada: id=%d sensor=%s tipo=%s valor=%.2f",
                    evento.idConfiguracionAlarma(),
                    evento.idSensor(),
                    evento.tipoAlarma(),
                    evento.valorLeido());
        } catch (JsonProcessingException e) {
            log.error("Error serializando AlarmaDisparadaEvent", e);
        }
    }

    /**
     * Publicador legacy hardcodeado — se mantiene solo por si quedó alguna prueba
     * apuntando a este método. La lógica real usa publicarAlarma(AlarmaDisparadaEvent).
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
