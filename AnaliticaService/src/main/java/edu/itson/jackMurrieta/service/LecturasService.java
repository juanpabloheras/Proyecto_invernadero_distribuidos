package edu.itson.jackMurrieta.service;

import edu.itson.jackMurrieta.dtos.ResultadoBusqueda;
import edu.itson.jackMurrieta.dtos.RespuestaCursor;
import edu.itson.jackMurrieta.entidades.SensorData;
import edu.itson.jackMurrieta.repository.LecturasRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class LecturasService {

    @Inject
    LecturasRepository repository;

    public RespuestaCursor<SensorData> obtenerLecturas(
            String desde, String hasta, String sensorIdParam,
            String tipo, boolean soloValidos, boolean formatoPlano,
            int limit, String cursor) {

        limit = Math.min(Math.max(1, limit), 10000);

        // Rango por defecto: últimas 24 horas si no se especifica ni cursor
        if (desde == null && hasta == null && cursor == null) {
            Instant ahora = Instant.now();
            hasta = ahora.toString();
            desde = ahora.minus(24, ChronoUnit.HOURS).toString();
        }

        List<String> sensorIds = parseSensorIds(sensorIdParam);
        Boolean filtroValidos = soloValidos ? true : null;

        ResultadoBusqueda resultado = repository.buscarConFiltros(
            desde, hasta, sensorIds, tipo, filtroValidos, formatoPlano, limit, cursor);

        return new RespuestaCursor<>(resultado.datos, resultado.siguienteCursor, limit, resultado.hayMas, desde, hasta);
    }

    public List<Document> obtenerAgregado(
            String desde, String hasta, String sensorIdParam,
            String tipo, boolean soloValidos,
            String intervalo, String metricasParam) {

        List<String> sensorIds = parseSensorIds(sensorIdParam);
        List<String> metricas = (metricasParam != null && !metricasParam.isBlank())
            ? Arrays.asList(metricasParam.split(","))
            : null;
        Boolean filtroValidos = soloValidos ? true : null;

        return repository.obtenerAgregado(desde, hasta, sensorIds, tipo, filtroValidos, intervalo, metricas);
    }

    public List<SensorData> obtenerUltimaPorSensor() {
        return repository.obtenerUltimaPorSensor();
    }

    public List<String> obtenerSensores() {
        return repository.obtenerSensores();
    }

    public List<String> obtenerTiposMedicion() {
        return repository.obtenerTiposMedicion();
    }

    private List<String> parseSensorIds(String param) {
        if (param == null || param.isBlank()) return null;
        return Arrays.asList(param.split(","));
    }
}
