package edu.itson.jackMurrieta.controller;

import com.mongodb.client.MongoClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.Map;

@Path("/v1/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthController {

    @Inject
    MongoClient mongoClient;

    @GET
    public Response health() {
        try {
            // Ping rápido a MongoDB
            mongoClient.getDatabase("invernadero_db")
                       .runCommand(new org.bson.Document("ping", 1));

            return Response.ok(Map.of(
                "status", "UP",
                "servicio", "analitica-service",
                "timestamp", Instant.now().toString(),
                "mongodb", "UP"
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(Map.of(
                    "status", "DOWN",
                    "servicio", "analitica-service",
                    "timestamp", Instant.now().toString(),
                    "mongodb", "DOWN",
                    "error", e.getMessage()
                )).build();
        }
    }
}
