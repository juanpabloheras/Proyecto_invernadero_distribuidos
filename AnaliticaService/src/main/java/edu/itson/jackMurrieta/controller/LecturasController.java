package edu.itson.jackMurrieta.controller;

import edu.itson.jackMurrieta.dtos.RespuestaCursor;
import edu.itson.jackMurrieta.entidades.SensorData;
import edu.itson.jackMurrieta.service.LecturasService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/v1/lecturas")
@Produces(MediaType.APPLICATION_JSON)
public class LecturasController {

    @Inject
    LecturasService service;

    /**
     * Lecturas crudas con filtros y paginación por cursor.
     * Parámetros opcionales: desde, hasta (ISO 8601), sensorId (comma-sep),
     * tipo, soloValidos, formato (plano), limit (max 10000), cursor.
     */
    @GET
    public Response obtenerLecturas(
            @QueryParam("desde") String desde,
            @QueryParam("hasta") String hasta,
            @QueryParam("sensorId") String sensorId,
            @QueryParam("tipo") String tipo,
            @DefaultValue("false") @QueryParam("soloValidos") boolean soloValidos,
            @QueryParam("formato") String formato,
            @DefaultValue("100") @QueryParam("limit") int limit,
            @QueryParam("cursor") String cursor) {

        try {
            boolean formatoPlano = "plano".equalsIgnoreCase(formato);
            RespuestaCursor<SensorData> resultado = service.obtenerLecturas(
                desde, hasta, sensorId, tipo, soloValidos, formatoPlano, limit, cursor);
            return Response.ok(resultado).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage())).build();
        }
    }

    /**
     * Métricas calculadas agrupadas por intervalo de tiempo.
     * intervalo: minuto | hora (default) | dia | semana | mes
     * metricas: promedio,min,max,count,desviacion (comma-sep, default: todos)
     */
    @GET
    @Path("/agregado")
    public Response obtenerAgregado(
            @QueryParam("desde") String desde,
            @QueryParam("hasta") String hasta,
            @QueryParam("sensorId") String sensorId,
            @QueryParam("tipo") String tipo,
            @DefaultValue("false") @QueryParam("soloValidos") boolean soloValidos,
            @DefaultValue("hora") @QueryParam("intervalo") String intervalo,
            @QueryParam("metricas") String metricas) {

        try {
            return Response.ok(service.obtenerAgregado(
                desde, hasta, sensorId, tipo, soloValidos, intervalo, metricas)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage())).build();
        }
    }

    /**
     * Última lectura de cada sensor (estado actual del invernadero).
     */
    @GET
    @Path("/ultima")
    public Response obtenerUltima() {
        return Response.ok(service.obtenerUltimaPorSensor()).build();
    }

    /**
     * Catálogo de sensores existentes en la colección.
     */
    @GET
    @Path("/sensores")
    public Response obtenerSensores() {
        return Response.ok(service.obtenerSensores()).build();
    }

    /**
     * Catálogo de tipos de medición disponibles.
     */
    @GET
    @Path("/tipos-medicion")
    public Response obtenerTiposMedicion() {
        return Response.ok(service.obtenerTiposMedicion()).build();
    }
}
