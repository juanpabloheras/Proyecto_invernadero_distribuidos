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
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.UUID;

/**
 * Procesador de paquetes binarios.
 * Convierte paquetes del TcpSensorServer al formato canónico SensorEvent
 * y los publica en RabbitMQ.
 *
 * Detección de protocolo por byte[2] del header:
 *   0x02 → Protocolo B
 *   0x03 → Protocolo C
 *   otro  → Protocolo A (fallback; byte[2] es el high-byte de longitud y vale 0x00 para 36 bytes)
 *
 * @author Sistema Invernadero Distribuido
 */
@ApplicationScoped
public class PacketProcessor {

    private static final Logger LOG = Logger.getLogger(PacketProcessor.class);

    @Inject
    RabbitMQPublisher rabbitMQPublisher;

    // Inmutable: asignado una sola vez en @PostConstruct, nunca modificado.
    // Thread-safe por construcción — no requiere volatile.
    private Map<String, ISensorAdapter> adapters;

    @PostConstruct
    void init() {
        adapters = Map.of(
                "02", new SensorAdapterB(),
                "03", new SensorAdapterC(),
                "default", new SensorAdapterA()
        );
        LOG.infof("[PACKET PROCESSOR] Adapters inicializados: %s", adapters.keySet());
    }

    /**
     * Procesa un paquete binario: detecta protocolo, adapta a SensorEvent y publica en RabbitMQ.
     * Llamar desde un worker thread (no desde el Event Loop de Vert.x).
     *
     * @param data Paquete binario recibido
     * @return SensorEvent en formato canónico, o evento de error si el parseo falla
     */
    public SensorEvent processPacket(byte[] data) {
        try {
            String key = obtenerKeyDeHeader(data);
            ISensorAdapter adapter = adapters.getOrDefault(key, adapters.get("default"));

            LOG.infof("[DETECCION] Protocolo: %s (key: %s)", adapter.getTipo(), key);

            SensorEvent event = adapter.adaptar(data);
            event.setEventId(UUID.randomUUID().toString());

            LOG.infof("[EVENTO CANONICO] %s", event);
            LOG.infof("[JSON]\n%s", event.toJson());

            rabbitMQPublisher.publicarLectura(event.toJson());

            return event;

        } catch (Exception e) {
            LOG.errorf("[ERROR] Error al procesar paquete: %s", e.getMessage());
            return crearEventoError();
        }
    }

    /**
     * Extrae la clave del HashMap desde el byte header[2].
     * Protocolo A: byte[2] es el high-byte de longitud (0x00 para paquetes de 36 bytes) → "default".
     * Protocolo B: byte[2] = 0x02 → "02".
     * Protocolo C: byte[2] = 0x03 → "03".
     */
    private String obtenerKeyDeHeader(byte[] data) {
        if (data == null || data.length < 3) {
            return "default";
        }
        return String.format("%02X", data[2]);
    }

    private SensorEvent crearEventoError() {
        SensorEvent event = new SensorEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setSensorId("ERROR");
        event.setEventType("errorEvent");
        return event;
    }
}
