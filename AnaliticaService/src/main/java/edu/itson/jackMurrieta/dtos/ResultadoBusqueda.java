package edu.itson.jackMurrieta.dtos;

import edu.itson.jackMurrieta.entidades.SensorData;

import java.util.List;

public class ResultadoBusqueda {
    public final List<SensorData> datos;
    public final String siguienteCursor;
    public final boolean hayMas;

    public ResultadoBusqueda(List<SensorData> datos, String siguienteCursor, boolean hayMas) {
        this.datos = datos;
        this.siguienteCursor = siguienteCursor;
        this.hayMas = hayMas;
    }
}
