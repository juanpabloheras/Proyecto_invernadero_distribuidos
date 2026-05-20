package org.itson.adapter.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Formato canónico de eventos para el sistema de invernadero.
 * Todos los eventos de sensores se normalizan a esta estructura antes de publicarse.
 * Serialización a JSON mediante Gson.
 *
 * @author Sistema Invernadero Distribuido
 */
public class SensorEvent {

    private String eventId;
    private int version;
    private String eventType = "sensor.lectura.registrada";
    private Timestamp timestamp;
    private String sensorId;
    private List<String> mediciones;

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();

    public SensorEvent() {
        this.version = 1;
        this.eventType = "sensor.lectura.registrada";
        this.mediciones = new ArrayList<>();
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public SensorEvent(String eventId, int version, String eventType, Timestamp timestamp,
            String sensorId, List<String> mediciones) {
        this.eventId = eventId;
        this.version = version;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.sensorId = sensorId;
        this.mediciones = mediciones != null ? mediciones : new ArrayList<>();
    }

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

    public List<String> getMediciones() {
        return mediciones;
    }

    public void setMediciones(List<String> mediciones) {
        this.mediciones = mediciones;
    }

    public void agregarMedicion(String medicion) {
        if (this.mediciones == null) {
            this.mediciones = new ArrayList<>();
        }
        this.mediciones.add(medicion);
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static SensorEvent fromJson(String json) {
        return gson.fromJson(json, SensorEvent.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorEvent that = (SensorEvent) o;
        return version == that.version
                && Objects.equals(eventId, that.eventId)
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
