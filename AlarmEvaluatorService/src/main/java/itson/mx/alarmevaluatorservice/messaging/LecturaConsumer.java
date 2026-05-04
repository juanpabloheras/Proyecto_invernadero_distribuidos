package itson.mx.alarmevaluatorservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import itson.mx.alarmevaluatorservice.cache.ConfiguracionCache;
import itson.mx.alarmevaluatorservice.events.AlarmaDisparadaEvent;
import itson.mx.alarmevaluatorservice.events.SensorEvent;
import itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarma;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class LecturaConsumer {

    private static final Logger log = Logger.getLogger(LecturaConsumer.class);

    @Inject
    AlarmaPublisher alarmaPublisher;

    @Inject
    ConfiguracionCache cache;

    @Inject
    ObjectMapper mapper;

    @Incoming("sensor-readings")
    public void consumir(String mensaje) {
        SensorEvent evento;
        try {
            evento = mapper.readValue(mensaje, SensorEvent.class);
        } catch (Exception e) {
            log.errorf(e, "[ALARM-EVALUATOR] No se pudo parsear el JSON de la lectura: %s", mensaje);
            return;
        }

        if (evento.mediciones() == null || evento.mediciones().isEmpty()) {
            log.warnf("[ALARM-EVALUATOR] Evento %s sin mediciones, se ignora", evento.eventId());
            return;
        }

        log.infof("[ALARM-EVALUATOR] Lectura recibida: sensor=%s mediciones=%d",
                evento.sensorId(), evento.mediciones().size());

        for (String medicion : evento.mediciones()) {
            evaluarMedicion(evento, medicion);
        }
    }

    private void evaluarMedicion(SensorEvent evento, String medicion) {
        String tipo;
        double valor;
        try {
            String[] partes = medicion.split(":");
            tipo = partes[0].trim();
            String valorConUnidad = partes[1].trim();
            String valorStr = valorConUnidad.replaceAll("[^0-9.-]", "");
            valor = Double.parseDouble(valorStr);
        } catch (Exception e) {
            log.warnf("[ALARM-EVALUATOR] Medición con formato inválido: '%s'", medicion);
            return;
        }

        List<ConfiguracionAlarma> configs = cache.obtenerPorTipo(tipo);
        if (configs.isEmpty()) {
            return;
        }

        for (ConfiguracionAlarma config : configs) {
            if (cumpleCondicion(valor, config.getOperador(), config.getValorCritico())) {
                AlarmaDisparadaEvent alarma = construirAlarma(evento, config, valor);
                alarmaPublisher.publicarAlarma(alarma);
            }
        }
    }

    private boolean cumpleCondicion(double valorLeido, String operador, double valorCritico) {
        return switch (operador) {
            case "MAYOR_QUE" -> valorLeido > valorCritico;
            case "MENOR_QUE" -> valorLeido < valorCritico;
            case "IGUAL_A"   -> Math.abs(valorLeido - valorCritico) < 0.001;
            default -> {
                log.warnf("[ALARM-EVALUATOR] Operador desconocido: '%s'", operador);
                yield false;
            }
        };
    }

    private AlarmaDisparadaEvent construirAlarma(SensorEvent evento, ConfiguracionAlarma config, double valorLeido) {
        String mensaje = String.format(
                "%s: %s %s %.2f (valor leído: %.2f)",
                config.getNombreAlarma(),
                config.getTipoAlarma(),
                config.getOperador(),
                config.getValorCritico(),
                valorLeido);

        // idInvernadero no viene en el SensorEvent canónico; queda como "" hasta
        // que se resuelva el mapeo sensorId -> idInvernadero (fuera de scope acá).
        return new AlarmaDisparadaEvent(
                "",
                evento.sensorId(),
                config.getIdConfiguracionAlarma(),
                config.getNombreAlarma(),
                config.getTipoAlarma(),
                config.getOperador(),
                config.getValorCritico(),
                valorLeido,
                evento.timestamp(),
                mensaje);
    }
}
