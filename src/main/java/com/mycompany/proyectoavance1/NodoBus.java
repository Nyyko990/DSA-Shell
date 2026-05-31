/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectoavance1;

/**
 *
 * @author lcast
 */
public class NodoBus {
    private Bus bus;
    private NodoBus siguiente;

    public NodoBus(Bus bus) {
        this.bus = bus;
        this.siguiente = null;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public NodoBus getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoBus siguiente) {
        this.siguiente = siguiente;
    }
}
