/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

/**
 * Clase modelo para enviar una alarma hacia RabbitMQ
 *
 * @author Juan Pablo Heras
 */
public record AlarmaDisparadaEvent(
        String idInvernadero,
        String idSensor,
        int idConfiguracionAlarma,
        String nombreAlarma,
        String tipoAlarma,
        String operador,
        double valorCritico,
        double valorLeido,
        String fechaLectura,
        String mensaje) {

}
