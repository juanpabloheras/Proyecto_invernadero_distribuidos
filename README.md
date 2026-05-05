# Proyecto_invernadero_distribuidos

Sistema distribuido orientado a eventos para el monitoreo de sensores en invernaderos, capaz de procesar lecturas en tiempo real, evaluar reglas de negocio y generar notificaciones mediante una arquitectura desacoplada basada en mensajería.

El sistema recibe datos desde dispositivos físicos a través de un adaptador TCP, los publica en RabbitMQ y los distribuye a múltiples servicios consumidores. Un componente especializado evalúa condiciones de alarma utilizando configuraciones dinámicas en caché, mientras que otros servicios permiten la persistencia histórica y el envío de notificaciones por distintos medios (SSE, email, SMS).

- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Prerrequisitos](#prerrequisitos)
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
    ``` Y deberíamos ver algo como lo siguiente si es correcto

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
7. Una vez con los consumidores levantados, podemos revisar en la interfaz de RabbitMQ en el apartado de **Queues and Streams** que se hayan creado las colas `alarm-evaluator.sensor-readings`  y `notificador.alarmas` además de en la sección **Exchanges** que aparezca `invernadero.events`.
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
