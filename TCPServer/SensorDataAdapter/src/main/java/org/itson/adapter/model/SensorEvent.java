package org.itson.adapter.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Formato canónico de eventos para el sistema de invernadero Esta clase representa el formato estándar de todos los eventos de sensores Los eventos se serializan en JSON usando Gson
 *
 * @author Sistema Invernadero Distribuido
 */
public class SensorEvent {

    private String eventId;
    private int version;
    private String eventType = "sensor.lectura.registrada";
    private Timestamp timestamp;
    private String sensorId;
    private ArrayList<String> mediciones;

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();

    /**
     * Constructor por defecto
     */
    public SensorEvent() {
        this.version = 1;
        this.eventType = "sensor.lectura.registrada";
        this.mediciones = new ArrayList<>();
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Constructor completo
     */
    public SensorEvent(String eventId, int version, String eventType, Timestamp timestamp,
            String sensorId, ArrayList<String> mediciones) {
        this.eventId = eventId;
        this.version = version;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.sensorId = sensorId;
        this.mediciones = mediciones != null ? mediciones : new ArrayList<>();
    }

    // Getters y Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public ArrayList<String> getMediciones() {
        return mediciones;
    }

    public void setMediciones(ArrayList<String> mediciones) {
        this.mediciones = mediciones;
    }

    /**
     * Agrega una medición al ArrayList
     *
     * @param medicion Medición en formato "tipo:valor unidad"
     */
    public void agregarMedicion(String medicion) {
        if (this.mediciones == null) {
            this.mediciones = new ArrayList<>();
        }
        this.mediciones.add(medicion);
    }

    /**
     * Serializa el evento a JSON usando Gson
     *
     * @return String JSON del evento
     */
    public String toJson() {
        return gson.toJson(this);
    }

    /**
     * Deserializa un JSON a un objeto SensorEvent
     *
     * @param json String JSON
     * @return SensorEvent deserializado
     */
    public static SensorEvent fromJson(String json) {
        return gson.fromJson(json, SensorEvent.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SensorEvent that = (SensorEvent) o;
        return eventId == that.eventId
                && version == that.version
                && Objects.equals(sensorId, that.sensorId)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, version, sensorId, timestamp);
    }

    @Override
    public String toString() {
        return "SensorEvent{"
                + "eventId=" + eventId
                + ", version=" + version
                + ", eventType='" + eventType + '\''
                + ", timestamp=" + timestamp
                + ", sensorId='" + sensorId + '\''
                + ", mediciones=" + mediciones
                + '}';
    }
}
