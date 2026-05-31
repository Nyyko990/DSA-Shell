/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectoavance1;

/**
 * Representa un bus del sistema de atención al cliente.
 * <p>
 * Cada bus tiene un inspector que puede atender un ticket a la vez,
 * y mantiene una fila de espera para los tickets que están aguardando su turno.
 * </p>
 * 
 * @author lcast
 */
public class Bus {
    private int numeroBus;
    private char tipoBus;
    private boolean inspectorOcupado;
    private Ticket ticketEnAtencion;
    private ListaTickets filaEspera;

    /**
     * Constructor del bus
     * <p>
     * Inicializa el bus con su número y tipo. Al crearlo, el inspector
     * comienza libre y la fila de espera está vacía.
     * </p>
     * @param numeroBus Número identificador del bus
     * @param tipoBus Tipo de bus
     */
    public Bus(int numeroBus, char tipoBus) {
        this.numeroBus = numeroBus;
        this.tipoBus = tipoBus;
        this.inspectorOcupado = false;
        this.ticketEnAtencion = null;
        this.filaEspera = new ListaTickets();
    }

    public int getNumeroBus() {
        return numeroBus;
    }

    public char getTipoBus() {
        return tipoBus;
    }

    public boolean isInspectorOcupado() {
        return inspectorOcupado;
    }

    public Ticket getTicketEnAtencion() {
        return ticketEnAtencion;
    }

    public ListaTickets getFilaEspera() {
        return filaEspera;
    }

    /**
     * Verifica si el bus está disponible para recibir atención directa.
     * <p>
     * Un bus está libre cuando el inspector no está ocupado
     * y no hay un ticket en atención actualmente.
     * </p>
     * @return {@code true} si el bus está libre, {@code false} si está ocupado
     */
    public boolean estaLibre() {
        return !inspectorOcupado && ticketEnAtencion == null;
    }

     /**
     * Asigna un ticket para atención directa en el bus.
     * <p>
     * El método verifica que el ticket no sea nulo antes de asignarlo.
     * Si el objeto proporcionado es {@code null}, la operación se ignora.
     * Al asignarlo el ticket se marca como "en atención".
     * </p>
     * @param ticket El objeto {@link Ticket} que será atendido directamente
     */
    public void asignarAtencionDirecta(Ticket ticket) {
        if (ticket == null) {
            return;
        }

        ticketEnAtencion = ticket;
        inspectorOcupado = true;
        ticketEnAtencion.marcarEnAtencion();
    }

    /**
     * Agrega un ticket a la fila de espera del bus.
     * <p>
     * El método verifica que el ticket no sea nulo antes de agregarlo.
     * Si el objeto proporcionado es {@code null}, la operación se ignora.
     * </p>
     * @param ticket El objeto {@link Ticket} que se pondrá en la fila de espera
     */
    public void agregarAFila(Ticket ticket) {
        if (ticket == null) {
            return;
        }

        filaEspera.agregar(ticket);
    }

    /**
     * Finaliza la atención del ticket actual.
     * <p>
     * Libera al inspector y retorna el ticket que estaba siendo atendido.
     * Después de este método, el bus queda libre para atender el siguiente ticket.
     * </p>
     * @return El ticket que estaba siendo atendido, o {@code null} si no había ninguno
     */
    public Ticket finalizarAtencion() {
        Ticket ticketFinalizado = ticketEnAtencion;
        ticketEnAtencion = null;
        inspectorOcupado = false;
        return ticketFinalizado;
    }

    public int cantidadEnFila() {
        return filaEspera.tamano();
    }

    /**
     * Verifica si el bus está completamente vacío.
     * <p>
     * Un bus está vacío cuando no tiene ningún ticket en atención
     * y la fila de espera no tiene tickets pendientes.
     * </p>
     * @return {@code true} si no hay tickets en atención ni en fila, {@code false} si hay al menos un ticket pendiente o en atención
     */
    public boolean estaCompletamenteVacio(){
        return ticketEnAtencion == null && filaEspera.estaVacia();
    }

    /**
     * Genera un resumen con el estado actual del bus.
     * <p>
     * El resumen incluye el número de bus, su tipo, si está atendiendo a alguien,
     * el nombre del ticket en atención si aplica, y la cantidad de tickets en fila.
     * </p>
     * @return Una cadena de texto con la información resumida del bus
     */
    public String resumenBus() {
        String texto = "Bus # " + numeroBus + " | Tipo= " + tipoBus;

        if (inspectorOcupado && ticketEnAtencion != null) {
            texto += " | En atencion: " + ticketEnAtencion.getNombre();
        } else {
            texto += " | Sin atencion actual";
        }

        texto += " | En fila: " + filaEspera.tamano();

        return texto;
    }
}
