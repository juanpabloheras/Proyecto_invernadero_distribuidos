package service;

import entidades.SensorData;
import repository.SensorDataRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;

/**
 *
 * @author janot
 */
@ApplicationScoped
public class SensorDataService {

    @Inject
    SensorDataRepository repository;

    public void procesarYGuardar(String tipo, double valor, String unidad) {
        // 1. Armamos el objeto puro
        SensorData dato = new SensorData();
        dato.tipoSensor = tipo;
        dato.valor = valor;
        dato.unidad = unidad;
        dato.fecha = Instant.now();
        
        // 2. Delegamos la tarea de guardado al Repositorio
        repository.persist(dato); 
        
        System.out.println("💾 [SERVICIO] Guardado en Mongo -> " + tipo + ": " + valor + " " + unidad);
    }
}