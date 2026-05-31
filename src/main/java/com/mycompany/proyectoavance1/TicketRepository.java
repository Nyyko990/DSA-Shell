/**
 * Repositorio encargado de administrar los tickets almacenados
 * en un archivo JSON.
 *
 * Esta clase permite cargar, guardar, agregar y actualizar
 * tickets utilizando Gson para convertir objetos Java a JSON
 * y viceversa.
 */
package com.mycompany.proyectoavance1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;

public class TicketRepository {

    private String ruta;
    private ListaTickets cache;

    public TicketRepository(String rutaArchivo) {
        ruta = rutaArchivo;
        cache = cargarTickets();
    }
 /**
 * Carga los tickets desde el archivo JSON indicado en la ruta.
 *
 * @return una lista enlazada con los tickets cargados, o una lista vacía
 * si el archivo no existe o no se puede leer.
 */
    public ListaTickets cargarTickets() {
        try {
            Gson gson = new Gson();
            FileReader lectorArchivo = new FileReader(ruta);
            ListaTickets listaLeida = gson.fromJson(lectorArchivo, ListaTickets.class);
            lectorArchivo.close();

            if (listaLeida != null) {
                return listaLeida;
            }
        } catch (Exception excepcion) {
        }

        return new ListaTickets();
    }

    /**
     * Agrega un nuevo ticket al repositorio.
     * <p>
     * El método verifica que el ticket no sea nulo antes de agregarlo.
     * Si el objeto proporcionado es {@code null}, la operación se ignora.
     * Después de agregarlo, guarda automáticamente la lista actualizada en el archivo.
     * </p>
     * 
     * @param ticket El objeto {@link Ticket} que se desea agregar
     */
    public void agregarTicket(Ticket ticket) {
        if (ticket == null) {
            return;
        }

        cache.agregar(ticket);
        guardarListaCompleta();
    }

    /**
     * Actualiza un ticket existente en el repositorio.
     * <p>
     * Busca el ticket por su ID y fecha de compra. Si lo encuentra,
     * lo reemplaza en la cache. Si no lo encuentra, lo agrega como nuevo.
     * Después, guarda los cambios en el archivo.
     * </p>
     * 
     * @param actualizado El ticket con los datos actualizados
     */
    public void actualizarTicket(Ticket actualizado) {
        if (actualizado == null) {
            return;
        }

        int indice = 0;
        while (indice < cache.tamano()) {
            Ticket ticketActual = cache.obtener(indice);

            if (ticketActual != null) {
                if (ticketActual.getId() == actualizado.getId()
                        && mismoTexto(ticketActual.getFechaHoraCompra(), actualizado.getFechaHoraCompra())) {
                    cache.reemplazar(indice, actualizado);
                    guardarListaCompleta();
                    return;
                }
            }

            indice++;
        }

        cache.agregar(actualizado);
        guardarListaCompleta();
    }
/**
 * Guarda la lista completa de tickets en el archivo JSON.
 *
 * @return true si el guardado se realizó correctamente,
 * false en caso contrario.
 */
    public boolean guardarListaCompleta() {
        try {
            Gson gsonBonito = new GsonBuilder().setPrettyPrinting().create();
            FileWriter escritorArchivo = new FileWriter(ruta);
            gsonBonito.toJson(cache, escritorArchivo);
            escritorArchivo.flush();
            escritorArchivo.close();
            return true;
        } catch (Exception excepcion) {
            return false;
        }
    }

    public ListaTickets obtenerCache() {
        return cache;
    }

    private boolean mismoTexto(String textoA, String textoB) {
        if (textoA == null && textoB == null) {
            return true;
        }
        if (textoA == null) {
            return false;
        }
        return textoA.equals(textoB);
    }
}
