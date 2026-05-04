package itson.mx.alarmevaluatorservice.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.quarkus.Generated(value = "by gRPC proto compiler (version 1.65.1)", comments = "Source: alarma-notificacion.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AlarmaNotificacionServiceGrpc {

    private AlarmaNotificacionServiceGrpc() {
    }

    public static final java.lang.String SERVICE_NAME = "alarmas.AlarmaNotificacionService";

    // Static method descriptors that strictly reflect the proto.
    private static volatile io.grpc.MethodDescriptor<itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest, itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> getNotificarConfiguracionCreadaMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "NotificarConfiguracionCreada", requestType = itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest.class, responseType = itson.mx.alarmevaluatorservice.grpc.NotificacionResponse.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest, itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> getNotificarConfiguracionCreadaMethod() {
        io.grpc.MethodDescriptor<itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest, itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> getNotificarConfiguracionCreadaMethod;
        if ((getNotificarConfiguracionCreadaMethod = AlarmaNotificacionServiceGrpc.getNotificarConfiguracionCreadaMethod) == null) {
            synchronized (AlarmaNotificacionServiceGrpc.class) {
                if ((getNotificarConfiguracionCreadaMethod = AlarmaNotificacionServiceGrpc.getNotificarConfiguracionCreadaMethod) == null) {
                    AlarmaNotificacionServiceGrpc.getNotificarConfiguracionCreadaMethod = getNotificarConfiguracionCreadaMethod = io.grpc.MethodDescriptor.<itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest, itson.mx.alarmevaluatorservice.grpc.NotificacionResponse>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName(SERVICE_NAME, "NotificarConfiguracionCreada")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(itson.mx.alarmevaluatorservice.grpc.NotificacionResponse.getDefaultInstance())).setSchemaDescriptor(new AlarmaNotificacionServiceMethodDescriptorSupplier("NotificarConfiguracionCreada")).build();
                }
            }
        }
        return getNotificarConfiguracionCreadaMethod;
    }

    private static volatile io.grpc.MethodDescriptor<itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest, itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> getObtenerConfiguracionesActivasMethod;

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "ObtenerConfiguracionesActivas", requestType = itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest.class, responseType = itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest, itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> getObtenerConfiguracionesActivasMethod() {
        io.grpc.MethodDescriptor<itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest, itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> getObtenerConfiguracionesActivasMethod;
        if ((getObtenerConfiguracionesActivasMethod = AlarmaNotificacionServiceGrpc.getObtenerConfiguracionesActivasMethod) == null) {
            synchronized (AlarmaNotificacionServiceGrpc.class) {
                if ((getObtenerConfiguracionesActivasMethod = AlarmaNotificacionServiceGrpc.getObtenerConfiguracionesActivasMethod) == null) {
                    AlarmaNotificacionServiceGrpc.getObtenerConfiguracionesActivasMethod = getObtenerConfiguracionesActivasMethod = io.grpc.MethodDescriptor.<itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest, itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName(SERVICE_NAME, "ObtenerConfiguracionesActivas")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse.getDefaultInstance())).setSchemaDescriptor(new AlarmaNotificacionServiceMethodDescriptorSupplier("ObtenerConfiguracionesActivas")).build();
                }
            }
        }
        return getObtenerConfiguracionesActivasMethod;
    }

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static AlarmaNotificacionServiceStub newStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<AlarmaNotificacionServiceStub> factory = new io.grpc.stub.AbstractStub.StubFactory<AlarmaNotificacionServiceStub>() {

            @java.lang.Override
            public AlarmaNotificacionServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new AlarmaNotificacionServiceStub(channel, callOptions);
            }
        };
        return AlarmaNotificacionServiceStub.newStub(factory, channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static AlarmaNotificacionServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<AlarmaNotificacionServiceBlockingStub> factory = new io.grpc.stub.AbstractStub.StubFactory<AlarmaNotificacionServiceBlockingStub>() {

            @java.lang.Override
            public AlarmaNotificacionServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new AlarmaNotificacionServiceBlockingStub(channel, callOptions);
            }
        };
        return AlarmaNotificacionServiceBlockingStub.newStub(factory, channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static AlarmaNotificacionServiceFutureStub newFutureStub(io.grpc.Channel channel) {
        io.grpc.stub.AbstractStub.StubFactory<AlarmaNotificacionServiceFutureStub> factory = new io.grpc.stub.AbstractStub.StubFactory<AlarmaNotificacionServiceFutureStub>() {

            @java.lang.Override
            public AlarmaNotificacionServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
                return new AlarmaNotificacionServiceFutureStub(channel, callOptions);
            }
        };
        return AlarmaNotificacionServiceFutureStub.newStub(factory, channel);
    }

    /**
     */
    public interface AsyncService {

        /**
         */
        default void notificarConfiguracionCreada(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest request, io.grpc.stub.StreamObserver<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getNotificarConfiguracionCreadaMethod(), responseObserver);
        }

        /**
         */
        default void obtenerConfiguracionesActivas(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest request, io.grpc.stub.StreamObserver<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> responseObserver) {
            io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getObtenerConfiguracionesActivasMethod(), responseObserver);
        }
    }

    /**
     * Base class for the server implementation of the service AlarmaNotificacionService.
     */
    public static abstract class AlarmaNotificacionServiceImplBase implements io.grpc.BindableService, AsyncService {

        @java.lang.Override
        public io.grpc.ServerServiceDefinition bindService() {
            return AlarmaNotificacionServiceGrpc.bindService(this);
        }
    }

    /**
     * A stub to allow clients to do asynchronous rpc calls to service AlarmaNotificacionService.
     */
    public static class AlarmaNotificacionServiceStub extends io.grpc.stub.AbstractAsyncStub<AlarmaNotificacionServiceStub> {

        private AlarmaNotificacionServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected AlarmaNotificacionServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new AlarmaNotificacionServiceStub(channel, callOptions);
        }

        /**
         */
        public void notificarConfiguracionCreada(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest request, io.grpc.stub.StreamObserver<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> responseObserver) {
            io.grpc.stub.ClientCalls.asyncUnaryCall(getChannel().newCall(getNotificarConfiguracionCreadaMethod(), getCallOptions()), request, responseObserver);
        }

        /**
         */
        public void obtenerConfiguracionesActivas(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest request, io.grpc.stub.StreamObserver<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> responseObserver) {
            io.grpc.stub.ClientCalls.asyncUnaryCall(getChannel().newCall(getObtenerConfiguracionesActivasMethod(), getCallOptions()), request, responseObserver);
        }
    }

    /**
     * A stub to allow clients to do synchronous rpc calls to service AlarmaNotificacionService.
     */
    public static class AlarmaNotificacionServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<AlarmaNotificacionServiceBlockingStub> {

        private AlarmaNotificacionServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected AlarmaNotificacionServiceBlockingStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new AlarmaNotificacionServiceBlockingStub(channel, callOptions);
        }

        /**
         */
        public itson.mx.alarmevaluatorservice.grpc.NotificacionResponse notificarConfiguracionCreada(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest request) {
            return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getNotificarConfiguracionCreadaMethod(), getCallOptions(), request);
        }

        /**
         */
        public itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse obtenerConfiguracionesActivas(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest request) {
            return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getObtenerConfiguracionesActivasMethod(), getCallOptions(), request);
        }
    }

    /**
     * A stub to allow clients to do ListenableFuture-style rpc calls to service AlarmaNotificacionService.
     */
    public static class AlarmaNotificacionServiceFutureStub extends io.grpc.stub.AbstractFutureStub<AlarmaNotificacionServiceFutureStub> {

        private AlarmaNotificacionServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected AlarmaNotificacionServiceFutureStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new AlarmaNotificacionServiceFutureStub(channel, callOptions);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse> notificarConfiguracionCreada(itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest request) {
            return io.grpc.stub.ClientCalls.futureUnaryCall(getChannel().newCall(getNotificarConfiguracionCreadaMethod(), getCallOptions()), request);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse> obtenerConfiguracionesActivas(itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest request) {
            return io.grpc.stub.ClientCalls.futureUnaryCall(getChannel().newCall(getObtenerConfiguracionesActivasMethod(), getCallOptions()), request);
        }
    }

    private static final int METHODID_NOTIFICAR_CONFIGURACION_CREADA = 0;

    private static final int METHODID_OBTENER_CONFIGURACIONES_ACTIVAS = 1;

    private static final class MethodHandlers<Req, Resp> implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>, io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

        private final AsyncService serviceImpl;

        private final int methodId;

        MethodHandlers(AsyncService serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                case METHODID_NOTIFICAR_CONFIGURACION_CREADA:
                    serviceImpl.notificarConfiguracionCreada((itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest) request, (io.grpc.stub.StreamObserver<itson.mx.alarmevaluatorservice.grpc.NotificacionResponse>) responseObserver);
                    break;
                case METHODID_OBTENER_CONFIGURACIONES_ACTIVAS:
                    serviceImpl.obtenerConfiguracionesActivas((itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest) request, (io.grpc.stub.StreamObserver<itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    public static io.grpc.ServerServiceDefinition bindService(AsyncService service) {
        return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(getNotificarConfiguracionCreadaMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(new MethodHandlers<itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest, itson.mx.alarmevaluatorservice.grpc.NotificacionResponse>(service, METHODID_NOTIFICAR_CONFIGURACION_CREADA))).addMethod(getObtenerConfiguracionesActivasMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(new MethodHandlers<itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest, itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse>(service, METHODID_OBTENER_CONFIGURACIONES_ACTIVAS))).build();
    }

    private static abstract class AlarmaNotificacionServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {

        AlarmaNotificacionServiceBaseDescriptorSupplier() {
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return itson.mx.alarmevaluatorservice.grpc.AlarmaNotificacionProto.getDescriptor();
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
            return getFileDescriptor().findServiceByName("AlarmaNotificacionService");
        }
    }

    private static final class AlarmaNotificacionServiceFileDescriptorSupplier extends AlarmaNotificacionServiceBaseDescriptorSupplier {

        AlarmaNotificacionServiceFileDescriptorSupplier() {
        }
    }

    private static final class AlarmaNotificacionServiceMethodDescriptorSupplier extends AlarmaNotificacionServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {

        private final java.lang.String methodName;

        AlarmaNotificacionServiceMethodDescriptorSupplier(java.lang.String methodName) {
            this.methodName = methodName;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
            return getServiceDescriptor().findMethodByName(methodName);
        }
    }

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (AlarmaNotificacionServiceGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME).setSchemaDescriptor(new AlarmaNotificacionServiceFileDescriptorSupplier()).addMethod(getNotificarConfiguracionCreadaMethod()).addMethod(getObtenerConfiguracionesActivasMethod()).build();
                }
            }
        }
        return result;
    }
}
