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
 * Repositorio encargado de guardar y cargar el estado completo
 * de las colas de los buses en el archivo colas.json
 * 
 * @author fabiana
 */
public class ColaRepository {
    
    private String rutaArchivo;
    
    public ColaRepository(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
    
    /**
     * Carga desde el archivo JSON la lista completa de buses con sus colas
     *
     * @return lista de buses cargada desde el archivo, o null si no existe
     * o si ocurrió un error de lectura
     */
    public ListaBuses cargarColas() {
        try {
            Gson gson = new Gson();
            FileReader lectorArchivo = new FileReader(rutaArchivo);
            ListaBuses listaBusesCargada = gson.fromJson(lectorArchivo, ListaBuses.class);
            lectorArchivo.close();

            if (listaBusesCargada != null) {
                return listaBusesCargada;
            }
        } catch (Exception excepcion) {
        }

        return null;
    }

    /**
     * Guarda en el archivo JSON el estado actual de la lista de buses.
     *
     * @param listaBusesActual lista de buses que se desea guardar
     * @return {@code true} si se guardó correctamente, {@code false} si ocurrió un error
     */
    public boolean guardarColas(ListaBuses listaBusesActual) {
        try {
            Gson gsonBonito = new GsonBuilder().setPrettyPrinting().create();
            FileWriter escritorArchivo = new FileWriter(rutaArchivo);
            gsonBonito.toJson(listaBusesActual, escritorArchivo);
            escritorArchivo.flush();
            escritorArchivo.close();
            return true;
        } catch (Exception excepcion) {
            return false;
        }
    }
    
}
