# Proyecto Invernadero Distribuido

## ¿Qué es este proyecto?
Sistema de monitoreo de invernadero distribuido con múltiples microservicios
que se comunican via RabbitMQ, gRPC y HTTP.

## Arquitectura general

```
TcpSensorServer → SensorDataAdapter ──(RabbitMQ Topic)──┬──▶ MSDatosHistoricos (MongoDB)
  puerto 8080       puerto 9090/8081                     └──▶ AlarmEvaluatorService ──(gRPC)──▶ MS_CRUDINVERNADERO
                                                                      │
                                                          routing key: "alarma.disparada"
                                                                      │
                                                                      ▼
                                                             NotificadorAlarmas ──(SSE)──▶ WebApp
                                                                puerto 8082
```

---

## Estado actual de microservicios

| Servicio | Tecnología | Puerto | Estado |
|---|---|---|---|
| TcpSensorServer | Java puro (ServerSocket + ThreadPool) | 8080 | ✅ Completo |
| SensorDataAdapter | Quarkus + Vert.x + SmallRye RabbitMQ | 9090 TCP / 8081 HTTP | ✅ Completo |
| MSDatosHistoricos | Quarkus + MongoDB Panache + SmallRye RabbitMQ | - | ✅ Completo* |
| NotificadorAlarmas | Quarkus + SmallRye RabbitMQ + SSE | 8082 | ✅ Completo |
| MS_CRUDINVERNADERO | Node.js + Express + Sequelize + MySQL | 3000 / gRPC 50052 | ✅ Completo |
| AlarmEvaluatorService | Quarkus + gRPC + SmallRye RabbitMQ | 8080 HTTP / 50051 gRPC | 🟡 Infraestructura lista, falta lógica real |
| WebApp | Vue 3 + Vite | 5173 (dev) | 🟡 Parcial (solo Login) |

*MSDatosHistoricos tiene un bug en el exchange — ver sección abajo.

---

## RabbitMQ — Exchange único y routing keys

**TODOS los servicios usan UN SOLO exchange:**

```
Exchange: invernadero.events  (tipo: topic, durable)
```

| Routing Key | Publicado por | Consumido por |
|---|---|---|
| `sensor.lectura.registrada` | SensorDataAdapter | MSDatosHistoricos, AlarmEvaluatorService |
| `alarma.disparada` | AlarmEvaluatorService | NotificadorAlarmas |

### Diagrama completo

```
[SensorDataAdapter]
        │  routing key: "sensor.lectura.registrada"
        ▼
Exchange: invernadero.events  (topic, durable)
        │
        ├──▶ Queue: sensor-readings-queue       ──▶ MSDatosHistoricos
        └──▶ Queue: alarm-evaluator.sensor-readings ──▶ AlarmEvaluatorService

[AlarmEvaluatorService]
        │  routing key: "alarma.disparada"
        ▼
Exchange: invernadero.events  (mismo exchange)
        │
        └──▶ Queue: notificador.alarmas ──▶ NotificadorAlarmas ──(SSE)──▶ WebApp
```

---

## SensorDataAdapter ✅

- Publica al exchange `invernadero.events` con routing key `sensor.lectura.registrada`
- Formato canónico `SensorEvent`:

```json
{
  "eventId": "uuid",
  "version": 1,
  "eventType": "sensorReadingEvent",
  "timestamp": "2026-05-03T20:24:07.131-0700",
  "sensorId": "12345",
  "mediciones": ["temperatura:23.4°C", "humedad:62.1%"]
}
```

---

## MSDatosHistoricos ✅ (con bug pendiente)

- Quarkus + MongoDB Panache, colección `lecturas_invernadero`
- Guarda un documento por medición (no uno por evento)

```json
{ "tipoSensor": "temperatura", "valor": 23.4, "unidad": "°C", "fecha": "..." }
```

### ⚠️ Bug: exchange incorrecto

Su `application.properties` todavía apunta al exchange viejo. Hay que cambiar:
```properties
# INCORRECTO (exchange viejo):
mp.messaging.incoming.sensor-readings.exchange.name=sensor.events.topic
mp.messaging.incoming.sensor-readings.routing-keys=sensorReadingEvent

# CORRECTO:
mp.messaging.incoming.sensor-readings.exchange.name=invernadero.events
mp.messaging.incoming.sensor-readings.routing-keys=sensor.lectura.registrada
```

### Patrón de parseo de mediciones (reutilizable)

```java
// input: "temperatura:23.4°C"
String[] partes = medicion.split(":");
String tipo = partes[0].trim();
String valorConUnidad = partes[1].trim();
String valorStr = valorConUnidad.replaceAll("[^0-9.-]", "");
String unidad = valorConUnidad.replace(valorStr, "").trim();
double valor = Double.parseDouble(valorStr);
```

---

## NotificadorAlarmas ✅

- Puerto: **8082**, CORS habilitado para `localhost:3000`, `5173`, `8080`
- Consume `alarma.disparada` del exchange `invernadero.events`
- Emite via SSE a todos los clientes conectados

### Endpoint SSE
```
GET http://localhost:8082/notificaciones/stream
```

