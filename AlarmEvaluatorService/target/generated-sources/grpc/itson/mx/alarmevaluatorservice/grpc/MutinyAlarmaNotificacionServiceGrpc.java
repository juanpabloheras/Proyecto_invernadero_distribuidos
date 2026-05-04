package itson.mx.alarmevaluatorservice.grpc;

import static itson.mx.alarmevaluatorservice.grpc.AlarmaNotificacionServiceGrpc.getServiceDescriptor;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;

@jakarta.annotation.Generated(value = "by Mutiny Grpc generator", comments = "Source: alarma-notificacion.proto")
public final class MutinyAlarmaNotificacionServiceGrpc implements io.quarkus.grpc.MutinyGrpc {

    private MutinyAlarmaNotificacionServiceGrpc() {
    }

    public static MutinyAlarmaNotificacionServiceStub newMutinyStub(io.grpc.Channel channel) {
        return new MutinyAlarmaNotificacionServiceStub(channel);
    }

    public static class MutinyAlarmaNotificacionServiceStub extends io.grpc.stub.AbstractStub<MutinyAlarmaNotificacionServiceStub> implements io.quarkus.grpc.MutinyStub {

        private AlarmaNotificacionServiceGrpc.AlarmaNotificacionServiceStub delegateStub;

        private MutinyAlarmaNotificacionServiceStub(io.grpc.Channel channel) {
            super(channel);
            delegateStub = AlarmaNotificacionServiceGrpc.newStub(channel);
        }

        private MutinyAlarmaNotificacionServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
            delegateStub = AlarmaNotificacionServiceGrpc.newStub(channel).build(channel, callOptions);
        }

        @Override
        protected MutinyAlarmaNotificacionServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new MutinyAlarmaNotificacionServiceStub(channel, callOptions);
        }

        public io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> notificarConfiguracionCreada(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest request) {
            return io.quarkus.grpc.stubs.ClientCalls.oneToOne(request, delegateStub::notificarConfiguracionCreada);
        }

        public io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> obtenerConfiguracionesActivas(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest request) {
            return io.quarkus.grpc.stubs.ClientCalls.oneToOne(request, delegateStub::obtenerConfiguracionesActivas);
        }
    }

    public static abstract class AlarmaNotificacionServiceImplBase implements io.grpc.BindableService {

        private String compression;

        /**
         * Set whether the server will try to use a compressed response.
         *
         * @param compression the compression, e.g {@code gzip}
         */
        public AlarmaNotificacionServiceImplBase withCompression(String compression) {
            this.compression = compression;
            return this;
        }

        public io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> notificarConfiguracionCreada(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        public io.smallrye.mutiny.Uni<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> obtenerConfiguracionesActivas(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest request) {
            throw new io.grpc.StatusRuntimeException(io.grpc.Status.UNIMPLEMENTED);
        }

        @java.lang.Override
        public io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(itson.mx.alarmevaluatorservice.grpc.AlarmaNotificacionServiceGrpc.getNotificarConfiguracionCreadaMethod(), asyncUnaryCall(new MethodHandlers<itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest, itson.mx.alarmevaluatorservice.grpc.NotificacionResponse>(this, METHODID_NOTIFICAR_CONFIGURACION_CREADA, compression))).addMethod(itson.mx.alarmevaluatorservice.grpc.AlarmaNotificacionServiceGrpc.getObtenerConfiguracionesActivasMethod(), asyncUnaryCall(new MethodHandlers<itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest, itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse>(this, METHODID_OBTENER_CONFIGURACIONES_ACTIVAS, compression))).build();
        }
    }

    private static final int METHODID_NOTIFICAR_CONFIGURACION_CREADA = 0;

    private static final int METHODID_OBTENER_CONFIGURACIONES_ACTIVAS = 1;

    private static final class MethodHandlers<Req, Resp> implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>, io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

        private final AlarmaNotificacionServiceImplBase serviceImpl;

        private final int methodId;

        private final String compression;

        MethodHandlers(AlarmaNotificacionServiceImplBase serviceImpl, int methodId, String compression) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
            this.compression = compression;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                case METHODID_NOTIFICAR_CONFIGURACION_CREADA:
                    io.quarkus.grpc.stubs.ServerCalls.oneToOne((itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest) request, (io.grpc.stub.StreamObserver<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse>) responseObserver, compression, serviceImpl::notificarConfiguracionCreada);
                    break;
                case METHODID_OBTENER_CONFIGURACIONES_ACTIVAS:
                    io.quarkus.grpc.stubs.ServerCalls.oneToOne((itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest) request, (io.grpc.stub.StreamObserver<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse>) responseObserver, compression, serviceImpl::obtenerConfiguracionesActivas);
                    break;
                default:
                    throw new java.lang.AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                default:
                    throw new java.lang.AssertionError();
            }
        }
    }
}
