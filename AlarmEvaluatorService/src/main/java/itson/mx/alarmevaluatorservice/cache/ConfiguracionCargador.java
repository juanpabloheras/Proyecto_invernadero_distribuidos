package itson.mx.alarmevaluatorservice.cache;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.StartupEvent;
import itson.mx.alarmevaluatorservice.grpc.AlarmaNotificacionServiceGrpc;
import itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarma;
import itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse;
import itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ConfiguracionCargador {

    private static final Logger log = Logger.getLogger(ConfiguracionCargador.class);

    @GrpcClient("crud-invernadero")
    AlarmaNotificacionServiceGrpc.AlarmaNotificacionServiceBlockingStub stub;

    void onStart(@Observes StartupEvent ev) {
        log.info("Cargando configuraciones activas desde el CRUD...");

        try {
            ObtenerConfiguracionesRequest request = ObtenerConfiguracionesRequest.newBuilder().build();
            ConfiguracionesResponse response = stub.obtenerConfiguracionesActivas(request);

            log.infof("Recibidas %d configuraciones activas", response.getConfiguracionesList().size());

            for (ConfiguracionAlarma c : response.getConfiguracionesList()) {
                log.infof("  - [%d] %s | tipo=%s | operador=%s | valorCritico=%.2f",
                        c.getIdConfiguracionAlarma(),
                        c.getNombreAlarma(),
                        c.getTipoAlarma(),
                        c.getOperador(),
                        c.getValorCritico());
            }
        } catch (Exception e) {
            log.error("No se pudieron obtener las configuraciones activas", e);
        }
    }
}
