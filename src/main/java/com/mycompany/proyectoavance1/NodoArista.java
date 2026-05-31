//Author = Nyko
package com.mycompany.proyectoavance1;

/**
 * Representa una ruta entre localidades en el grafo.
 * <p>
 * Guarda el destino, el costo o distancia y una referencia
 * al siguiente nodo.
 * </p>
 * 
 * @author Nyko
 */
public class NodoArista {
    private String destino;
    private double peso;
    private NodoArista siguiente;
    
    public NodoArista(String destino, double peso) {
        this.destino = destino;
        this.peso = peso;
        this.siguiente = null;
    }
 
    public String getDestino() {
        return destino;
    }
 
    public double getPeso() {
        return peso;
    }
 
    public NodoArista getSiguiente() {
        return siguiente;
    }
 
    public void setSiguiente(NodoArista siguiente) {
        this.siguiente = siguiente;
    }
}

