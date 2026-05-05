# Proyecto_invernadero_distribuidos

Sistema distribuido orientado a eventos para el monitoreo de sensores en invernaderos, capaz de procesar lecturas en tiempo real, evaluar reglas de negocio y generar notificaciones mediante una arquitectura desacoplada basada en mensajería.

El sistema recibe datos desde dispositivos físicos a través de un adaptador TCP, los publica en RabbitMQ y los distribuye a múltiples servicios consumidores. Un componente especializado evalúa condiciones de alarma utilizando configuraciones dinámicas en caché, mientras que otros servicios permiten la persistencia histórica y el envío de notificaciones por distintos medios (SSE, email, SMS).

- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Prerrequisitos](#prerrequisitos)
- [Datos de prueba e inserts](#datos-de-prueba-e-inserts)
- [Variables de entorno](#variables-de-entorno)
- [Pasos para correr la aplicación](#pasos-para-correr-la-aplicación)
- [Mensajería](#mensajería)
- [Problemas comunes](#problemas-comunes)
- [Equipo de desarrollo](#equipo-de-desarrollo)



## Arquitectura

El sistema sigue un enfoque orientado a eventos, con microservicios utilizando RabbitMQ como broker central.

### Flujo de datos

1. Sensor → envía datos vía TCP
2. SensorDataAdapter → traduce y publica en RabbitMQ
3. RabbitMQ → distribuye eventos a consumidores
4. AlarmEvaluatorService → evalúa reglas de alarma
5. NotificadorAlarmas → envía notificaciones
6. HistoricalDataService → almacena datos históricos

##  Tecnologías

- Vue.js (Frontend)
- Node.js + Express (Backend)
- Java (Quarkus / NetBeans services)
- RabbitMQ (mensajería)
- Docker (contenedorización)
- SSE (Server-Sent Events)
- gRPC (comunicación entre servicios)
- Python (simulación de sensores)


##  Prerrequisitos

- Docker Desktop
- Node.js
- Java (JDK 21)
- Python (para simuladores)


## Datos de prueba e inserts

Antes de probar el frontend y los endpoints del backend, se recomienda cargar datos base en MySQL y, si se desea probar reportes o graficas sin mandar lecturas desde sensores, tambien cargar lecturas historicas en MongoDB.

### MySQL - Backend

La base de datos del backend debe existir con el nombre configurado en `Backend/.env`, normalmente:

```sql
CREATE DATABASE IF NOT EXISTS invernadero_db;
```

Despues de crear la base, ejecutar el script de datos de prueba:

```text
Backend/docs/db/seed_test_data.sql
```

Desde MySQL Workbench:

1. Abrir el archivo `Backend/docs/db/seed_test_data.sql`
2. Verificar que la primera linea sea `USE invernadero_db;`
3. Ejecutar todo el script

El script inserta usuarios, sensores, medios de notificacion, configuraciones de alarma y la relacion entre alarmas y medios. Usa `ON DUPLICATE KEY UPDATE`, por lo que se puede ejecutar mas de una vez sin duplicar registros.

Usuarios de prueba:

| Correo | Password |
|------|------|
| `admin@test.com` | `Admin123` |
| `pedro@test.com` | `Password123` |
| `operador@test.com` | `Invernadero123` |

### MongoDB - HistoricalDataService

Las lecturas historicas normalmente se generan al correr los sensores y pasar por RabbitMQ. 


### Variables de entorno
Antes de correr el backend  es necesario crear un archivo `.env` dentro de la carpeta `Backend`

El backend requiere las siguientes variables de entorno:
- `PORT` → Puerto donde se ejecuta la API backend
- `DB_NAME` → Nombre de la base de datos MySQL
- `DB_USER` → Contraseña de MySQL
- `DB_PASSWORD` → Contraseña de MySQL
- `DB_HOST` → Host de la base de datos
- `DB_PORT` → Puerto de MySQL
- `ALARMA_GRPC_URL` → Dirección del servicio gRPC utilizado para la comunicación con AlarmEvaluatorService
- `ALARMA_GRPC_TIMEOUT_MS` → Tiempo máximo de espera para llamadas gRPC en milisegundos


Ejemplo:

```env
PORT=3000

DB_NAME=invernadero_db
DB_USER=usuario_db
DB_PASSWORD=password_db
DB_HOST=localhost
DB_PORT=3306

ALARMA_GRPC_URL=localhost:50051
ALARMA_GRPC_TIMEOUT_MS=3000
```


## Pasos para correr la aplicación
1. Abrir Docker Desktop
2. Detener RabbitMQ de manera local
3. Levantamos RabbitMQ en Docker
    Para esto necesitamos crear el contenedor primero con 
    ```bash
    docker run -d --name rabbitmq_invernadero -p 5672:5672 -p 15672:15672 rabbitmq:3-management
    ```
    Verficamos que Docker este arriba con
   ```bash 
    docker ps
    ```
   Y deberíamos ver algo como lo siguiente si es correcto

    ```bash
    0.0.0.0:5672->5672/tcp   0.0.0.0:15672->15672/tcp
    ```
5. Entramos al panel /UI de rabbit en http://localhost:15672 con usuario: **guest** y password: **guest**
6. Levantamos los consumidores primero (**Antes de correr proyectos, los compilamos** ):
    - Primero levantamos el backend dirigiendonos a la carpeta `Backend` y ejecutamos los comandos 
    ```bash 
    npm install
    npm run dev
    ``` 
    Para poder levantar correctamente el backend la base de datos necesita estar creada como **invernadero_db** o como este en el env de Backend `DB_NAME`. Una vez que se haya levantado correctamente el backend, podemos continuar con el siguiente paso:
    - En `AlarmEvaluatorService`, `NotificadorAlarmas` y `HistoricalDataService `ejecutamos los proyectos en NetBeans.
7. Una vez con los consumidores levantados, podemos revisar en la interfaz de RabbitMQ en el apartado de **Queues and Streams** que se hayan creado las colas `alarm-evaluator.sensor-readings`, `historical.sensor-readings` y `notificador.alarmas` además de en la sección **Exchanges** que aparezca `invernadero.events`.
8. Después de que se hayan levantado los consumidores ahora levantamos quien vendría siendo publicador de lecturas corremos el proyecto `SensorDataAdapter`, si se ejecutó correctamente debe mostrar algo como
```bash
SensorDataAdapter TCP Server iniciado
Escuchando en: 0.0.0.0:9090
```
8. Mandar lecturas de sensores:
- En una terminal o ide, corremos el programa cualquiera de los progamas ubicados en el directorio sensorTest, dependiendo del archivo a ejecutar tomamos como ejemplo el siguiente.
```bash
python sensorB.py --temp 43 --hum 62.1
```
9. Si todo es correcto al momento de mandar la lectura, debería mostrarse 
* En `SensorAdapter` : 
```bash
[PAQUETE RECIBIDO]
[DETECCION] Protocolo detectado: B
[RABBITMQ] Publicado → sensor.lectura.registrada
```
* En `AlarmEvaluatorService` :
```bash
Lectura recibida: 
Alarma disparada!
``` 
En caso de que alguna alarma coincida.




---------------------------------------------

Podemos correr el FrontEnd para insertar sensores o configuraciones de alarma en la ruta Frontend/invernadero frontend y ejecutar npm install y posterior npm run dev.

Debemos ejecutar todo esto con el backend corriendo.


## Mensajería

Se utiliza RabbitMQ con un exchange tipo topic llamado:

- `invernadero.events`

Principales routing keys:

- `sensor.lectura.registrada`
- `alarma.disparada`

Colas:

- `alarm-evaluator.sensor-readings`
- `historical.sensor-readings`
- `notificador.alarmas`
    
## Problemas comunes

- RabbitMQ no levanta → verificar `docker ps`
- Puerto ocupado → cambiar puertos en configuración
- DB no existe → crear manualmente antes de correr backend
- No se crean colas → revisar que los consumidores estén corriendo antes del publisher

## Equipo de desarrollo

| Nombre | Rol | Contribución |
|------|-----|-------------|
| Christopher Álvarez | Backend Developer | Implementación de AlarmEvaluatorService y comunicación gRPC |
| Pedro Morales | Frontend & Messaging | Desarrollo del frontend y configuración de RabbitMQ |
| Juan Heras | Backend Developer | Desarrollo de NotificadorAlarmas, API backend (SQL) e integración con gRPC |
| Jack Murrieta | Backend Developer | Implementación de SensorDataAdapter y apoyo en integración con RabbitMQ |
| Alejandro Valdéz | Backend Developer | Desarrollo de HistoricalDataService (NoSQL) y API para análisis/BI |
