/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package TCPServer;

import java.net.Socket;

/**
 *
 * @author Jack Murrieta
 */
public interface ITCPServer {

    // Todos son implícitamente public y abstract
    void iniciar();

    void enviarPaquete(byte[] paquete);

    void recibirPaquete();
}
