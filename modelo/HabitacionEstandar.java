/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.modelo;

/**
 *
 * @author ordon
 */
public class HabitacionEstandar extends Habitacion {
    public HabitacionEstandar(String codigoHabitacion, int numero, int capacidad, double precioPorNoche, String estado) {
        super(codigoHabitacion, numero, "Estandar", capacidad, precioPorNoche, estado);
    }

    @Override
    public double calcularCostoTotal(int noches) {
        return noches * precioPorNoche;
    }
}

