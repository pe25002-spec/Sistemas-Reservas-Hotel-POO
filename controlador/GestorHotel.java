/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.controlador;

/**
 *
 * @author ordon
 */
import com.hotel.modelo.*;
import com.hotel.persistencia.ConexionMongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GestorHotel {

    private final MongoCollection<Document> colUsuarios;
    private final MongoCollection<Document> colClientes;
    private final MongoCollection<Document> colHabitaciones;
    private final MongoCollection<Document> colReservas;

    public GestorHotel() {
        MongoDatabase db = ConexionMongoDB.getDatabase();
        this.colUsuarios = db.getCollection("usuarios");
        this.colClientes = db.getCollection("clientes");
        this.colHabitaciones = db.getCollection("habitaciones");
        this.colReservas = db.getCollection("reservas");

        // Crea el usuario administrador inicial si la BD está vacía
        if (colUsuarios.countDocuments() == 0) {
            String hash = BCrypt.hashpw("admin123", BCrypt.gensalt());
            colUsuarios.insertOne(new Document("nombreUsuario", "admin").append("contrasenia", hash));
        }
    }

    // Inicio de sesión
    public boolean validarLogin(String usuario, String password) {
        Document doc = colUsuarios.find(Filters.eq("nombreUsuario", usuario)).first();
        if (doc != null) {
            return BCrypt.checkpw(password, doc.getString("contrasenia"));
        }
        return false;
    }

    //GESTIÓN DE CLIENTES
    public boolean registrarCliente(Cliente c) {
        if (colClientes.find(Filters.eq("codigoCliente", c.getCodigoCliente())).first() != null) {
            return false; // Código duplicado
        }
        Document doc = new Document("codigoCliente", c.getCodigoCliente())
                .append("nombreCompleto", c.getNombreCompleto())
                .append("documentoIdentidad", c.getDocumentoIdentidad())
                .append("telefono", c.getTelefono())
                .append("correoElectronico", c.getCorreoElectronico());
        colClientes.insertOne(doc);
        return true;
    }

    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>();
        for (Document d : colClientes.find()) {
            lista.add(new Cliente(
                    d.getString("codigoCliente"),
                    d.getString("nombreCompleto"),
                    d.getString("documentoIdentidad"),
                    d.getString("telefono"),
                    d.getString("correoElectronico")
            ));
        }
        return lista;
    }

    public boolean eliminarCliente(String codigoCliente) {
        if (colReservas.countDocuments(Filters.eq("codigoCliente", codigoCliente)) > 0) {
            return false; // Tiene historial de reservas
        }
        return colClientes.deleteOne(Filters.eq("codigoCliente", codigoCliente)).getDeletedCount() > 0;
    }

    //3. GESTIÓN DE HABITACIONES
    public boolean registrarHabitacion(Habitacion h) {
        if (colHabitaciones.find(Filters.eq("codigoHabitacion", h.getCodigoHabitacion())).first() != null) {
            return false;
        }
        Document doc = new Document("codigoHabitacion", h.getCodigoHabitacion())
                .append("numero", h.getNumero())
                .append("tipo", h.getTipo())
                .append("capacidad", h.getCapacidad())
                .append("precioPorNoche", h.getPrecioPorNoche())
                .append("estado", h.getEstado());
        colHabitaciones.insertOne(doc);
        return true;
    }

    public List<Habitacion> listarHabitaciones() {
        List<Habitacion> lista = new ArrayList<>();
        for (Document d : colHabitaciones.find()) {
            String tipo = d.getString("tipo");
            if ("Suite".equalsIgnoreCase(tipo)) {
                lista.add(new HabitacionSuite(d.getString("codigoHabitacion"), d.getInteger("numero"),
                        d.getInteger("capacidad"), d.getDouble("precioPorNoche"), d.getString("estado")));
            } else {
                lista.add(new HabitacionEstandar(d.getString("codigoHabitacion"), d.getInteger("numero"),
                        d.getInteger("capacidad"), d.getDouble("precioPorNoche"), d.getString("estado")));
            }
        }
        return lista;
    }

    public void cambiarEstadoHabitacion(String codigoHabitacion, String nuevoEstado) {
        colHabitaciones.updateOne(Filters.eq("codigoHabitacion", codigoHabitacion), Updates.set("estado", nuevoEstado));
    }

    //DISPONIBILIDAD Y RESERVAS
    public boolean verificarDisponibilidad(String codHab, LocalDate entrada, LocalDate salida) {
        if (!salida.isAfter(entrada)) return false;

        Document hab = colHabitaciones.find(Filters.eq("codigoHabitacion", codHab)).first();
        if (hab == null || "Fuera de servicio".equalsIgnoreCase(hab.getString("estado"))) {
            return false;
        }

        Date dEntrada = Date.from(entrada.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dSalida = Date.from(salida.atStartOfDay(ZoneId.systemDefault()).toInstant());

        long conflictos = colReservas.countDocuments(Filters.and(
                Filters.eq("codigoHabitacion", codHab),
                Filters.eq("estado", "Confirmada"),
                Filters.lt("fechaEntrada", dSalida),
                Filters.gt("fechaSalida", dEntrada)
        ));
        return conflictos == 0;
    }

    public String registrarReserva(String codReserva, String codCliente, String codHab, int huespedes, LocalDate entrada, LocalDate salida) {
        if (!salida.isAfter(entrada)) return "La fecha de salida debe ser posterior a la entrada.";
        if (colReservas.find(Filters.eq("codigoReserva", codReserva)).first() != null) return "El codigo de reserva ya existe.";
        if (colClientes.find(Filters.eq("codigoCliente", codCliente)).first() == null) return "El cliente no existe.";

        Document hab = colHabitaciones.find(Filters.eq("codigoHabitacion", codHab)).first();
        if (hab == null) return "La habitación no existe.";
        if (huespedes > hab.getInteger("capacidad")) return "Supera la capacidad maxima de la habitacion (" + hab.getInteger("capacidad") + ").";
        if (!verificarDisponibilidad(codHab, entrada, salida)) return "La habitacion no esta disponible o esta fuera de servicio en esas fechas.";

        long noches = ChronoUnit.DAYS.between(entrada, salida);
        double costoTotal = noches * hab.getDouble("precioPorNoche");

        Document doc = new Document("codigoReserva", codReserva)
                .append("codigoCliente", codCliente)
                .append("codigoHabitacion", codHab)
                .append("fechaEntrada", Date.from(entrada.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("fechaSalida", Date.from(salida.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("estado", "Confirmada")
                .append("costoTotal", costoTotal);

        colReservas.insertOne(doc);
        return "OK:" + costoTotal;
    }

    public List<Document> listarReservas() {
        return colReservas.find().into(new ArrayList<>());
    }

    public boolean cancelarReserva(String codReserva) {
        Document res = colReservas.find(Filters.eq("codigoReserva", codReserva)).first();
        if (res == null || "Cancelada".equalsIgnoreCase(res.getString("estado"))) return false;
        colReservas.updateOne(Filters.eq("codigoReserva", codReserva), Updates.set("estado", "Cancelada"));
        return true;
    }
} 

