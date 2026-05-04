package service;

import entidades.SensorData;
import entidades.Medicion;
import repository.SensorDataRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;

/**
 * Servicio de negocio para manejo de datos históricos de sensores.
 * Centraliza validaciones y lógica de persistencia.
 *
 * @author janot
 */
@ApplicationScoped
public class SensorDataService {

    @Inject
    SensorDataRepository repository;

    /**
     * Guarda una lectura completa de sensor en MongoDB.
     *
     * @param sensorId ID del sensor que emitió la lectura
     * @param timestamp Timestamp del evento (puede ser null)
     * @param mediciones Lista de mediciones (válidas e inválidas)
     */
    public void guardarLectura(
        String sensorId,
        Instant timestamp,
        List<Medicion> mediciones
    ) {


        if (sensorId == null || sensorId.isBlank()) {
            System.err.println("[ERROR] sensorId es requerido, no se guardará");
            return;
        }

        if (mediciones == null || mediciones.isEmpty()) {
            System.err.println("[ERROR] No hay mediciones para guardar");
            return;
        }

        // Determinar timestamp final
        Instant timestampFinal;
        String timestampSource;

        if (timestamp != null) {
            timestampFinal = timestamp;
            timestampSource = "EVENT";
        } else {
            timestampFinal = Instant.now();
            timestampSource = "AUTO_GENERATED";
            System.err.println("[WARN] Usando timestamp autogenerado para evento ");
        }

        // Armar el documento MongoDB
        SensorData dato = new SensorData();
        dato.sensorId = sensorId;
        dato.timestamp = timestampFinal;
        dato.mediciones = mediciones;
        dato.timestampSource = timestampSource;

        // Persistir en MongoDB
        repository.persist(dato);

        // Logging con métricas de calidad
        long validas = mediciones.stream().filter(m -> Boolean.TRUE.equals(m.esValido)).count();
        long invalidas = mediciones.size() - validas;

        System.out.printf(
            " [SERVICIO] Guardado exitoso%n" +
            "   Sensor ID: %s%n" +
            "   Timestamp: %s (%s)%n" +
            "   Mediciones: %d válidas, %d inválidas%n",
             sensorId, timestampFinal, timestampSource, validas, invalidas
        );
    }
}