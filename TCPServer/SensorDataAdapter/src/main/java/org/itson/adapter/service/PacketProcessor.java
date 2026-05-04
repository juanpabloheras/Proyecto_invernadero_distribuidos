package org.itson.adapter.service;

import SensorAdapter.ISensorAdapter;
import SensorAdapter.SensorAdapterA;
import SensorAdapter.SensorAdapterB;
import SensorAdapter.SensorAdapterC;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.itson.adapter.messaging.RabbitMQPublisher;
import org.itson.adapter.model.SensorEvent;
import java.util.UUID;

import java.util.HashMap;
import java.util.Map;

/**
 * Procesador de paquetes binarios Convierte los paquetes binarios del TcpSensorServer al formato canónico SensorEvent Detecta automáticamente el tipo de protocolo y usa el adapter correspondiente
 *
 * @author Sistema Invernadero Distribuido
 */
@ApplicationScoped
public class PacketProcessor {

    @Inject
    RabbitMQPublisher rabbitMQPublisher;

    // HashMap de las diferentes clases de adapter que tiene
    // La llave es el tipo de protocolo que se usa en el sensor para usar el adapter de ese protocolo
    private Map<String, ISensorAdapter> adapters = new HashMap<>();

    /**
     * Inicializa los adapters para cada tipo de protocolo La llave es el byte tipo_sensor en header[2] como string hexadecimal - "02" → Protocolo B (0x02) - "03" → Protocolo C (0x03) - "default" → Protocolo A (sin tipo_sensor o valor diferente)
     */
    @PostConstruct
    void init() {
        adapters.put("02", new SensorAdapterB());  // 0x02 en header[2]
        adapters.put("03", new SensorAdapterC());  // 0x03 en header[2]
        adapters.put("default", new SensorAdapterA());  // Para A u otros

        System.out.println("[PACKET PROCESSOR] Adapters inicializados: 02->B, 03->C, default->A");
    }

    /**
     * Procesa un paquete binario usando HashMap para buscar el adapter
     *
     * @param data Paquete binario recibido
     * @return SensorEvent en formato canónico
     */
    public SensorEvent processPacket(byte[] data) {
        try {
            // Obtener llave del header[2]
            String key = obtenerKeyDeHeader(data);

            // Buscar adapter directamente en HashMap
            ISensorAdapter adapter = adapters.getOrDefault(key, adapters.get("default"));

            System.out.println("[DETECCION] Protocolo detectado: " + adapter.getTipo() + " (key: " + key + ")");

            // Adaptar paquete a SensorEvent
            SensorEvent event = adapter.adaptar(data);

            // Generar eventId único
            event.setEventId(generarEventId());

            // Generar JSON
            String eventJson = event.toJson();

            // Mostrar evento y JSON
            System.out.println("\n[EVENTO CANONICO] " + event);
            System.out.println("\n[JSON]");
            System.out.println(eventJson);
            System.out.println();

            // Publicar a RabbitMQ usando eventType como routing key
            rabbitMQPublisher.publishEvent(eventJson, event.getEventType());

            return event;

        } catch (Exception e) {
            System.err.println("[ERROR] Error al procesar paquete: " + e.getMessage());
            e.printStackTrace();
            return crearEventoError();
        }
    }

    /**
     * Obtiene la llave del HashMap a partir del byte header[2] Convierte el byte tipo_sensor a string hexadecimal (sin "0x")
     *
     * @param data Paquete binario
     * @return Llave para buscar en el HashMap (ej: "02", "03", "default")
     */
    private String obtenerKeyDeHeader(byte[] data) {
        if (data == null || data.length < 3) {
            return "default";
        }

        // Convertir byte header[2] a string hexadecimal (2 dígitos, uppercase)
        return String.format("%02X", data[2]);
    }

    //id de eventos para no repetir en rabbitMQ puede crear duplicados
    private String generarEventId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Crea un evento de error cuando falla el procesamiento
     *
     * @return SensorEvent con datos de error
     */
    private SensorEvent crearEventoError() {
        SensorEvent event = new SensorEvent();
        event.setEventId(generarEventId());
        event.setSensorId("ERROR");
        event.setEventType("errorEvent");
        return event;
    }
}
