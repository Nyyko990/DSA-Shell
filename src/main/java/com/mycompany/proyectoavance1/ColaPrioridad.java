/**
 * Estructura encargada de administrar tickets en colas
 * según su prioridad o tipoBusTicket de bus.
 * <p>
 * El sistema utiliza tres colas:
 * preferencial, directo y normal.
 * Al momento de atender, siempre se da prioridad
 * primero a preferencial, luego directo y finalmente normal.
 * </p>
 */
package com.mycompany.proyectoavance1;
public class ColaPrioridad {

    private NodoTicket cabezaPreferencial, colaPreferencial;
    private NodoTicket cabezaDirecto, colaDirecto;
    private NodoTicket cabezaNormal, colaNormal;
    private int tamanoColaPreferencial, tamanoColaDirecto, tamanoColaNormal;

    public ColaPrioridad() {
        tamanoColaPreferencial = 0; tamanoColaDirecto = 0; tamanoColaNormal = 0;
    }

     /**
     * Agrega un ticket a la cola según su tipo de prioridad.
     * <p>
     * El método verifica que el ticket no sea nulo antes de encolarlo.
     * Si el objeto proporcionado es {@code null}, la operación se ignora.
     * Los tickets tipo 'P' van a la cola preferencial, tipo 'D' a directo,
     * y cualquier otro tipo a la cola normal.
     * </p>
     * @param ticket El objeto {@link Ticket} que se desea encolar
     */
    public void encolar(Ticket ticket) {
        if (ticket == null){
            return;
        }
        char tipoBusTicket = ticket.getTipoBus();
        if (tipoBusTicket == 'P'){
            encolarPreferencial(ticket); 
        }else if (tipoBusTicket == 'D') {
            encolarDirecto(ticket);
        }else{
            encolarNormal(ticket);
        }
        
    }

    /**
     * Extrae el siguiente ticket según el orden de prioridad.
     * La prioridad es: Preferencial primero, luego Directo, y finalmente Normal.
     * Si no hay tickets en ninguna cola, retorna {@code null}.
     * @return El siguiente ticket en la cola de prioridad, o {@code null} si todas las colas están vacías
     */
    public Ticket desencolar() {
        if (cabezaPreferencial != null) 
            return desencolarP();
        if (cabezaDirecto != null) 
            return desencolarD();
        if (cabezaNormal != null) 
            return desencolarN();
        return null;
    }

    public int tamanoPreferencial() { return tamanoColaPreferencial; }
    public int tamanoDirecto() { return tamanoColaDirecto; }
    public int tamanoNormal() { return tamanoColaNormal; }

    /**
     * Muestra una vista previa de los primeros tickets en cada cola.
     * <p>
     * Para cada nivel de prioridad, muestra hasta la cantidad máxima
     * especificada de tickets que están esperando.
     * </p>
     * @param cantidadMaxima Número máximo de tickets a mostrar por cada cola
     * @return Una cadena con el resumen de los tickets en espera
     */
    public String vistaPrevia(int cantidadMaxima) {
        String string = "";
        string += "Preferencial:\n" + obtenerTopCola(cabezaPreferencial, cantidadMaxima);
        string += "Directo:\n" + obtenerTopCola(cabezaDirecto, cantidadMaxima);
        string += "Normal:\n" + obtenerTopCola(cabezaNormal, cantidadMaxima);
        return string;
    }

    /**
     * Obtiene los primeros tickets de una cola específica.
     * @param cabeza El primer nodo de la cola a inspeccionar
     * @param cantidadMaxima Número máximo de tickets a mostrar
     * @return Una cadena con los tickets encontrados, o "(vacio)" si no hay ninguno
     */
    private String obtenerTopCola(NodoTicket cabeza, int cantidadMaxima) {
        String resultado = "";
        int contador = 0;
        NodoTicket nodoActual = cabeza;

        while (nodoActual != null && contador < cantidadMaxima) {
            resultado += " - " + nodoActual.getValor().resumen() + "\n";
            nodoActual = nodoActual.getSiguiente();
            contador++;
        }

        if (contador == 0){ 
            resultado += " - (vacio)\n";
        }
            return resultado;
    }

    /**
     * Agrega un ticket a la cola preferencial.
     * @param ticket El ticket a encolar en preferencial
     */
    private void encolarPreferencial(Ticket ticket) {
        NodoTicket nuevoNodo = new NodoTicket(ticket);
        if (cabezaPreferencial == null){
            cabezaPreferencial = colaPreferencial = nuevoNodo; 
        }else{ 
            colaPreferencial.setSiguiente(nuevoNodo); colaPreferencial = nuevoNodo; }
        tamanoColaPreferencial++;
    }

    /**
     * Agrega un ticket a la cola directo.
     * @param ticket El ticket a encolar en directo
     */
    private void encolarDirecto(Ticket ticket) {
        NodoTicket nuevoNodo = new NodoTicket(ticket);
        if (cabezaDirecto == null) { 
            cabezaDirecto = colaDirecto = nuevoNodo; 
        }else{ 
            colaDirecto.setSiguiente(nuevoNodo); colaDirecto = nuevoNodo; 
            }
        tamanoColaDirecto++;
    }

    /**
     * Agrega un ticket a la cola normal.
     * @param ticket El ticket a encolar en normal
     */
    private void encolarNormal(Ticket ticket) {
        NodoTicket nuevoNodo = new NodoTicket(ticket);
        if (cabezaNormal == null) {
            cabezaNormal = colaNormal = nuevoNodo;
        } else { 
            colaNormal.setSiguiente(nuevoNodo); colaNormal = nuevoNodo;
        }
        tamanoColaNormal++;
    }

    /**
     * Extrae un ticket de la cola preferencial.
     * @return El ticket que estaba al frente de la cola preferencial
     */
    private Ticket desencolarP() {
        Ticket ticket = cabezaPreferencial.getValor();
        cabezaPreferencial = cabezaPreferencial.getSiguiente();
        if (cabezaPreferencial == null) {
            colaPreferencial = null;
        }
        tamanoColaPreferencial--;
        return ticket;
    }

    /**
     * Extrae un ticket de la cola directo.
     * @return El ticket que estaba al frente de la cola directo
     */
    private Ticket desencolarD() {
        Ticket ticket = cabezaDirecto.getValor();
        cabezaDirecto = cabezaDirecto.getSiguiente();
        if (cabezaDirecto == null){
            colaDirecto = null;
        }
        tamanoColaDirecto--;
        return ticket;
    }

    /**
     * Extrae un ticket de la cola normal.
     * @return El ticket que estaba al frente de la cola normal
     */
    private Ticket desencolarN() {
        Ticket ticket = cabezaNormal.getValor();
        cabezaNormal = cabezaNormal.getSiguiente();
        if (cabezaNormal == null){
            colaNormal = null;
        }
        tamanoColaNormal--;
        return ticket;
    }
}
