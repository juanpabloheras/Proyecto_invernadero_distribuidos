package sse;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jboss.resteasy.reactive.RestStreamElementType;

/**
 * Clase notificadora por SSE
 *
 * @author Juan Heras
 */
@ApplicationScoped
public class NotificacionesSseService {

    private final List<MultiEmitter<? super String>> clientes = new CopyOnWriteArrayList<>();

    public Multi<String> stream() {
        return Multi.createFrom().emitter(emitter -> {
            clientes.add(emitter);

            emitter.onTermination(() -> clientes.remove(emitter));
        });
    }

    public void enviar(String mensaje) {
        for (MultiEmitter<? super String> cliente : clientes) {
            cliente.emit(mensaje);
        }
    }

}
