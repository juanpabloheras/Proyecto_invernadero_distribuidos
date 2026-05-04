package itson.mx.alarmevaluatorservice.grpc;

import java.util.function.BiFunction;
import io.quarkus.grpc.MutinyClient;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: alarma-notificacion.proto")
public class AlarmaNotificacionServiceClient implements AlarmaNotificacionService, MutinyClient<MutinyAlarmaNotificacionServiceGrpc.MutinyAlarmaNotificacionServiceStub> {

    private final MutinyAlarmaNotificacionServiceGrpc.MutinyAlarmaNotificacionServiceStub stub;

    public AlarmaNotificacionServiceClient(String name, io.grpc.Channel channel, BiFunction<String, MutinyAlarmaNotificacionServiceGrpc.MutinyAlarmaNotificacionServiceStub, MutinyAlarmaNotificacionServiceGrpc.MutinyAlarmaNotificacionServiceStub> stubConfigurator) {
        this.stub = stubConfigurator.apply(name, MutinyAlarmaNotificacionServiceGrpc.newMutinyStub(channel));
    }

    private AlarmaNotificacionServiceClient(MutinyAlarmaNotificacionServiceGrpc.MutinyAlarmaNotificacionServiceStub stub) {
        this.stub = stub;
    }

    public AlarmaNotificacionServiceClient newInstanceWithStub(MutinyAlarmaNotificacionServiceGrpc.MutinyAlarmaNotificacionServiceStub stub) {
        return new AlarmaNotificacionServiceClient(stub);
    }

    @Override
    public MutinyAlarmaNotificacionServiceGrpc.MutinyAlarmaNotificacionServiceStub getStub() {
        return stub;
    }

    @Override
    public io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> notificarConfiguracionCreada(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest request) {
        return stub.notificarConfiguracionCreada(request);
    }

    @Override
    public io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> obtenerConfiguracionesActivas(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest request) {
        return stub.obtenerConfiguracionesActivas(request);
    }
}
