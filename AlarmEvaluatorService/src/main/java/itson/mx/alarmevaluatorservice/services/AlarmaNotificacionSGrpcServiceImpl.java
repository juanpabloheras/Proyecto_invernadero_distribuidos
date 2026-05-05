/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.mx.alarmevaluatorservice.services;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import itson.mx.alarmevaluatorservice.grpc.AlarmaNotificacionService;
import itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarmaCreadaRequest;
import itson.mx.alarmevaluatorservice.grpc.ConfiguracionesResponse;
import itson.mx.alarmevaluatorservice.grpc.NotificacionResponse;
import itson.mx.alarmevaluatorservice.grpc.ObtenerConfiguracionesRequest;

/**
 *
 * @author chris
 */
@GrpcService
public class AlarmaNotificacionSGrpcServiceImpl implements AlarmaNotificacionService {

    @Override
    public Uni<NotificacionResponse> notificarConfiguracionCreada(ConfiguracionAlarmaCreadaRequest request) {

        System.out.println("====================================");
        System.out.println("LLEGÓ NOTIFICACIÓN DESDE EL BACKEND");
        System.out.println("ID: " + request.getIdConfiguracionAlarma());
        System.out.println("Nombre: " + request.getNombreAlarma());
        System.out.println("Tipo alarma: " + request.getTipoAlarma());
        System.out.println("Operador: " + request.getOperador());
        System.out.println("Valor crítico: " + request.getValorCritico());
        System.out.println("Activa: " + request.getActiva());
        System.out.println("====================================");

        return Uni.createFrom().item(
                NotificacionResponse.newBuilder()
                        .setOk(true)
                        .setMessage("Configuración recibida en AlarmEvaluatorService")
                        .build()
        );
    }

    @Override
    public Uni<ConfiguracionesResponse> obtenerConfiguracionesActivas(ObtenerConfiguracionesRequest request) {

        System.out.println("====================================");
        System.out.println("ALGUIEN PIDIO CONFIGURACIONES ACTIVAS");
        System.out.println("====================================");

        return Uni.createFrom().item(
                ConfiguracionesResponse.newBuilder()
                        .build()
        );
    }
}