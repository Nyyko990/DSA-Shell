/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectoavance1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
/**
 *
 * @author lcast
 */
public class AtendidosRepository {
    private String ruta;
    private ListaTickets cacheAtendidos;

    /**
     * Constructor del repositorio.
     * Recibe la ubicación del archivo donde se guardan los tickets 
     * atendidos y carga automáticamente los tickets existentes en memoria.
     * @param rutaArchivo La ruta del archivo de almacenamiento
     */
    

    public AtendidosRepository(String rutaArchivo) {
        ruta = rutaArchivo;
        cacheAtendidos = cargarAtendidos();
    }

     /**
     * Carga los tickets atendidos desde el archivo.
     * Si el archivo no existe o ocurre un error durante la lectura, 
     * se retorna una lista vacía para evitar problemas en el sistema.
     * @return La lista de tickets atendidos previamente guardados, o una lista vacía si no hay datos o ocurre un error
     */
    public ListaTickets cargarAtendidos() {
        try {
            Gson gson = new Gson();
            FileReader lectorArchivo = new FileReader(ruta);
            ListaTickets listaLeida = gson.fromJson(lectorArchivo, ListaTickets.class);
            lectorArchivo.close();

            if (listaLeida != null) {
                return listaLeida;
            }
        } catch (Exception exception) {
        }

        return new ListaTickets();
    }

     /**
     * Agrega un ticket a la lista de atendidos.
     * El método verifica que el ticket no sea nulo antes de agregarlo.
     * Si el objeto proporcionado es {@code null}, la operación se ignora.
     * Después de agregarlo, guarda automáticamente la lista actualizada en el archivo.
     * @param ticketAtendido El objeto {@link Ticket} que se marcó como atendido
     */
    public void agregarAtendido(Ticket ticketAtendido) {
        if (ticketAtendido == null) {
            return;
        }

        cacheAtendidos.agregar(ticketAtendido);
        guardarListaCompleta();
    }

    public ListaTickets obtenerAtendidos() {
        return cacheAtendidos;
    }

     /**
     * Guarda toda la lista de tickets atendidos en el archivo.
     * Utiliza formato JSON con indentación para facilitar la lectura del archivo.
     * @return {@code true} si la operación fue exitosa, {@code false} si ocurrió algún error al guardar
     */
    public boolean guardarListaCompleta() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter escritorArchivo = new FileWriter(ruta);
            gson.toJson(cacheAtendidos, escritorArchivo);
            escritorArchivo.flush();
            escritorArchivo.close();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
