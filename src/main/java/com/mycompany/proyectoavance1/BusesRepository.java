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
 * Repositorio encargado de gestionar la persistencia de los buses en un archivo JSON.
 * <p>
 * Permite cargar los datos desde un archivo, mantener una copia en la memoria 
 * y guardar los cambios realizados. Utiliza la librería Gson para la conversión
 * entre objetos Java y formato JSON.
 * </p>
 * 
 * @author lcast
 */
public class BusesRepository {
    private String ruta;
    private ListaBuses cacheBuses;

    /**
     * Constructor del repositorio.
     * <p>
     * Inicializa la ruta del archivo y carga automáticamente los buses
     * almacenados en el sistema.
     * </p>
     * 
     * @param rutaArchivo Ruta del archivo JSON
     */
    public BusesRepository(String rutaArchivo) {
        ruta = rutaArchivo;
        cacheBuses = cargarBuses();
    }

    /**
     * Carga los buses desde el archivo JSON.
     * <p>
     * Intenta leer el archivo especificado y convertir su contenido en un objeto
     * {@code ListaBuses}. Si ocurre algún error, se retorna una lista vacía.
     * </p>
     * 
     * @return Lista de buses cargada desde el archivo o una lista vacía si falla la lectura
     */
    public ListaBuses cargarBuses() {
        try {
            Gson gson = new Gson();
            FileReader lectorArchivo = new FileReader(ruta);
            ListaBuses listaLeida = gson.fromJson(lectorArchivo, ListaBuses.class);
            lectorArchivo.close();

            if (listaLeida != null) {
                return listaLeida;
            }
        } catch (Exception e) {
        }

        return new ListaBuses();
    }

    /**
     * Obtiene la lista de buses almacenada en la caché.
     * <p>
     * No realiza lectura del archivo, simplemente retorna la información
     * actualmente cargada en la memoria.
     * </p>
     * 
     * @return Lista de buses en memoria
     */
    public ListaBuses obtenerBuses() {
        return cacheBuses;
    }

    /**
     * Actualiza la caché de buses en memoria.
     * <p>
     * Solo se realiza el cambio si la lista dada no es nula.
     * </p>
     * 
     * @param listaBuses Nueva lista de buses
     */
    public void setCacheBuses(ListaBuses listaBuses) {
        if (listaBuses != null) {
            cacheBuses = listaBuses;
        }
    }

    /**
     * Guarda la lista de buses en el archivo JSON.
     * <p>
     * Convierte la lista en formato JSON y la escribe en el archivo.
     * También actualiza la caché en memoria con los nuevos datos.
     * </p>
     * 
     * @param listaBuses Lista de buses a guardar
     * @return {@code true} si se guardó correctamente, {@code false} si ocurrió un error
     */
    public boolean guardarBuses(ListaBuses listaBuses) {
        if (listaBuses != null) {
            cacheBuses = listaBuses;
        }

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter escritorArchivo = new FileWriter(ruta);
            gson.toJson(cacheBuses, escritorArchivo);
            escritorArchivo.flush();
            escritorArchivo.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
