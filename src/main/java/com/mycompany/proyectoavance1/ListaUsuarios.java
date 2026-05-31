package com.mycompany.proyectoavance1;
/**
 * Lista enlazada para gestionar los usuarios autorizados del sistema.
 * <p>
 * Permite agregar usuarios, verificar si existen y validar sus credenciales
 * para el inicio de sesión.
 * </p>
 * 
 * @author nyko
 */
public class ListaUsuarios {
    private NodoUsuario cabeza;
    private int tamano;

    public ListaUsuarios() {
        cabeza = null;
        tamano = 0;
    }

    /**
     * Agrega un nuevo usuario a la lista.
     * <p>
     * Crea un nuevo nodo con el nombre de usuario y contraseña proporcionados
     * y lo agrega al final de la lista.
     * </p>
     * 
     * @param nombreUsuario El nombre del usuario
     * @param contrasena La contraseña del usuario
     */
    public void agregar(String nombreUsuario, String contrasena) {
        NodoUsuario nuevoNodo = new NodoUsuario(nombreUsuario, contrasena);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoUsuario actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamano++;
    }

    /**
     * Verifica si existe un usuario con el nombre indicado.
     * <p>
     * Recorre la lista buscando un usuario que coincida con el nombre proporcionado.
     * </p>
     * 
     * @param nombreUsuario El nombre del usuario a buscar
     * @return {@code true} si el usuario existe, {@code false} en caso contrario
     */
    public boolean existeUsuario(String nombreUsuario) {
        NodoUsuario actual = cabeza;
        while (actual != null) {
            if (actual.getNombreUsuario().equals(nombreUsuario)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Valida las credenciales de un usuario para el inicio de sesión.
     * <p>
     * Busca un usuario que coincida tanto en nombre como en contraseña.
     * </p>
     * 
     * @param nombreUsuario El nombre del usuario
     * @param contrasena La contraseña a validar
     * @return {@code true} si las credenciales son correctas, {@code false} en el caso contrario
     */
    public boolean validarLogin(String nombreUsuario, String contrasena) {
        NodoUsuario actual = cabeza;
        while (actual != null) {
            if (actual.getNombreUsuario().equals(nombreUsuario)
                && actual.getContrasena().equals(contrasena)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public int tamano(){ 
        return tamano; 
    }

    public NodoUsuario getCabeza() {
        return cabeza;
    }
}
