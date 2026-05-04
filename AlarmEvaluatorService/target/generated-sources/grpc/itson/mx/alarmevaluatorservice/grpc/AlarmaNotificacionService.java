package itson.mx.alarmevaluatorservice.grpc;

import io.quarkus.grpc.MutinyService;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: alarma-notificacion.proto")
public interface AlarmaNotificacionService extends MutinyService {

    io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> notificarConfiguracionCreada(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest request);

    io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> obtenerConfiguracionesActivas(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest request);
}
