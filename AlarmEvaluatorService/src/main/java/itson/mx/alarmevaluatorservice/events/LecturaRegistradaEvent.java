/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.mx.alarmevaluatorservice.events;

/**
 * Clase modelo para recibir una lectura desde RabbitMQ
 *
 * @author Juan Pablo Heras
 */
public record LecturaRegistradaEvent(
        String idInvernadero,
        String idSensor,
        String tipoLectura,
        double valor,
        String fecha) {

}
