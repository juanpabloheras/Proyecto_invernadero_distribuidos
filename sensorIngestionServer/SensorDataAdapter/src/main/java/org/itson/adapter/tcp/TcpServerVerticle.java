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
import org.jboss.logging.Logger;

/**
 * Servidor TCP usando Vert.x para recibir paquetes binarios del TcpSensorServer.
 * Se inicia automáticamente con Quarkus.
 *
 * Framing: cada conexión mantiene un acumulador de bytes. El método
 * resolvePacketLength determina cuántos bytes forman un paquete completo
 * según el tipo de protocolo indicado en byte[2] del header.
 *
 * El procesamiento de cada paquete se delega a un worker thread mediante
 * vertx.executeBlocking para no bloquear el Event Loop.
 *
 * @author Sistema Invernadero Distribuido
 */
@ApplicationScoped
public class TcpServerVerticle extends AbstractVerticle {

    private static final Logger LOG = Logger.getLogger(TcpServerVerticle.class);

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

    void onStart(@Observes StartupEvent event) {
        vertx.deployVerticle(this);
    }

    @Override
    public void start(Promise<Void> startPromise) {
        netServer = vertx.createNetServer();

        netServer.connectHandler(this::handleConnection)
                .listen(port, host, result -> {
                    if (result.succeeded()) {
                        LOG.infof("SensorDataAdapter TCP Server iniciado en %s:%d (buffer: %d bytes)",
                                host, port, bufferSize);
                        startPromise.complete();
                    } else {
                        LOG.errorf("Error al iniciar servidor TCP: %s", result.cause().getMessage());
                        startPromise.fail(result.cause());
                    }
                });
    }

    private void handleConnection(NetSocket socket) {
        String clientAddress = socket.remoteAddress().toString();
        LOG.infof("[CONEXION] Cliente conectado: %s", clientAddress);

        // Buffer acumulador por conexión — safe porque los handlers de un socket
        // siempre se ejecutan en el mismo hilo del Event Loop.
        Buffer[] accumRef = {Buffer.buffer()};

        socket.handler(chunk -> {
            accumRef[0].appendBuffer(chunk);
            drainAccumulator(socket, accumRef, clientAddress);
        });

        socket.closeHandler(v ->
                LOG.infof("[DESCONEXION] Cliente desconectado: %s", clientAddress));

        socket.exceptionHandler(err ->
                LOG.errorf("[ERROR] Error con cliente %s: %s", clientAddress, err.getMessage()));
    }

    /**
     * Extrae y despacha todos los paquetes completos del acumulador.
     * Mientras haya bytes suficientes para un paquete entero, lo extrae y
     * lo procesa en un worker thread; el resto permanece en el acumulador.
     */
    private void drainAccumulator(NetSocket socket, Buffer[] accumRef, String clientAddress) {
        while (true) {
            int packetLen = resolvePacketLength(accumRef[0]);
            if (packetLen < 0) return;                      // cabecera incompleta
            if (accumRef[0].length() < packetLen) return;   // paquete incompleto

            byte[] packet = accumRef[0].getBytes(0, packetLen);
            // slice+copy consume los bytes del paquete ya extraído
            accumRef[0] = accumRef[0].slice(packetLen, accumRef[0].length()).copy();

            LOG.debugf("[PAQUETE] Cliente: %s, tamaño: %d bytes, hex: %s",
                    clientAddress, packetLen, bytesToHex(packet));

            // Delegar a worker thread para no bloquear el Event Loop.
            // ordered=true preserva el orden de llegada de paquetes por conexión.
            vertx.<Void>executeBlocking(() -> {
                packetProcessor.processPacket(packet);
                return null;
            }, true).onFailure(err ->
                    LOG.errorf("[ERROR] Fallo procesando paquete de %s: %s",
                            clientAddress, err.getMessage())
            );

            socket.write(Buffer.buffer("ACK".getBytes()));
        }
    }

    /**
     * Determina la longitud total del paquete a partir del campo longitud del header.
     * Retorna -1 si el acumulador no tiene suficientes bytes para leer la cabecera.
     *
     * Protocolo B (byte[2]=0x02): longitud en bytes [3-4]
     * Protocolo C (byte[2]=0x03): longitud en bytes [4-5]
     * Protocolo A (byte[2]=0x00): longitud en bytes [2-3]  (high-byte siempre 0x00 para 36 bytes)
     */
    private int resolvePacketLength(Buffer accum) {
        if (accum.length() < 3) return -1;

        byte typeByte = accum.getByte(2);

        if (typeByte == 0x02) {
            if (accum.length() < 5) return -1;
            return accum.getUnsignedShort(3);
        }
        if (typeByte == 0x03) {
            if (accum.length() < 6) return -1;
            return accum.getUnsignedShort(4);
        }
        // Protocolo A
        if (accum.length() < 4) return -1;
        return accum.getUnsignedShort(2);
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        if (netServer != null) {
            netServer.close(result -> {
                if (result.succeeded()) {
                    LOG.info("Servidor TCP detenido correctamente");
                    stopPromise.complete();
                } else {
                    stopPromise.fail(result.cause());
                }
            });
        } else {
            stopPromise.complete();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(bytes.length, 64);
        for (int i = 0; i < limit; i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        if (bytes.length > 64) {
            sb.append("... (").append(bytes.length - 64).append(" bytes mas)");
        }
        return sb.toString().trim();
    }
}
