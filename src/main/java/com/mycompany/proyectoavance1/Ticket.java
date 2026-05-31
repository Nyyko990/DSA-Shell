/**
 * Representa un ticket o tiquete generado por el sistema
 * para un pasajero de la terminal.
 * <p>
 * Contiene información personal del cliente, datos del servicio,
 * tipo de bus, moneda y tiempos de compra y abordaje.
 * </p>
 */
package com.mycompany.proyectoavance1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    
    public static String obtenerFecha() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private String nombre;
    private int id;
    private int edad;
    private String monedaCuenta;
    private String horaCompra;
    private String fechaHoraAbordaje;
    private String servicio;
    private char tipoBus;
    private String estadoTicket;
    private String fechaHoraAtencion;
    private String terminalCompra;
    private int numeroBusAbordado;
    private double montoCobrado;
    private boolean pagado;

    public Ticket() {
        fechaHoraAbordaje = "NA";
        fechaHoraAtencion = "NA";
        terminalCompra = "";
        numeroBusAbordado = 0;
        montoCobrado = 0;
        pagado = false;
        estadoTicket = "Pendiente";
    }
        /**
     * Crea un nuevo ticket con la información básica del cliente.
     *
     * @param nombre nombre del cliente.
     * @param id identificación del cliente.
     * @param edad edad del cliente.
     * @param moneda moneda de pago.
     * @param servicio tipo de servicio solicitado.
     * @param tipo tipo de servicio asignado.
     * @return nuevo objeto {@link Ticket}.
     */
    public static Ticket crearNuevo(String nombre, int id, int edad, String moneda, String servicio, char tipo) {
        Ticket nuevoTicket = new Ticket();
        nuevoTicket.nombre = nombre;
        nuevoTicket.id = id;
        nuevoTicket.edad = edad;
        nuevoTicket.monedaCuenta = moneda;
        nuevoTicket.servicio = servicio;
        nuevoTicket.tipoBus = tipo;
        nuevoTicket.horaCompra = obtenerFecha();
        nuevoTicket.fechaHoraAbordaje = "NA";
        nuevoTicket.fechaHoraAtencion = "NA";
        nuevoTicket.pagado = false;
        nuevoTicket.numeroBusAbordado = 0;
        nuevoTicket.montoCobrado = 0;
        nuevoTicket.estadoTicket = "Pendiente";
        return nuevoTicket;
    }

    public void marcarAbordajeAhora() {
        fechaHoraAbordaje = obtenerFecha();
    }
    
    public void marcarAtencion(String terminalCompra, int numeroBusAbordado, double montoCobrado, boolean pagado) {
        this.fechaHoraAtencion = obtenerFecha();
        this.fechaHoraAbordaje = this.fechaHoraAtencion;
        this.terminalCompra = terminalCompra;
        this.numeroBusAbordado = numeroBusAbordado;
        this.montoCobrado = montoCobrado;
        this.pagado = pagado;

        if (pagado) {
            this.estadoTicket = "Atendido";
        } else {
            this.estadoTicket = "No Pagado";
        }
    }

    /**
     * Marca la atención del ticket.
     * @param terminalCompra Terminal donde se realizó la compra
     * @param numeroBusAbordado Número del bus que atendió al cliente
     * @param montoCobrado Monto cobrado por el servicio
     * @param pagado {@code true} si el cliente pagó, {@code false} en caso contrario
     */
    public void marcarEnAtencion() {
        estadoTicket = "En Atencion";
    }

    public String resumen() {
        return "Nombre = " + nombre +
                " \nID = " + id +
                " \nEdad = " + edad +
                " \nMoneda = " + monedaCuenta +
                " \nCompra = " + horaCompra +
                " \nAbordaje = " + fechaHoraAbordaje +
                " \nServicio = " + servicio +
                " \nTipoBus = " + tipoBus;
    }

    public int getId() { 
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public String getMonedaCuenta() {
        return monedaCuenta;
    }

    public String getFechaHoraCompra() {
        return horaCompra;
    }

    public String getFechaHoraAbordaje() {
        return fechaHoraAbordaje;
    }

    public String getServicio() {
        return servicio;
    }

    public char getTipoBus() {
        return tipoBus;
    }

    public String getEstadoTicket() {
        return estadoTicket;
    }

    public String getFechaHoraAtencion() {
        return fechaHoraAtencion;
    }

    public String getTerminalCompra() {
        return terminalCompra;
    }

    public int getNumeroBusAbordado() {
        return numeroBusAbordado;
    }

    public double getMontoCobrado() {
        return montoCobrado;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setTerminalCompra(String terminalCompra) {
        this.terminalCompra = terminalCompra;
    }
}
