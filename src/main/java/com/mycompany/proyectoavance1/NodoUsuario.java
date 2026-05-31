//Author = Nyko
package com.mycompany.proyectoavance1;

/**
 * Representa un usuario dentro del sistema.
 * <p>
 * Guarda el nombre de usuario, la contraseña y una referencia
 * al siguiente nodo.
 * </p>
 * 
 * @author Nyko
 */
public class NodoUsuario {
    private String nombreUsuario;
    private String contrasena;
    private NodoUsuario siguiente;

    public NodoUsuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        siguiente = null;
    }

    public String getNombreUsuario(){ 
        return nombreUsuario;
    }
    public String getContrasena(){
        return contrasena; 
    }
    public NodoUsuario getSiguiente(){
        return siguiente; 
    }
    public void setSiguiente(NodoUsuario nodoSiguiente){ 
        siguiente = nodoSiguiente; 
    }
}
