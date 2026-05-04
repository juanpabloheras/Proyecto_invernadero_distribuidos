package itson.mx.alarmevaluatorservice.cache;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.StartupEvent;
import itson.mx.alarmevaluatorservice.grpc.AlarmaNotificacionServiceGrpc;
import itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarma;
import itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse;
import itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ConfiguracionCargador {

    private static final Logger log = Logger.getLogger(ConfiguracionCargador.class);

    @GrpcClient("crud-invernadero")
    AlarmaNotificacionServiceGrpc.AlarmaNotificacionServiceBlockingStub stub;

    @Inject
    ConfiguracionCache cache;

    void onStart(@Observes StartupEvent ev) {
        log.info("Cargando configuraciones activas desde el CRUD...");

        try {
            ObtenerConfiguracionesRequest request = ObtenerConfiguracionesRequest.newBuilder().build();
            ConfiguracionesResponse response = stub.obtenerConfiguracionesActivas(request);

            cache.reemplazarTodo(response.getConfiguracionesList());

            log.infof("Caché poblado con %d configuraciones activas", cache.tamanio());

            for (ConfiguracionAlarma c : response.getConfiguracionesList()) {
                log.infof("  - [%d] %s | tipo=%s | operador=%s | valorCritico=%.2f | activa=%s",
                        c.getIdConfiguracionAlarma(),
                        c.getNombreAlarma(),
                        c.getTipoAlarma(),
                        c.getOperador(),
                        c.getValorCritico(),
                        c.getActiva());
            }
        } catch (Exception e) {
            log.error("No se pudieron obtener las configuraciones activas", e);
        }
    }
}
