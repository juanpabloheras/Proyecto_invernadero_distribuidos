package itson.mx.alarmevaluatorservice.grpc;

import io.grpc.BindableService;
import io.quarkus.grpc.GrpcService;
import io.quarkus.grpc.MutinyBean;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: alarma-notificacion.proto")
public class AlarmaNotificacionServiceBean extends MutinyAlarmaNotificacionServiceGrpc.AlarmaNotificacionServiceImplBase implements BindableService, MutinyBean {

    private final AlarmaNotificacionService delegate;

    AlarmaNotificacionServiceBean(@GrpcService AlarmaNotificacionService delegate) {
        this.delegate = delegate;
    }

    @Override
    public io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> notificarConfiguracionCreada(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest request) {
        try {
            return delegate.notificarConfiguracionCreada(request);
        } catch (UnsupportedOperationException e) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }
    }

    @Override
    public io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> obtenerConfiguracionesActivas(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest request) {
        try {
            return delegate.obtenerConfiguracionesActivas(request);
        } catch (UnsupportedOperationException e) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }
    }
}
