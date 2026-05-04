package entidades;

/**
 * Representa una medición individual de sensor.
 * Almacena tanto el valor original (auditoría) como el parseado (análisis).
 */
public class Medicion {
    public String tipo;              // "Temperatura", "Humedad", etc.
    public String valorOriginal;     // "25.5°C" - texto exacto del sensor
    public Double valorNumerico;     // 25.5 - valor parseado para análisis
    public String unidad;            // "°C", "%", etc.
    public Boolean esValido;         // true si se pudo parsear correctamente

    // Constructor vacío para Panache/Jackson
    public Medicion() {}

    // Constructor para parseo exitoso
    public Medicion(String tipo, String valorOriginal, Double valorNumerico, String unidad) {
        this.tipo = tipo;
        this.valorOriginal = valorOriginal;
        this.valorNumerico = valorNumerico;
        this.unidad = unidad;
        this.esValido = true;
    }

    // Factory method para casos de error
    public static Medicion invalida(String tipo, String valorOriginal) {
        Medicion m = new Medicion();
        m.tipo = tipo != null ? tipo : "DESCONOCIDO";
        m.valorOriginal = valorOriginal;
        m.valorNumerico = null;
        m.unidad = null;
        m.esValido = false;
        return m;
    }
}
