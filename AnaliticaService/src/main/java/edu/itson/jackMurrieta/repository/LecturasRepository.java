package edu.itson.jackMurrieta.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import edu.itson.jackMurrieta.dtos.ResultadoBusqueda;
import edu.itson.jackMurrieta.entidades.Medicion;
import edu.itson.jackMurrieta.entidades.SensorData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class LecturasRepository {

    @Inject
    MongoClient mongoClient;

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("invernadero_db")
                         .getCollection("lecturas_invernadero");
    }

    // --- /v1/lecturas ---

    public ResultadoBusqueda buscarConFiltros(
            String desde, String hasta,
            List<String> sensorIds, String tipo,
            Boolean soloValidos, boolean formatoPlano,
            int limit, String cursor) {

        List<Document> pipeline = new ArrayList<>();
        Document matchStage = buildMatchBase(desde, hasta, sensorIds);

        if (cursor != null) {
            String[] parts = decodeCursor(cursor);
            Instant lastTs = Instant.ofEpochMilli(Long.parseLong(parts[0]));
            ObjectId lastId = new ObjectId(parts[1]);
            matchStage.append("$or", Arrays.asList(
                new Document("timestamp", new Document("$gt", Date.from(lastTs))),
                new Document("timestamp", Date.from(lastTs))
                    .append("_id", new Document("$gt", lastId))
            ));
        }

        if (!matchStage.isEmpty()) {
            pipeline.add(new Document("$match", matchStage));
        }

        pipeline.add(new Document("$sort", new Document("timestamp", 1).append("_id", 1)));

        if (formatoPlano) {
            pipeline.add(new Document("$unwind", "$mediciones"));
            Document medMatch = new Document();
            if (tipo != null) {
                medMatch.append("mediciones.tipo",
                    new Document("$regex", tipo).append("$options", "i"));
            }
            if (Boolean.TRUE.equals(soloValidos)) {
                medMatch.append("mediciones.esValido", true);
            }
            if (!medMatch.isEmpty()) {
                pipeline.add(new Document("$match", medMatch));
            }
        } else if (tipo != null || Boolean.TRUE.equals(soloValidos)) {
            Document filterCond = buildMedicionFilterCond(tipo, soloValidos);
            pipeline.add(new Document("$project", new Document()
                .append("sensorId", 1)
                .append("timestamp", 1)
                .append("timestampSource", 1)
                .append("mediciones", new Document("$filter", new Document()
                    .append("input", "$mediciones")
                    .append("as", "m")
                    .append("cond", filterCond)))));
        }

        pipeline.add(new Document("$limit", limit + 1));

        List<Document> docs = getCollection().aggregate(pipeline).into(new ArrayList<>());

        boolean hayMas = docs.size() > limit;
        if (hayMas) docs.remove(docs.size() - 1);

        String siguienteCursor = null;
        if (hayMas && !docs.isEmpty()) {
            Document last = docs.get(docs.size() - 1);
            Instant lastTs = toInstant(last.get("timestamp"));
            ObjectId lastId = last.getObjectId("_id");
            siguienteCursor = encodeCursor(lastTs, lastId);
        }

        List<SensorData> datos = docs.stream().map(this::docToSensorData).collect(Collectors.toList());
        return new ResultadoBusqueda(datos, siguienteCursor, hayMas);
    }

    // --- /v1/lecturas/agregado ---

    public List<Document> obtenerAgregado(
            String desde, String hasta,
            List<String> sensorIds, String tipo,
            Boolean soloValidos, String intervalo,
            List<String> metricas) {

        List<Document> pipeline = new ArrayList<>();

        Document matchStage = buildMatchBase(desde, hasta, sensorIds);
        if (!matchStage.isEmpty()) {
            pipeline.add(new Document("$match", matchStage));
        }

        pipeline.add(new Document("$unwind", "$mediciones"));

        Document medMatch = new Document();
        if (tipo != null) {
            medMatch.append("mediciones.tipo",
                new Document("$regex", tipo).append("$options", "i"));
        }
        if (Boolean.TRUE.equals(soloValidos)) {
            medMatch.append("mediciones.esValido", true);
        }
        if (!medMatch.isEmpty()) {
            pipeline.add(new Document("$match", medMatch));
        }

        Document groupId = new Document()
            .append("sensorId", "$sensorId")
            .append("tipo", "$mediciones.tipo")
            .append("periodo", getIntervalExpression(intervalo));

        Document groupStage = new Document("_id", groupId);

        Set<String> metricasSet = (metricas != null && !metricas.isEmpty())
            ? new HashSet<>(metricas)
            : new HashSet<>(Arrays.asList("promedio", "min", "max", "count"));

        if (metricasSet.contains("promedio"))
            groupStage.append("promedio", new Document("$avg", "$mediciones.valorNumerico"));
        if (metricasSet.contains("min"))
            groupStage.append("min", new Document("$min", "$mediciones.valorNumerico"));
        if (metricasSet.contains("max"))
            groupStage.append("max", new Document("$max", "$mediciones.valorNumerico"));
        if (metricasSet.contains("count"))
            groupStage.append("count", new Document("$sum", 1));
        if (metricasSet.contains("desviacion"))
            groupStage.append("desviacion", new Document("$stdDevSamp", "$mediciones.valorNumerico"));

        pipeline.add(new Document("$group", groupStage));
        pipeline.add(new Document("$sort",
            new Document("_id.sensorId", 1).append("_id.periodo", 1)));

        Document project = new Document()
            .append("_id", 0)
            .append("sensorId", "$_id.sensorId")
            .append("tipo", "$_id.tipo")
            .append("periodo", "$_id.periodo");

        if (metricasSet.contains("promedio")) project.append("promedio", 1);
        if (metricasSet.contains("min"))      project.append("min", 1);
        if (metricasSet.contains("max"))      project.append("max", 1);
        if (metricasSet.contains("count"))    project.append("count", 1);
        if (metricasSet.contains("desviacion")) project.append("desviacion", 1);

        pipeline.add(new Document("$project", project));

        return getCollection().aggregate(pipeline).into(new ArrayList<>());
    }

    // --- /v1/lecturas/ultima ---

    public List<SensorData> obtenerUltimaPorSensor() {
        List<Document> pipeline = Arrays.asList(
            new Document("$sort", new Document("timestamp", -1)),
            new Document("$group", new Document("_id", "$sensorId")
                .append("doc", new Document("$first", "$$ROOT"))),
            new Document("$replaceRoot", new Document("newRoot", "$doc")),
            new Document("$sort", new Document("sensorId", 1))
        );
        return getCollection().aggregate(pipeline).into(new ArrayList<>())
            .stream().map(this::docToSensorData).collect(Collectors.toList());
    }

    // --- /v1/lecturas/sensores ---

    public List<String> obtenerSensores() {
        return getCollection().distinct("sensorId", String.class).into(new ArrayList<>());
    }

    // --- /v1/lecturas/tipos-medicion ---

    public List<String> obtenerTiposMedicion() {
        return getCollection().distinct("mediciones.tipo", String.class).into(new ArrayList<>());
    }

    // --- Helpers de construcción de pipeline ---

    private Document buildMatchBase(String desde, String hasta, List<String> sensorIds) {
        Document match = new Document();
        if (desde != null || hasta != null) {
            Document tsFilter = new Document();
            if (desde != null) tsFilter.append("$gte", Date.from(Instant.parse(desde)));
            if (hasta != null) tsFilter.append("$lte", Date.from(Instant.parse(hasta)));
            match.append("timestamp", tsFilter);
        }
        if (sensorIds != null && !sensorIds.isEmpty()) {
            match.append("sensorId", sensorIds.size() == 1
                ? sensorIds.get(0)
                : new Document("$in", sensorIds));
        }
        return match;
    }

    private Document buildMedicionFilterCond(String tipo, Boolean soloValidos) {
        List<Document> conditions = new ArrayList<>();
        if (tipo != null) {
            conditions.add(new Document("$regexMatch", new Document()
                .append("input", "$$m.tipo")
                .append("regex", tipo)
                .append("options", "i")));
        }
        if (Boolean.TRUE.equals(soloValidos)) {
            conditions.add(new Document("$eq", Arrays.asList("$$m.esValido", true)));
        }
        return conditions.size() == 1 ? conditions.get(0) : new Document("$and", conditions);
    }

    private Document getIntervalExpression(String intervalo) {
        String format = switch (intervalo != null ? intervalo : "hora") {
            case "minuto" -> "%Y-%m-%dT%H:%M:00Z";
            case "dia"    -> "%Y-%m-%d";
            case "semana" -> "%Y-W%U";
            case "mes"    -> "%Y-%m";
            default       -> "%Y-%m-%dT%H:00:00Z";
        };
        return new Document("$dateToString", new Document()
            .append("format", format)
            .append("date", "$timestamp"));
    }

    // --- Cursor encoding/decoding ---

    private String encodeCursor(Instant timestamp, ObjectId id) {
        String raw = timestamp.toEpochMilli() + "|" + id.toHexString();
        return Base64.getUrlEncoder().withoutPadding()
                     .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String[] decodeCursor(String cursor) {
        String raw = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
        return raw.split("\\|", 2);
    }

    // --- Mapeo Document → POJO ---

    private SensorData docToSensorData(Document doc) {
        SensorData sd = new SensorData();
        sd.id = doc.getObjectId("_id");
        sd.sensorId = doc.getString("sensorId");
        sd.timestamp = toInstant(doc.get("timestamp"));
        sd.timestampSource = doc.getString("timestampSource");

        List<Document> medDocs = doc.getList("mediciones", Document.class);
        if (medDocs != null) {
            sd.mediciones = medDocs.stream()
                .map(this::docToMedicion)
                .collect(Collectors.toList());
        }
        return sd;
    }

    private Medicion docToMedicion(Document doc) {
        Medicion m = new Medicion();
        m.tipo = doc.getString("tipo");
        m.valorOriginal = doc.getString("valorOriginal");
        m.valorNumerico = doc.getDouble("valorNumerico");
        m.unidad = doc.getString("unidad");
        m.esValido = doc.getBoolean("esValido");
        return m;
    }

    private Instant toInstant(Object value) {
        if (value instanceof Date d) return d.toInstant();
        return null;
    }
}
