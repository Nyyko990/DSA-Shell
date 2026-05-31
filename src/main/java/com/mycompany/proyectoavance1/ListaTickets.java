package com.mycompany.proyectoavance1;
/**
* Lista enlazada para gestionar tickets.
* <p>
* Permite agregar tickets al final, obtenerlos por posición,
* reemplazar elementos y extraer el primer ticket de la lista.
* </p>
* 
* @author Nyko
*/
public class ListaTickets {
    private NodoTicketRepo cabeza;
    private int tamano;

    public ListaTickets() {
        cabeza = null;
        tamano = 0;
    }

    /**
     * Agrega un ticket al final de la lista.
     * <p>
     * El método verifica que el ticket no sea nulo antes de agregarlo.
     * Si el objeto proporcionado es {@code null}, la operación se ignora.
     * </p>
     * 
     * @param ticket El objeto {@link Ticket} que se desea agregar
     */
    public void agregar(Ticket ticket) {
        if (ticket == null) return;
        NodoTicketRepo nuevoNodo = new NodoTicketRepo(ticket);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoTicketRepo actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamano++;
    }

    /**
     * Obtiene un ticket por su posición en la lista.
     * <p>
     * La posición 0 corresponde al primer ticket.
     * </p>
     * 
     * @param posicion El índice del ticket a obtener
     * @return El ticket en la posición indicada, 
     * o {@code null} si la posición es inválida
     */
    public Ticket obtener(int posicion) {
        if (posicion < 0 || posicion >= tamano) return null;
        NodoTicketRepo actual = cabeza;
        int indice = 0;
        while (indice < posicion) {
            actual = actual.getSiguiente();
            indice++;
        }
        return actual.getValor();
    }

     /**
     * Reemplaza un ticket en una posición específica.
     * <p>
     * Si la posición es inválida o el ticket es {@code null},
     * la operación se ignora.
     * </p>
     * 
     * @param posicion El índice donde se reemplazará el ticket
     * @param ticket El nuevo ticket a colocar en esa posición
     */
    public void reemplazar(int posicion, Ticket ticket) {
        if (posicion < 0 || posicion >= tamano) return;
        NodoTicketRepo actual = cabeza;
        int indice = 0;
        while (indice < posicion) {
            actual = actual.getSiguiente();
            indice++;
        }
        actual.setValor(ticket);
    }

    /**
     * Extrae y retorna el primer ticket de la lista.
     * <p>
     * Elimina el ticket que está al frente de la lista
     * y lo devuelve. Si la lista está vacía, retorna {@code null}.
     * </p>
     * 
     * @return El primer ticket de la lista,
     * o {@code null} si la lista está vacía
     */
    public Ticket sacarPrimero() {
        if (cabeza == null) {
            return null;
    }

    Ticket ticketPrimero = cabeza.getValor();
    cabeza = cabeza.getSiguiente();
    tamano--;
    return ticketPrimero;
    }

    public int tamano(){ 
        return tamano; 
    }
    public boolean estaVacia(){
        return cabeza == null;
    }

    public NodoTicketRepo getCabeza(){ 
        return cabeza; 
    }
}
