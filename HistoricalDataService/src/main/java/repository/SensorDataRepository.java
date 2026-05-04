package repository;

import entidades.SensorData;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 *
 * @author janot
 */
@ApplicationScoped
public class SensorDataRepository implements PanacheMongoRepository<SensorData> {
    
}