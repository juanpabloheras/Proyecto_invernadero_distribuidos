# Proyecto Invernadero Distribuido

## ¿Qué es este proyecto?
Sistema de monitoreo de invernadero distribuido con múltiples microservicios
que se comunican via RabbitMQ, gRPC y HTTP.

## Arquitectura general

```
TcpSensorServer → SensorDataAdapter ──┬──(RabbitMQ)──▶ MSDatosHistoricos
                                       ├──(RabbitMQ)──▶ AlarmEvaluatorService ──(gRPC)──▶ MS_CRUDINVERNADERO
                                       └──(RabbitMQ)──▶ SSEMonitorAlarmas ──(SSE)──▶ WebApp
```

## Microservicios

| Servicio | Tecnología | Estado |
|---|---|---|
| MS_CRUDINVERNADERO | Node.js + Express + Sequelize + MySQL | ✅ Completo |
| AlarmEvaluatorService | Quarkus (Java) | 🔴 Por implementar |
| TcpSensorServer | Quarkus / Netty | 🔴 Pendiente |
| SensorDataAdapter | - | 🔴 Pendiente |
| MSDatosHistoricos | - | 🔴 Pendiente |
| SSEMonitorAlarmas | - | 🔴 Pendiente |
| MSAnaliticas | - | 🔴 Pendiente |
| WebApp | Vue 3 + Vite | 🟡 Parcial (solo Login) |

## MS_CRUDINVERNADERO (Node.js) — Arquitectura por capas

```
Route → Controller → Service → DAO → Model (Sequelize)
```

- Rutas: `/api/sensores`, `/api/configuraciones-alarma`, `/api/usuarios`
- Errores controlados con `AppError(mensaje, statusCode)`
- Paginación estándar: `page`, `limit`, `sort`, `order`
- gRPC cliente saliente en `src/grpc/alarmaNotificacion.client.js`
- Proto en `src/proto/alarma-notificacion.proto`

## AlarmEvaluatorService (Quarkus) — Responsabilidades

1. Consumir lecturas de sensor via RabbitMQ (queue: `sensor.evaluador`)
2. Consultar configuraciones activas al MS_CRUDINVERNADERO via gRPC
3. Cachear configuraciones en memoria (`ConcurrentHashMap`)
4. Evaluar: `MAYOR_QUE`, `MENOR_QUE`, `IGUAL_A`
5. Publicar `AlarmTriggeredEvent` via RabbitMQ (exchange: `alarm.events`, tipo fanout)

## Modelos de datos clave

### SensorReading (entrada RabbitMQ)
```json
{ "sensorId": 1, "tipo": "TEMPERATURA|HUMEDAD", "valor": 35.5, "timestamp": "..." }
```

### AlarmTriggeredEvent (salida RabbitMQ)
```json
{
  "idConfiguracionAlarma": 1,
  "nombreAlarma": "Temp alta",
  "tipoAlarma": "TEMPERATURA",
  "operador": "MAYOR_QUE",
  "valorCritico": 35.0,
  "valorLeido": 37.2,
  "sensorId": 1,
  "timestamp": "..."
}
```

### ConfiguracionAlarma (desde gRPC / BD)
```json
{
  "idConfiguracionAlarma": 1,
  "nombreAlarma": "string",
  "tipoAlarma": "TEMPERATURA|HUMEDAD",
  "operador": "MAYOR_QUE|MENOR_QUE|IGUAL_A",
  "valorCritico": 35.0,
  "activa": true
}
```

## Proto gRPC — alarma-notificacion.proto

### RPC existente (Node.js notifica al evaluador)
```proto
rpc NotificarConfiguracionCreada(ConfiguracionAlarmaCreadaRequest)
    returns (NotificacionResponse);
```

### RPC pendiente (evaluador consulta a Node.js)
```proto
rpc ObtenerConfiguracionesActivas(ObtenerConfiguracionesRequest)
    returns (ConfiguracionesResponse);
```

## RabbitMQ — Configuración

- Host: localhost, Puerto: 5672
- Exchange entrada sensor: `sensor.data` (direct)
- Queue evaluador: `sensor.evaluador`
- Exchange salida alarmas: `alarm.events` (fanout)
- **RabbitMQ hay que configurarlo desde cero**

## Dónde nos quedamos

Estábamos a punto de comenzar la implementación del **AlarmEvaluatorService**.
El primer paso acordado es:

1. **Extender el proto** `alarma-notificacion.proto` para agregar
   `ObtenerConfiguracionesActivas` — esto permite que el evaluador
   le pida las configuraciones activas al MS_CRUDINVERNADERO.

2. Luego implementar el **AlarmEvaluatorService en Quarkus** paso a paso.

## Reglas de este proyecto

- Ir despacio, explicar cada concepto antes de escribir código
- El alumno está aprendiendo, no asumir conocimiento previo de Quarkus o RabbitMQ
- Siempre explicar el "por qué" antes del "cómo"
- Seguir la arquitectura del diagrama de componentes estrictamente
