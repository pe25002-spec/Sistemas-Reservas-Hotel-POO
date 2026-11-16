/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hotel.vista;

import com.hotel.controlador.GestorHotel;
import com.hotel.modelo.*;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;

public class SistemaHotelFrame extends JFrame {
    private GestorHotel gestor;
    private DefaultTableModel modClientes, modHabitaciones, modReservas;

    public SistemaHotelFrame(GestorHotel gestor) {
        this.gestor = gestor;
        setTitle("Sistema de Gestion de Reservas");
        setSize(850, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("1. Gestion de Clientes", crearPanelClientes());
        tabs.addTab("2. Gestion de Habitaciones", crearPanelHabitaciones());
        tabs.addTab("3. Gestion de Reservas y Costos", crearPanelReservas());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalir = new JButton("Cerrar Sesion");
        btnSalir.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        topBar.add(btnSalir);

        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        cargarTablas();
    }

    //PESTAÑA CLIENTES
    private JPanel crearPanelClientes() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

        JTextField txtCod = new JTextField(), txtNom = new JTextField(), txtDoc = new JTextField(), txtTel = new JTextField(), txtMail = new JTextField();
        JButton btnGuardar = new JButton("Registrar Cliente");
        JButton btnEliminar = new JButton("Eliminar Seleccionado");

        form.add(new JLabel("Codigo:")); form.add(txtCod);
        form.add(new JLabel("Nombre Completo:")); form.add(txtNom);
        form.add(new JLabel("Documento (DUI/Pas):")); form.add(txtDoc);
        form.add(new JLabel("Telefono:")); form.add(txtTel);
        form.add(new JLabel("Correo:")); form.add(txtMail);
        form.add(btnGuardar); form.add(btnEliminar);

        modClientes = new DefaultTableModel(new String[]{"Codigo", "Nombre", "Documento", "Telefono", "Correo"}, 0);
        JTable tabla = new JTable(modClientes);

        btnGuardar.addActionListener(e -> {
            if (txtCod.getText().trim().isEmpty() || txtNom.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Codigo y Nombre son obligatorios.");
                return;
            }
            Cliente c = new Cliente(txtCod.getText().trim(), txtNom.getText().trim(), txtDoc.getText().trim(), txtTel.getText().trim(), txtMail.getText().trim());
            if (gestor.registrarCliente(c)) {
                JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente.");
                txtCod.setText(""); txtNom.setText(""); txtDoc.setText(""); txtTel.setText(""); txtMail.setText("");
                cargarTablas();
            } else {
                JOptionPane.showMessageDialog(this, "El codigo de cliente ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row == -1) return;
            String cod = modClientes.getValueAt(row, 0).toString();
            if (gestor.eliminarCliente(cod)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado.");
                cargarTablas();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: posee reservas asociadas.", "Restriccion", JOptionPane.WARNING_MESSAGE);
            }
        });

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return p;
    }

    //PESTAÑA HABITACIONES
    private JPanel crearPanelHabitaciones() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(4, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Datos de la Habitacion"));

        JTextField txtCod = new JTextField(), txtNum = new JTextField(), txtCap = new JTextField(), txtPrecio = new JTextField();
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Estandar", "Suite"});
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Disponible", "Fuera de servicio"});
        JButton btnGuardar = new JButton("Registrar Habitacion");
        JButton btnCambiarEstado = new JButton("Cambiar Estado");

        form.add(new JLabel("Codigo Hab:")); form.add(txtCod);
        form.add(new JLabel("Numero:")); form.add(txtNum);
        form.add(new JLabel("Tipo:")); form.add(cmbTipo);
        form.add(new JLabel("Capacidad:")); form.add(txtCap);
        form.add(new JLabel("Precio/Noche ($):")); form.add(txtPrecio);
        form.add(new JLabel("Estado:")); form.add(cmbEstado);
        form.add(btnGuardar); form.add(btnCambiarEstado);

        modHabitaciones = new DefaultTableModel(new String[]{"Codigo", "Numero", "Tipo", "Capacidad", "Precio/Noche", "Estado"}, 0);
        JTable tabla = new JTable(modHabitaciones);

        btnGuardar.addActionListener(e -> {
            try {
                int num = Integer.parseInt(txtNum.getText().trim());
                int cap = Integer.parseInt(txtCap.getText().trim());
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                if (cap <= 0 || precio < 0) throw new NumberFormatException();

                String tipo = cmbTipo.getSelectedItem().toString();
                Habitacion h = tipo.equals("Suite")
                        ? new HabitacionSuite(txtCod.getText().trim(), num, cap, precio, cmbEstado.getSelectedItem().toString())
                        : new HabitacionEstandar(txtCod.getText().trim(), num, cap, precio, cmbEstado.getSelectedItem().toString());

                if (gestor.registrarHabitacion(h)) {
                    JOptionPane.showMessageDialog(this, "Habitacion registrada.");
                    cargarTablas();
                } else {
                    JOptionPane.showMessageDialog(this, "Codigo duplicado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese numeros validos positivos para Numero, Capacidad y Precio.");
            }
        });

        btnCambiarEstado.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row == -1) return;
            String cod = modHabitaciones.getValueAt(row, 0).toString();
            String nuevo = cmbEstado.getSelectedItem().toString();
            gestor.cambiarEstadoHabitacion(cod, nuevo);
            cargarTablas();
        });

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return p;
    }