### Cómo conectarse desde Vue
```js
const es = new EventSource('http://localhost:8082/notificaciones/stream')
es.onmessage = (e) => console.log(JSON.parse(e.data))
onUnmounted(() => es.close())
```

---

## AlarmEvaluatorService 🟡 — Infraestructura lista, falta lógica real

- Puerto HTTP: **8080**, Puerto gRPC servidor: **50051**
- gRPC cliente apunta al MS_CRUDINVERNADERO en `localhost:50052`

### Lo que YA está implementado

**RabbitMQ completo:**
- Consume `sensor.lectura.registrada` del exchange `invernadero.events` (queue: `alarm-evaluator.sensor-readings`)
- Publica `alarma.disparada` al exchange `invernadero.events`

**gRPC servidor** (`AlarmaNotificacionSGrpcServiceImpl`):
- `NotificarConfiguracionCreada` — recibe notificación del CRUD cuando se crea una config (solo imprime, no invalida caché aún)
- `ObtenerConfiguracionesActivas` — existe pero devuelve lista vacía (sin implementar)

**gRPC cliente** (`ConfiguracionCargador`):
- Al arrancar (`@StartupEvent`) consulta configuraciones activas al CRUD via gRPC
- Ya parsea y loguea la respuesta

**Proto** (`alarma-notificacion.proto`) — completo con los dos RPCs:
```proto
rpc NotificarConfiguracionCreada(ConfiguracionAlarmaCreadaRequest) returns (NotificacionResponse);
rpc ObtenerConfiguracionesActivas(ObtenerConfiguracionesRequest) returns (ConfiguracionesResponse);
```

**Modelos definidos como Java records:**
```java
// Entrada desde RabbitMQ (aún no se usa para deserializar)
record LecturaRegistradaEvent(String idInvernadero, String idSensor,
    String tipoLectura, double valor, String fecha)

// Salida a RabbitMQ
record AlarmaDisparadaEvent(String idInvernadero, String idSensor,
    int idConfiguracionAlarma, String nombreAlarma, String tipoAlarma,
    String operador, double valorCritico, double valorLeido,
    String fechaLectura, String mensaje)
```

### Lo que FALTA implementar

**1. LecturaConsumer — lógica real** (actualmente es mock):
```java
// HOY hace esto (hardcodeado):
if (mensaje.contains("temperatura:43.0°C")) {
    alarmaPublisher.publicarAlarmaMock();
}

// DEBE hacer esto:
// 1. Deserializar JSON → SensorEvent
// 2. Parsear mediciones ("temperatura:23.4°C" → tipo, valor, unidad)
// 3. Consultar caché de configuraciones
// 4. Evaluar cada medición contra cada configuración activa del mismo tipo
// 5. Si cumple la condición → publicar AlarmaDisparadaEvent real (no mock)
```

**2. Caché de configuraciones** — `ConfiguracionCargador` ya carga al inicio pero no guarda nada en memoria. Falta:
- Guardar en un `Map<String, List<ConfiguracionAlarma>>` (key = tipoAlarma)
- Que `LecturaConsumer` consulte ese mapa
- Que `NotificarConfiguracionCreada` invalide el caché cuando llega nueva config

**3. AlarmaPublisher — publicar evento real** (actualmente es mock):
```java
// HOY publica JSON hardcodeado
// DEBE publicar un AlarmaDisparadaEvent serializado con Gson
```

**4. ObtenerConfiguracionesActivas en el gRPC servidor** — devuelve lista vacía, debería devolver el caché local.

### Lógica de evaluación (ya definida, falta conectar)
```java
switch (operador) {
    case "MAYOR_QUE" -> valorLeido > valorCritico;
    case "MENOR_QUE" -> valorLeido < valorCritico;
    case "IGUAL_A"   -> Math.abs(valorLeido - valorCritico) < 0.001;
}
```

---

## MS_CRUDINVERNADERO (Node.js)

```
Route → Controller → Service → DAO → Model (Sequelize)
```
- Puerto REST: **3000**, Puerto gRPC servidor: **50052**
- Rutas: `/api/sensores`, `/api/configuraciones-alarma`, `/api/usuarios`
- Cuando se crea una config de alarma → notifica al AlarmEvaluatorService via gRPC (puerto 50051)
- Proto en `src/proto/alarma-notificacion.proto` — debe ser igual al del AlarmEvaluatorService

---

## Scripts de prueba de sensores

```
sensorTest/sensorA.py      → Python, Protocolo A, puerto 8080
sensorTest/sensorB.py      → Python, Protocolo B, puerto 8080
sensorTest/sensorWindow.c  → C, Protocolo C batch, puerto 8080
```

---

## Reglas para Claude Code en este proyecto

- Ir despacio, explicar cada concepto antes de escribir código
- El alumno está aprendiendo, no asumir conocimiento previo de Quarkus o RabbitMQ
- Siempre explicar el "por qué" antes del "cómo"
- Exchange único para todo: `invernadero.events`
- El MSDatosHistoricos es la referencia de patrón para parseo de mediciones
- No saltarse pasos, implementar una cosa a la vez
