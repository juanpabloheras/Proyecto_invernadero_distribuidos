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

    /**
     * Inserta una config nueva o actualiza una existente (por idConfiguracionAlarma).
     * Si la config llega con activa=false, queda removida del caché.
     *
     * Es seguro llamarlo mientras LecturaConsumer está leyendo: las listas internas
     * son inmutables, y el reemplazo en el map es atómico (ConcurrentHashMap.put).
     */
    public void agregarOActualizar(ConfiguracionAlarma config) {
        int id = config.getIdConfiguracionAlarma();

        // 1) Remover el id de cualquier lista donde estuviera.
        //    Cubre el caso "ya existía" y el caso "cambió de tipoAlarma".
        for (Map.Entry<String, List<ConfiguracionAlarma>> e : porTipo.entrySet()) {
            List<ConfiguracionAlarma> sinEsteId = new ArrayList<>();
            for (ConfiguracionAlarma c : e.getValue()) {
                if (c.getIdConfiguracionAlarma() != id) {
                    sinEsteId.add(c);
                }
            }
            if (sinEsteId.size() != e.getValue().size()) {
                if (sinEsteId.isEmpty()) {
                    porTipo.remove(e.getKey());
                } else {
                    porTipo.put(e.getKey(), List.copyOf(sinEsteId));
                }
            }
        }

        // 2) Si llegó inactiva, ya quedó fuera. Terminamos.
        if (!config.getActiva()) {
            return;
        }

        // 3) Activa: la agregamos a la lista de su tipo (key en MAYÚSCULAS).
        String key = normalizar(config.getTipoAlarma());
        List<ConfiguracionAlarma> actual = porTipo.get(key);
        List<ConfiguracionAlarma> nueva = new ArrayList<>();
        if (actual != null) {
            nueva.addAll(actual);
        }
        nueva.add(config);
        porTipo.put(key, List.copyOf(nueva));
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
