package org.itson.adapter.tcp;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.itson.adapter.service.PacketProcessor;

/**
 * Servidor TCP usando Vert.x para recibir paquetes binarios del TcpSensorServer
 * Se inicia automáticamente con Quarkus
 *
 * @author Sistema Invernadero Distribuido
 */
@ApplicationScoped
public class TcpServerVerticle extends AbstractVerticle {

    @Inject
    Vertx vertx;

    @Inject
    PacketProcessor packetProcessor;

    @ConfigProperty(name = "tcp.server.port")
    int port;

    @ConfigProperty(name = "tcp.server.host")
    String host;

    @ConfigProperty(name = "tcp.buffer.size")
    int bufferSize;

    private NetServer netServer;

    /**
     * Inicia el servidor TCP cuando Quarkus arranca
     */
    void onStart(@Observes StartupEvent event) {
        vertx.deployVerticle(this);
    }

    @Override
    public void start(Promise<Void> startPromise) {
        netServer = vertx.createNetServer();

        netServer.connectHandler(this::handleConnection)
                .listen(port, host, result -> {
                    if (result.succeeded()) {
                        System.out.println("=================================================");
                        System.out.println("SensorDataAdapter TCP Server iniciado");
                        System.out.println("Escuchando en: " + host + ":" + port);
                        System.out.println("Buffer size: " + bufferSize + " bytes");
                        System.out.println("=================================================");
                        startPromise.complete();
                    } else {
                        System.err.println("Error al iniciar servidor TCP: " + result.cause().getMessage());
                        startPromise.fail(result.cause());
                    }
                });
    }

    /**
     * Maneja una nueva conexión TCP
     */
    private void handleConnection(NetSocket socket) {
        String clientAddress = socket.remoteAddress().toString();
        System.out.println("\n[CONEXION] Cliente conectado: " + clientAddress);

        socket.handler(buffer -> handlePacket(socket, buffer, clientAddress));

        socket.closeHandler(v -> {
            System.out.println("[DESCONEXION] Cliente desconectado: " + clientAddress);
        });

        socket.exceptionHandler(throwable -> {
            System.err.println("[ERROR] Error con cliente " + clientAddress + ": " + throwable.getMessage());
        });
    }

    /**
     * Procesa un paquete binario recibido
     */
    private void handlePacket(NetSocket socket, Buffer buffer, String clientAddress) {
        byte[] data = buffer.getBytes();

        System.out.println("\n[PAQUETE RECIBIDO]");
        System.out.println("  Cliente: " + clientAddress);
        System.out.println("  Tamaño: " + data.length + " bytes");
        System.out.println("  Datos (hex): " + bytesToHex(data));

        // Procesar el paquete usando el PacketProcessor
        packetProcessor.processPacket(data);

        // Enviar confirmación al cliente
        socket.write(Buffer.buffer("ACK".getBytes()));
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        if (netServer != null) {
            netServer.close(result -> {
                if (result.succeeded()) {
                    System.out.println("\nServidor TCP detenido correctamente");
                    stopPromise.complete();
                } else {
                    stopPromise.fail(result.cause());
                }
            });
        } else {
            stopPromise.complete();
        }
    }

    /**
     * Convierte bytes a representación hexadecimal
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(bytes.length, 64); // Limitar a 64 bytes para visualización
        for (int i = 0; i < limit; i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        if (bytes.length > 64) {
            sb.append("... (").append(bytes.length - 64).append(" bytes mas)");
        }
        return sb.toString().trim();
    }
}
