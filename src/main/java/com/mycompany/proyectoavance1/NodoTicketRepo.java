//Author = Nyko
package com.mycompany.proyectoavance1;
public class NodoTicketRepo {
    private Ticket valor;
    private NodoTicketRepo siguiente;
    
    public NodoTicketRepo(Ticket ticket){
        valor = ticket;
        siguiente = null;
    } 
    public Ticket getValor(){
        return valor; 
    }
    public void setValor(Ticket ticket){ 
        valor = ticket; 
    }
    public NodoTicketRepo getSiguiente(){
        return siguiente; 
    }
    public void setSiguiente(NodoTicketRepo nodoSiguiente){ 
        siguiente = nodoSiguiente; 
    }
}
