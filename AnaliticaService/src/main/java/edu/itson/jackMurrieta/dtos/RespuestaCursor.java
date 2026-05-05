package edu.itson.jackMurrieta.dtos;

import java.util.List;

public class RespuestaCursor<T> {

    public List<T> data;
    public Paginacion paginacion;
    public Meta meta;

    public RespuestaCursor(List<T> data, String siguienteCursor, int limit, boolean hayMas,
                           String desde, String hasta) {
        this.data = data;
        this.paginacion = new Paginacion(siguienteCursor, limit, hayMas);
        this.meta = new Meta(desde, hasta);
    }

    public static class Paginacion {
        public String siguienteCursor;
        public int limit;
        public boolean hayMas;

        public Paginacion(String siguienteCursor, int limit, boolean hayMas) {
            this.siguienteCursor = siguienteCursor;
            this.limit = limit;
            this.hayMas = hayMas;
        }
    }

    public static class Meta {
        public RangoConsultado rangoConsultado;

        public Meta(String desde, String hasta) {
            this.rangoConsultado = new RangoConsultado(desde, hasta);
        }

        public static class RangoConsultado {
            public String desde;
            public String hasta;

            public RangoConsultado(String desde, String hasta) {
                this.desde = desde;
                this.hasta = hasta;
            }
        }
    }
}
