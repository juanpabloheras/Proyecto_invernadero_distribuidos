package controller;

import entidades.SensorData;
import dtos.RespuestaPaginada;
import service.SensorDataService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 *
 * @author janot
 */
@Path("/api/lecturas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorDataController {

    @Inject
    SensorDataService sensorService;

    // 1. Obtener TODO (Ej. /api/lecturas?pagina=0&size=10)
    @GET
    public Response obtenerTodas(
            @DefaultValue("0") @QueryParam("pagina") int pagina,
            @DefaultValue("10") @QueryParam("size") int size) {

        pagina = validarPagina(pagina);
        size = validarSize(size);

        RespuestaPaginada<SensorData> resultado = sensorService.obtenerHistorialPaginado(pagina, size);
        return Response.ok(resultado).build();
    }

    // 2. Obtener por ID de Sensor (Ej. /api/lecturas/sensor/esp32-zona1?pagina=0&size=10)
    @GET
    @Path("/sensor/{sensorId}")
    public Response obtenerPorSensor(
            @PathParam("sensorId") String sensorId,
            @DefaultValue("0") @QueryParam("pagina") int pagina,
            @DefaultValue("10") @QueryParam("size") int size) {

        pagina = validarPagina(pagina);
        size = validarSize(size);

        RespuestaPaginada<SensorData> resultado = sensorService.obtenerHistorialPorSensor(sensorId, pagina, size);
        return Response.ok(resultado).build();
    }
    
    // 3. NUEVO: Obtener TODO en un rango de fechas (Ej. /api/lecturas/fechas?inicio=2026-05-01T00:00:00Z&fin=2026-05-05T23:59:59Z)
    @GET
    @Path("/fechas")
    public Response obtenerPorRangoFechas(
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fin") String finStr,
            @DefaultValue("0") @QueryParam("pagina") int pagina,
            @DefaultValue("10") @QueryParam("size") int size) {

        try {
            Instant inicio = Instant.parse(inicioStr);
            Instant fin = Instant.parse(finStr);
            pagina = validarPagina(pagina);
            size = validarSize(size);

            RespuestaPaginada<SensorData> resultado = sensorService.obtenerHistorialPorRangoFechas(inicio, fin, pagina, size);
            return Response.ok(resultado).build();

        } catch (DateTimeParseException | NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Formato de fecha inválido o ausente. Usa ISO-8601 (ej. 2026-05-01T00:00:00Z)\"}")
                    .build();
        }
    }

    // 4. NUEVO: Obtener de un SENSOR en un rango de fechas (El que usarán para las gráficas BI)
    @GET
    @Path("/sensor/{sensorId}/fechas")
    public Response obtenerPorSensorYFechas(
            @PathParam("sensorId") String sensorId,
            @QueryParam("inicio") String inicioStr,
            @QueryParam("fin") String finStr,
            @DefaultValue("0") @QueryParam("pagina") int pagina,
            @DefaultValue("10") @QueryParam("size") int size) {

        try {
            Instant inicio = Instant.parse(inicioStr);
            Instant fin = Instant.parse(finStr);
            pagina = validarPagina(pagina);
            size = validarSize(size);

            RespuestaPaginada<SensorData> resultado = sensorService.obtenerHistorialPorSensorYFechas(sensorId, inicio, fin, pagina, size);
            return Response.ok(resultado).build();

        } catch (DateTimeParseException | NullPointerException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Formato de fecha inválido o ausente. Usa ISO-8601 (ej. 2026-05-01T00:00:00Z)\"}")
                    .build();
        }
    }

    // --- Métodos de seguridad ---
    private int validarPagina(int pagina) {
        return Math.max(pagina, 0); // Evita páginas negativas
    }

    private int validarSize(int size) {
        if (size <= 0) {
            return 10;
        }
        if (size > 100) {
            return 100; // Límite de 100 registros por consulta para no tumbar el backend
        }
        return size;
    }
}