    //PESTAÑA RESERVAS
    private JPanel crearPanelReservas() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(4, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nueva Reserva (Formato Fecha: DD-MM-AAAA)"));

        JTextField txtCodRes = new JTextField(), txtCodCli = new JTextField(), txtCodHab = new JTextField(), txtHuesp = new JTextField();
        JTextField txtEntrada = new JTextField("20-11-2026"), txtSalida = new JTextField("23-11-2026");
        JButton btnReservar = new JButton("Confirmar Reserva");
        JButton btnCancelar = new JButton("Cancelar Seleccionada");

        form.add(new JLabel("Codigo Reserva:")); form.add(txtCodRes);
        form.add(new JLabel("Codigo Cliente:")); form.add(txtCodCli);
        form.add(new JLabel("Codigo Habitación:")); form.add(txtCodHab);
        form.add(new JLabel("Cant. Huespedes:")); form.add(txtHuesp);
        form.add(new JLabel("Fecha Entrada:")); form.add(txtEntrada);
        form.add(new JLabel("Fecha Salida:")); form.add(txtSalida);
        form.add(btnReservar); form.add(btnCancelar);

        modReservas = new DefaultTableModel(new String[]{"Cod. Reserva", "Cliente", "Habitacion", "Entrada", "Salida", "Estado", "Costo Total ($)"}, 0);
        JTable tabla = new JTable(modReservas);

        btnReservar.addActionListener(e -> {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate in = LocalDate.parse(txtEntrada.getText().trim(),formato);
                LocalDate out = LocalDate.parse(txtSalida.getText().trim(),formato);
                int huesp = Integer.parseInt(txtHuesp.getText().trim());

                String res = gestor.registrarReserva(txtCodRes.getText().trim(), txtCodCli.getText().trim(), txtCodHab.getText().trim(), huesp, in, out);
                if (res.startsWith("OK:")) {
                    JOptionPane.showMessageDialog(this, "Reserva confirmada.\nCosto Total: $" + res.substring(3));
                    cargarTablas();
                } else {
                    JOptionPane.showMessageDialog(this, res, "Error de Validacion", JOptionPane.ERROR_MESSAGE);
                }
            } catch (DateTimeParseException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Verifique el formato de fechas (DD-MM-AAAA) y huespedes.");
            }
        });

        btnCancelar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row == -1) return;
            String cod = modReservas.getValueAt(row, 0).toString();
            if (gestor.cancelarReserva(cod)) {
                JOptionPane.showMessageDialog(this, "Reserva cancelada.");
                cargarTablas();
            } else {
                JOptionPane.showMessageDialog(this, "La reserva ya estaba cancelada o no existe.");
            }
        });

        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return p;
    }

    private void cargarTablas() {
        modClientes.setRowCount(0);
        for (Cliente c : gestor.listarClientes()) {
            modClientes.addRow(new Object[]{c.getCodigoCliente(), c.getNombreCompleto(), c.getDocumentoIdentidad(), c.getTelefono(), c.getCorreoElectronico()});
        }

        modHabitaciones.setRowCount(0);
        for (Habitacion h : gestor.listarHabitaciones()) {
            modHabitaciones.addRow(new Object[]{h.getCodigoHabitacion(), h.getNumero(), h.getTipo(), h.getCapacidad(), h.getPrecioPorNoche(), h.getEstado()});
        }

        modReservas.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        for (Document d : gestor.listarReservas()) {
            modReservas.addRow(new Object[]{
                    d.getString("codigoReserva"),
                    d.getString("codigoCliente"),
                    d.getString("codigoHabitacion"),
                    sdf.format(d.getDate("fechaEntrada")),
                    sdf.format(d.getDate("fechaSalida")),
                    d.getString("estado"),
                    d.getDouble("costoTotal")
            });
        }
    }
}

