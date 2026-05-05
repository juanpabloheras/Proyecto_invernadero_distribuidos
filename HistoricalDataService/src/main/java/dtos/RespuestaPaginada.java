package dtos;

import java.util.List;

/**
 *
 * @author janot
 */
public class RespuestaPaginada<T> {
    public List<T> datos;
    public int paginaActual;
    public int totalPaginas;
    public long totalRegistros;
}
