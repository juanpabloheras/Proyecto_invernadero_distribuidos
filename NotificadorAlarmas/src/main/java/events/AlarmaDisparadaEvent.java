/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

import java.util.List;

/**
 *
 * @author juanpheras
 */
public record AlarmaDisparadaEvent(
        String tipoEvento,
        String mensaje,
        List<String> mediosNotificacion) {
}
