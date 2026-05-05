package entidades;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;
import java.time.Instant;
import java.util.List;

/**
 * Documento MongoDB que almacena una lectura completa de un sensor.
 * Cada documento contiene múltiples mediciones del mismo evento.
 *
 * @author janot
 */
@MongoEntity(collection="lecturas_invernadero")
public class SensorData {

    public ObjectId id;

    // Campos del evento
    public String sensorId;          // ID del sensor que emitió la lectura
    public Instant timestamp;        // Timestamp del evento (NO autogenerado)

    // Mediciones del sensor
    public List<Medicion> mediciones; // Array de mediciones del sensor

    // Campo opcional para debugging y auditoría
    public String timestampSource;    // "EVENT" o "AUTO_GENERATED"

    // Campos usados por documentos historicos planos ya existentes en MongoDB
    public Instant fecha;
    public String tipoSensor;
    public String unidad;
    public Double valor;
}
