package repository;

import entidades.SensorData;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;

/**
 *
 * @author janot
 */
@ApplicationScoped
public class SensorDataRepository implements PanacheMongoRepository<SensorData> {
    
    public PanacheQuery<SensorData> buscarPorSensor(String sensorId) {
        return find("sensorId", sensorId);
    }
    
    public PanacheQuery<SensorData> buscarPorRangoDeFechas(Instant inicio, Instant fin) {
        return find("timestamp >= ?1 and timestamp <= ?2", inicio, fin);
    }

    public PanacheQuery<SensorData> buscarPorSensorYFechas(String sensorId, Instant inicio, Instant fin) {
        return find("sensorId = ?1 and timestamp >= ?2 and timestamp <= ?3", sensorId, inicio, fin);
    }
    
}