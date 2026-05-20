# ─────────────────────────────────────────────
# Stage 1: Build — compila el módulo Quarkus
# Contexto esperado: sensorIngestionServer/  (el directorio padre del módulo)
# ─────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# 1. Parent POM primero (capa de caché separada)
COPY pom.xml ./pom.xml

# 2. POM del módulo (solo el descriptor, antes del source para aprovechar caché de deps)
COPY SensorDataAdapter/pom.xml SensorDataAdapter/pom.xml

# 3. Instalar el parent pom en el repo local para que el módulo lo encuentre
RUN mvn install -N -q

# 4. Descargar dependencias del módulo (capa de caché; solo se invalida si cambia el pom)
RUN mvn -f SensorDataAdapter/pom.xml dependency:go-offline -q

# 5. Copiar fuentes y compilar
COPY SensorDataAdapter/src SensorDataAdapter/src
RUN mvn -f SensorDataAdapter/pom.xml package -DskipTests -q

# ─────────────────────────────────────────────
# Stage 2: Runtime — imagen mínima JRE Alpine
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Quarkus fast-jar: requiere lib/, app/, quarkus/ junto al launcher
COPY --from=builder /workspace/SensorDataAdapter/target/quarkus-app/lib/     ./lib/
COPY --from=builder /workspace/SensorDataAdapter/target/quarkus-app/*.jar    ./
COPY --from=builder /workspace/SensorDataAdapter/target/quarkus-app/app/     ./app/
COPY --from=builder /workspace/SensorDataAdapter/target/quarkus-app/quarkus/ ./quarkus/

# 9090 = servidor TCP sensores | 8081 = HTTP Quarkus (health/metrics)
EXPOSE 9090 8081

CMD ["java", "-jar", "quarkus-run.jar"]
