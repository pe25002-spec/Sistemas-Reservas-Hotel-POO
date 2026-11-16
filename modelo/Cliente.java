/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.modelo;

/**
 *
 * @author ordon
 */
public class Cliente {
    private String codigoCliente;
    private String nombreCompleto;
    private String documentoIdentidad;
    private String telefono;
    private String correoElectronico;

    public Cliente(String codigoCliente, String nombreCompleto, String documentoIdentidad, String telefono, String correoElectronico) {
        this.codigoCliente = codigoCliente;
        this.nombreCompleto = nombreCompleto;
        this.documentoIdentidad = documentoIdentidad;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
    }

    public String getCodigoCliente() { return codigoCliente; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public String getTelefono() { return telefono; }
    public String getCorreoElectronico() { return correoElectronico; }
}

