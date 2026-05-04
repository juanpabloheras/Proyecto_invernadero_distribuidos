package entidades;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;
import java.time.Instant;

/**
 *
 * @author janot
 */
@MongoEntity(collection="lecturas_invernadero")
public class SensorData {
    
    public ObjectId id; 
    
    public String tipoSensor;
    public double valor;
    public String unidad;
    public Instant fecha;
}