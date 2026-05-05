package itson.mx.alarmevaluatorservice.cache;

import itson.mx.alarmevaluatorservice.grpc.ConfiguracionAlarma;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caché en memoria de configuraciones de alarma activas, indexadas por tipoAlarma.
 *
 * Lo escriben:
 *   - ConfiguracionCargador (carga inicial al arrancar)
 *   - El gRPC server cuando llega NotificarConfiguracionCreada
 *
 * Lo leen:
 *   - LecturaConsumer (en cada lectura entrante)
 *   - El gRPC server cuando le piden ObtenerConfiguracionesActivas
 */
@ApplicationScoped
public class ConfiguracionCache {

    private final ConcurrentHashMap<String, List<ConfiguracionAlarma>> porTipo = new ConcurrentHashMap<>();

    public void reemplazarTodo(List<ConfiguracionAlarma> configuraciones) {
        // Construye la nueva agrupación en local y la pasa al map principal recién al final,
        // con cada lista interna ya inmutable. Así un lector que esté iterando durante un
        // reemplazo no ve estructuras a medio armar.
        // Las keys se guardan en mayúsculas para que el lookup sea case-insensitive
        // (la config viene como "TEMPERATURA" y la medición como "temperatura").
        Map<String, List<ConfiguracionAlarma>> nuevo = new HashMap<>();
        for (ConfiguracionAlarma c : configuraciones) {
            if (!c.getActiva()) {
                continue;
            }
            nuevo.computeIfAbsent(normalizar(c.getTipoAlarma()), k -> new ArrayList<>()).add(c);
        }
        porTipo.clear();
        for (Map.Entry<String, List<ConfiguracionAlarma>> e : nuevo.entrySet()) {
            porTipo.put(e.getKey(), List.copyOf(e.getValue()));
        }
    }

    public List<ConfiguracionAlarma> obtenerPorTipo(String tipoAlarma) {
        List<ConfiguracionAlarma> lista = porTipo.get(normalizar(tipoAlarma));
        return lista == null ? Collections.emptyList() : lista;
    }

    private static String normalizar(String tipoAlarma) {
        return tipoAlarma == null ? "" : tipoAlarma.trim().toUpperCase();
    }

    public List<ConfiguracionAlarma> obtenerTodas() {
        List<ConfiguracionAlarma> resultado = new ArrayList<>();
        for (List<ConfiguracionAlarma> lista : porTipo.values()) {
            resultado.addAll(lista);
        }
        return resultado;
    }

    public int tamanio() {
        int total = 0;
        for (List<ConfiguracionAlarma> lista : porTipo.values()) {
            total += lista.size();
        }
        return total;
    }
}
