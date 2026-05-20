/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package TCPServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author JackMurrieta
 */
public class TCPServer implements ITCPServer {

    private int puerto;
    private ServerSocket socket;
    //pooll de hilos usar import java.util.concurrent.ExecutorService;
    private ExecutorService poolDeHilos;
    //buffersize de mensajes o paquetes que son en binario
    private int bufferSize;
    //uso de archivos.properties para la configuracion de servidor, puertos, etc.
    private Properties configuracion;
    private String sensorAdapterHost;
    private int sensorAdapterPort;
    private volatile boolean servidorActivo = false;

    /**
     * Constructor que carga la configuración desde el archivo properties
     */
    public TCPServer() {
        cargarConfiguracion();
    }

    /**
     * Carga la configuración desde el archivo server.properties
     */
    private void cargarConfiguracion() {
        configuracion = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("server.properties")) {

            if (input == null) {
                System.out.println("No se pudo encontrar server.properties, usando valores por defecto");
                configuracionPorDefecto();
                return;
            }

            configuracion.load(input);

            // Cargar configuración del servidor
            this.puerto = Integer.parseInt(configuracion.getProperty("server.port", "8080"));
            this.bufferSize = Integer.parseInt(configuracion.getProperty("server.buffer.size", "8192"));
            int threadPoolSize = Integer.parseInt(configuracion.getProperty("server.threadpool.size", "10"));

            // Cargar configuración del Sensor Adapter
            this.sensorAdapterHost = configuracion.getProperty("sensoradapter.host", "localhost");
            this.sensorAdapterPort = Integer.parseInt(configuracion.getProperty("sensoradapter.port", "9090"));

            // Inicializar pool de hilos
            this.poolDeHilos = Executors.newFixedThreadPool(threadPoolSize);

            System.out.println("Configuración cargada desde server.properties:");
            System.out.println("  Puerto servidor: " + this.puerto);
            System.out.println("  Buffer size: " + this.bufferSize + " bytes");
            System.out.println("  Pool de hilos: " + threadPoolSize);
            System.out.println("  Sensor Adapter: " + this.sensorAdapterHost + ":" + this.sensorAdapterPort);

        } catch (IOException ex) {
            System.err.println("Error al cargar configuración: " + ex.getMessage());
            configuracionPorDefecto();
        }
    }

    /**
     * Configura valores por defecto si no se encuentra el archivo properties
     */
    private void configuracionPorDefecto() {
        this.puerto = 8080;
        this.bufferSize = 8192;
        this.poolDeHilos = Executors.newFixedThreadPool(10);
        this.sensorAdapterHost = "localhost";
        this.sensorAdapterPort = 9090;
        System.out.println("Usando configuración por defecto");
    }

    //metodo encargado de correr el servidor usa pool de hilos con executorService
    @Override
    public void iniciar() {
        try {
            socket = new ServerSocket(puerto);
            servidorActivo = true;
            System.out.println("=================================================");
            System.out.println("Servidor TCP iniciado en puerto: " + puerto);
            System.out.println("Esperando conexiones de clientes...");
            System.out.println("=================================================");

            // Loop principal del servidor - acepta conexiones de clientes
            while (servidorActivo) {
                try {
                    Socket cliente = socket.accept();
                    System.out.println("\n[NUEVA CONEXION] Cliente conectado desde: "
                            + cliente.getInetAddress().getHostAddress()
                            + ":" + cliente.getPort());

                    // Delegar el manejo del cliente al pool de hilos
                    poolDeHilos.execute(() -> manejarCliente(cliente));

                } catch (IOException e) {
                    if (servidorActivo) {
                        System.err.println("Error al aceptar cliente: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            detenerServidor();
        }
    }

    /**
     * Maneja la comunicación con un cliente individual
     * Se ejecuta en un hilo del pool
     */
    private void manejarCliente(Socket cliente) {
        try (InputStream input = cliente.getInputStream();
             OutputStream output = cliente.getOutputStream()) {

            byte[] buffer = new byte[bufferSize];
            int bytesLeidos;

            while ((bytesLeidos = input.read(buffer)) != -1) {
                // Copiar los datos recibidos
                byte[] paqueteRecibido = new byte[bytesLeidos];
                System.arraycopy(buffer, 0, paqueteRecibido, 0, bytesLeidos);

                // Llamar al método recibirPaquete
                recibirPaquete();

                // Mostrar información del paquete binario
                System.out.println("Paquete binario recibido (" + bytesLeidos + " bytes)");
                System.out.println("Datos (hex): " + bytesToHex(paqueteRecibido));

                // Enviar el paquete al Sensor Adapter
                enviarPaquete(paqueteRecibido);
            }

        } catch (IOException e) {
            System.err.println("Error al manejar cliente: " + e.getMessage());
        } finally {
            try {
                cliente.close();
                System.out.println("[DESCONEXION] Cliente desconectado");
            } catch (IOException e) {
                System.err.println("Error al cerrar socket del cliente: " + e.getMessage());
            }
        }
    }

    //metodo que recibe el paquete solo se muestra en un sout que si llego al servidor
    @Override
    public void recibirPaquete() {
        System.out.println("[RECEPCION] Paquete recibido correctamente en el servidor TCP");
    }

    /**
     * Envía el paquete binario al Sensor Adapter
     * Establece una conexión TCP con el Adapter y envía los datos
     */
    @Override
    public void enviarPaquete(byte[] paquete) {
        Socket socketAdapter = null;
        try {
            // Conectar al Sensor Adapter
            socketAdapter = new Socket(sensorAdapterHost, sensorAdapterPort);

            // Enviar el paquete binario
            OutputStream output = socketAdapter.getOutputStream();
            output.write(paquete);
            output.flush();

            // Esperar confirmación (ACK)
            InputStream input = socketAdapter.getInputStream();
            byte[] ackBuffer = new byte[3];
            int bytesRead = input.read(ackBuffer);

            if (bytesRead > 0) {
                String ack = new String(ackBuffer, 0, bytesRead);
                System.out.println("[REENVIO] Paquete enviado al Sensor Adapter - Respuesta: " + ack);
            }

        } catch (IOException e) {
            System.err.println("[ERROR] Error al enviar paquete al Sensor Adapter: " + e.getMessage());
        } finally {
            // Cerrar la conexión con el Adapter
            if (socketAdapter != null) {
                try {
                    socketAdapter.close();
                } catch (IOException e) {
                    System.err.println("[ERROR] Error al cerrar conexión con Adapter: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Detiene el servidor de forma ordenada
     */
    private void detenerServidor() {
        try {
            servidorActivo = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (poolDeHilos != null) {
                poolDeHilos.shutdown();
            }
            System.out.println("\nServidor TCP detenido");
        } catch (IOException e) {
            System.err.println("Error al detener servidor: " + e.getMessage());
        }
    }

    /**
     * Convierte bytes a representación hexadecimal para visualización
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    /**
     * Método main para ejecutar el servidor
     */
    public static void main(String[] args) {
        TCPServer servidor = new TCPServer();
        servidor.iniciar();
    }
}
