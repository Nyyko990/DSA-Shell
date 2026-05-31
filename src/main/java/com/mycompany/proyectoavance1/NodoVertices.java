package com.mycompany.proyectoavance1;
public class NodoVertices {

    private String nombre;
    private NodoArista primeraArista;
    private NodoVertices siguiente;
    public NodoVertices(String nombre) {
        this.nombre = nombre;
        this.primeraArista = null;
        this.siguiente = null;
    }

    public String getNombre() {
        return nombre;
    }

    public NodoArista getPrimeraArista() {
        return primeraArista;
    }

    public void setPrimeraArista(NodoArista primeraArista) {
        this.primeraArista = primeraArista;
    }

    public NodoVertices getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoVertices siguiente) {
        this.siguiente = siguiente;
    }
}