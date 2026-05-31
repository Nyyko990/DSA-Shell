/**
 * Repositorio encargado de administrar la carga y el guardado
 * de la configuración general del sistema en un archivo JSON.
 * <p>
 * Esta clase permite leer la información almacenada en
 * <code>config.json</code> y convertirla en un objeto
 * {@link Configuracion}, así como guardar nuevamente los cambios
 * realizados en la configuración.
 * </p>
 */
package com.mycompany.proyectoavance1;

public class ConfigRepository {
    private String ruta;
    
    /**
    * Constructor del repositorio de configuración.
    * Recibe la ubicación del archivo donde se almacena la configuración.
    * @param rutaArchivo La ruta del archivo de configuración
    */
    public ConfigRepository(String rutaArchivo) {
        ruta = rutaArchivo;
    }

    /**
     * Carga la configuración desde el archivo JSON.
     * <p>
     * Lee el contenido del archivo ubicado en la ruta configurada
     * y lo convierte en un objeto de configuración.
     * Si el archivo no existe o hay algún error, retorna {@code null}.
     * </p>
     * @return El objeto {@link Configuracion} cargado desde el archivo o {@code null} si no se pudo cargar
     */
    public Configuracion cargar() {
        String json = JsonUtilSimple.leerArchivo(ruta);
        if (json == null) return null;
            return Configuracion.fromJSON(json);
    }

    /**
     * Guarda la configuración en el archivo JSON.
     * <p>
     * Convierte el objeto de configuración recibido a formato JSON
     * y lo escribe en el archivo ubicado en la ruta configurada.
     * Si la configuración es {@code null}, la operación se ignora.
     * </p>
     * @param configuracion El objeto {@link Configuracion} que se desea guardar
     * @return {@code true} si la operación fue exitosa, {@code false} si ocurrió algún error o la configuración es nula
     */
    public boolean guardar(Configuracion configuracion) {
        if (configuracion == null) return false;
            return JsonUtilSimple.escribirArchivo(ruta, configuracion.toJSON());
    }
}
