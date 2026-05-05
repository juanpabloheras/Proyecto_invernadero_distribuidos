package repository;

import entidades.SensorData;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author janot
 */
@ApplicationScoped
public class SensorDataRepository implements PanacheMongoRepository<SensorData> {
    
    public PanacheQuery<SensorData> buscarPorSensor(String sensorId) {
        return find("sensorId", sensorId);
    }
    
}