package com.mycompany.proyectoavance1;
public class GrafoRepository {

    private String ruta;
    private GrafoBuses cacheGrafo;

    public GrafoRepository(String rutaArchivo) {
        this.ruta = rutaArchivo;
        this.cacheGrafo = cargar();
    }

    public GrafoBuses cargar() {
        String contenido = JsonUtilSimple.leerArchivo(ruta);
        GrafoBuses grafo = new GrafoBuses();

        if (contenido != null && !contenido.trim().isEmpty()) {
            grafo.desdeJson(contenido);
        }

        cacheGrafo = grafo;
        return grafo;
    }

     /**
     * Guarda el grafo en el archivo.
     * 
     * @param grafo Grafo a guardar
     * @return {@code true} si se guardó correctamente
     */
    public boolean guardar(GrafoBuses grafo) {
        if (grafo != null) {
            cacheGrafo = grafo;
        }

        if (cacheGrafo == null) {
            return false;
        }

        String json = cacheGrafo.aJson();
        return JsonUtilSimple.escribirArchivo(ruta, json);
    }

    /**
     * Obtiene el grafo en memoria.
     * 
     * @return Grafo actual
     */
    public GrafoBuses obtenerGrafo() {
        return cacheGrafo;
    }
}
