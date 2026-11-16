/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.modelo;

/**
 *
 * @author ordon
 */
public abstract class Habitacion {
    protected String codigoHabitacion;
    protected int numero;
    protected String tipo;
    protected int capacidad;
    protected double precioPorNoche;
    protected String estado;

    public Habitacion(String codigoHabitacion, int numero, String tipo, int capacidad, double precioPorNoche, String estado) {
        this.codigoHabitacion = codigoHabitacion;
        this.numero = numero;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precioPorNoche = precioPorNoche;
        this.estado = estado;
    }

    public abstract double calcularCostoTotal(int noches);

    public String getCodigoHabitacion() { return codigoHabitacion; }
    public int getNumero() { return numero; }
    public String getTipo() { return tipo; }
    public int getCapacidad() { return capacidad; }
    public double getPrecioPorNoche() { return precioPorNoche; }
    public String getEstado() { return estado; }
}

