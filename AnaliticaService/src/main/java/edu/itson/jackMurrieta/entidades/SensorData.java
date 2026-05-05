package edu.itson.jackMurrieta.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

public class SensorData {

    @JsonIgnore
    public ObjectId id;

    @JsonProperty("id")
    public String getId() {
        return id != null ? id.toHexString() : null;
    }

    public String sensorId;
    public Instant timestamp;
    public List<Medicion> mediciones;
    public String timestampSource;

    // Campos de documentos legacy ya existentes en MongoDB
    public Instant fecha;
    public String tipoSensor;
    public String unidad;
    public Double valor;
}
