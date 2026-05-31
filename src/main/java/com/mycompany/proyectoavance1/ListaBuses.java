/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectoavance1;

/**
 * Lista enlazada que gestiona los buses del sistema.
 * <p>
 * Permite agregar buses, buscarlos por número, obtener el último bus,
 * y mostrar un resumen del estado de todos los buses registrados.
 * </p>
 * @author lcast
 */
public class ListaBuses {
    private NodoBus cabeza;
    private int tamano;

     /**
     * Obtiene el número más alto entre todos los buses registrados.
     * <p>
     * Recorre la lista completa y encuentra el bus con el número mayor.
     * Si la lista está vacía, retorna 0.
     * </p>
     * @return El número de bus más grande, o 0 si no hay buses
     */
    public int obtenerNumeroMayorBus() {
        if (cabeza == null) {
            return 0;
    }

    int numeroMayor = 0;
    NodoBus actual = cabeza;

    while (actual != null) {
        if (actual.getBus().getNumeroBus() > numeroMayor) {
            numeroMayor = actual.getBus().getNumeroBus();
        }
        actual = actual.getSiguiente();
    }

    return numeroMayor;
    }

    /**
     * Obtiene el último bus de la lista.
     * <p>
     * Recorre la lista hasta el final y retorna el último bus encontrado.
     * </p>
     * @return El último bus de la lista, o {@code null} si la lista está vacía
     */
    public Bus obtenerUltimoBus() {
        if (cabeza == null) {
            return null;
    }

    NodoBus actual = cabeza;

    while (actual.getSiguiente() != null) {
        actual = actual.getSiguiente();
    }

    return actual.getBus();
    }

    /**
     * Elimina el último bus de la lista.
     * <p>
     * Busca el penúltimo nodo y elimina la referencia al último.
     * Si la lista tiene un solo elemento, lo elimina dejando la lista vacía.
     * </p>
     * 
     * @return {@code true} si se eliminó correctamente, {@code false} si la lista estaba vacía
     */
    public boolean eliminarUltimoBus() {
        if (cabeza == null) {
            return false;
    }

    if (cabeza.getSiguiente() == null) {
        cabeza = null;
        tamano--;
        return true;
    }

    NodoBus actual = cabeza;

    while (actual.getSiguiente().getSiguiente() != null) {
        actual = actual.getSiguiente();
    }

    actual.setSiguiente(null);
    tamano--;
    return true;
    }

    public ListaBuses() {
        cabeza = null;
        tamano = 0;
    }

    /**
     * Agrega un bus al final de la lista.
     * <p>
     * El método verifica que el bus no sea nulo antes de agregarlo.
     * Si el objeto proporcionado es {@code null}, la operación se ignora.
     * </p>
     * @param bus El objeto {@link Bus} que se desea agregar
     */
    public void agregarBus(Bus bus) {
        if (bus == null) {
            return;
        }

        NodoBus nuevoNodo = new NodoBus(bus);

        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoBus actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }

        tamano++;
    }

    /**
     * Busca un bus por su número identificador.
     * <p>
     * Recorre la lista hasta encontrar un bus con el número especificado.
     * </p>
     * 
     * @param numeroBus El número del bus que se desea buscar
     * @return El bus encontrado, o {@code null} si no existe
     */
    public Bus buscarBusPorNumero(int numeroBus) {
        NodoBus actual = cabeza;

        while (actual != null) {
            if (actual.getBus().getNumeroBus() == numeroBus) {
                return actual.getBus();
            }
            actual = actual.getSiguiente();
        }

        return null;
    }

    /**
     * Obtiene un bus por su posición en la lista.
     * <p>
     * La posición 0 corresponde al primer bus.
     * </p>
     * 
     * @param posicion El índice del bus a obtener
     * @return El bus en la posición indicada, o {@code null} si la posición es inválida
     */
    public Bus obtenerBusEnPosicion(int posicion) {
        if (posicion < 0 || posicion >= tamano) {
            return null;
        }

        NodoBus actual = cabeza;
        int indice = 0;

        while (indice < posicion) {
            actual = actual.getSiguiente();
            indice++;
        }

        return actual.getBus();
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    /**
     * Genera un resumen con el estado de todos los buses.
     * <p>
     * Recorre la lista y muestra el resumen de cada bus utilizando el método {@link Bus#resumenBus()}.
     * Si no hay buses, retorna un mensaje indicándolo.
     * </p>
     * @return Una cadena con el resumen de todos los buses,
     * o un mensaje si la lista está vacía.
     */
    public String mostrarResumen() {
        if (cabeza == null) {
            return "No hay buses registrados.";
        }

        String texto = "";
        NodoBus actual = cabeza;

        while (actual != null) {
            texto += actual.getBus().resumenBus() + "\n";
            actual = actual.getSiguiente();
        }

        return texto;
    }
}
