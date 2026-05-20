/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package SensorAdapter;

import org.itson.adapter.model.SensorEvent;

/**
 *
 * @author Jack Murrieta
 */
public interface ISensorAdapter {
    SensorEvent adaptar(byte[] paquete);
    String getTipo();  // "A", "B", "C"
    
}
