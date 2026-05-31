/**
 * Representa la configuracion general del sistema.
 * <p>
 * Almacena datos principales de la terminal,
 * como su nombre, la cantidad total de buses y la lista
 * de usuarios con sus respectivas contrasenas.
 * </p>
 */
package com.mycompany.proyectoavance1;
public class Configuracion {

    private String nombreTerminal;
    private int cantidadBuses;
    private ListaUsuarios listaUsuarios;

    public Configuracion() {
        nombreTerminal = "";
        cantidadBuses = 0;
        listaUsuarios = new ListaUsuarios();
    }

     /**
     * Verifica si la configuración actual es válida.
     * @return {@code true} si la configuración es válida, {@code false} en caso contrario.
     */
    public boolean tieneConfigValida() {
        if (nombreTerminal == null) {
            return false;
        }
        if (nombreTerminal.trim().equals("")) {
            return false;
        }
        if (cantidadBuses < 3) {
            return false;
        }
        if (listaUsuarios.tamano() <= 0) {
            return false;
        }
        return true;
    }

    public String getNombreTerminal(){ 
        return nombreTerminal; 
    }
    public int getCantidadBuses(){ 
        return cantidadBuses; 
    }

    public void setNombreTerminal(String nombre){ 
        nombreTerminal = nombre; 
    }
    public void setCantidadBuses(int cantidad){ 
        cantidadBuses = cantidad; 
    }

    /**
     * Verifica si existe un usuario con el nombre indicado.
     * @param nombreUsuario El nombre del usuario a buscar
     * @return {@code true} si el usuario existe, si no {@code false} 
     */
    public boolean existeUsuario(String nombreUsuario) {
        return listaUsuarios.existeUsuario(nombreUsuario);
    }

    /**
     * Agrega un nuevo usuario a la lista de autorizados.
     * <p>
     * El método verifica que el nombre y la contraseña no sean nulos ni vacíos.
     * Si el usuario ya existe, la operación se ignora.
     * </p>
     * @param nombreUsuario El nombre del nuevo usuario
     * @param contrasena La contraseña del nuevo usuario
     */
    public void agregarUsuario(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || contrasena == null) return;
        nombreUsuario = nombreUsuario.trim();
        contrasena = contrasena.trim();
        if (nombreUsuario.equals("") || contrasena.equals("")) return;
        if (existeUsuario(nombreUsuario)) return;

        listaUsuarios.agregar(nombreUsuario, contrasena);
    }

     /**
     * Valida las credenciales de un usuario.
     * <p>
     * Verifica si existe un usuario con el nombre proporcionado
     * y si la contraseña coincide con la registrada.
     * </p>
     * @param nombreUsuario El nombre del usuario
     * @param contrasena La contraseña a validar
     * @return {@code true} si las credenciales son correctas, {@code false} en caso contrario
     */
    public boolean validarLogin(String nombreUsuario, String contrasena) {
        return listaUsuarios.validarLogin(nombreUsuario, contrasena);
    }

    /**
     * Convierte la configuración a formato JSON.
     * <p>
     * Genera una cadena JSON con la estructura:
     * <ul>
     *   <li>nombreTerminal: El nombre de la terminal</li>
     *   <li>cantidadBuses: El número de buses</li>
     *   <li>usuarios: Lista de usuarios registrados</li>
     * </ul>
     * </p>
     * @return Una cadena con la configuración en formato JSON
     */
    public String toJSON() {
        String jsonTexto = "{\n";
        jsonTexto += "  \"nombreTerminal\":\"" + JsonUtilSimple.escape(nombreTerminal) + "\",\n";
        jsonTexto += "  \"cantidadBuses\":" + cantidadBuses + ",\n";
        jsonTexto += "  \"usuarios\":[\n";

        NodoUsuario actual = listaUsuarios.getCabeza();
        int contador = 0;
        while (actual != null) {
            jsonTexto += "    {\"nombreUsuario\":\"" + JsonUtilSimple.escape(actual.getNombreUsuario())
                + "\",\"contrasena\":\"" + JsonUtilSimple.escape(actual.getContrasena()) + "\"}";
            if (actual.getSiguiente() != null) {
                jsonTexto += ",";
            }
            jsonTexto += "\n";
            actual = actual.getSiguiente();
            contador++;
        }

        jsonTexto += "  ]\n";
        jsonTexto += "}\n";
        return jsonTexto;
    }

    /**
     * Crea un objeto Configuracion a partir de una cadena JSON.
     * <p>
     * Extrae los datos del JSON y construye un objeto de configuración.
     * Si el JSON es inválido o no cumple con los criterios de validación,
     * retorna {@code null}.
     * </p>
     * @param json La cadena en formato JSON que representa la configuración
     * @return Un objeto {@link Configuracion} con los datos extraídos, 
     * o {@code null} si no se pudo construir una configuración válida
     */
    public static Configuracion fromJSON(String json) {
        try {
            if (json == null) return null;

            Configuracion configuracion = new Configuracion();
            String nombre = JsonUtilSimple.extraerString(json, "nombreTerminal");
            int buses = JsonUtilSimple.extraerInt(json, "cantidadBuses", -1);

            if (nombre == null) return null;
            if (buses < 3) return null;

            configuracion.setNombreTerminal(nombre);
            configuracion.setCantidadBuses(buses);

            int posicionBusqueda = 0;
            while (true) {
                int inicioObjeto = json.indexOf("{\"nombreUsuario\"", posicionBusqueda);
                if (inicioObjeto < 0) break;

                int finObjeto = json.indexOf("}", inicioObjeto);
                if (finObjeto < 0) break;

                String bloque = json.substring(inicioObjeto, finObjeto + 1);

                String nombreUsuario = JsonUtilSimple.extraerString(bloque, "nombreUsuario");
                String contrasena = JsonUtilSimple.extraerString(bloque, "contrasena");

                if (nombreUsuario != null && contrasena != null) {
                    configuracion.agregarUsuario(nombreUsuario, contrasena);
                }

                posicionBusqueda = finObjeto + 1;
            }

            if (!configuracion.tieneConfigValida()) return null;
            return configuracion;
        } catch (Exception excepcion) {
            return null;
        }
    }
}
