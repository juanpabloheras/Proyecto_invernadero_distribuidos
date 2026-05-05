package controller;

import entidades.SensorData;
import dtos.RespuestaPaginada;
import service.SensorDataService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
