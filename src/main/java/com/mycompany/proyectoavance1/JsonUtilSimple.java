/**
 * Clase utilitaria para trabajar con archivos y cadenas JSON
 * de manera sencilla, sin utilizar librerías externas.
 * <p>
 * Incluye métodos para leer y escribir archivos de texto,
 * escapar caracteres especiales y extraer valores básicos
 * desde una cadena en formato JSON.
 * </p>
 */
package com.mycompany.proyectoavance1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class JsonUtilSimple {

    /**
     * Lee el contenido completo de un archivo.
     * <p>
     * Abre el archivo ubicado en la ruta indicada y retorna todo su contenido
     * como una cadena de texto. Si ocurre algún error, retorna {@code null}.
     * </p>
     * @param ruta La ubicación del archivo que se desea leer
     * @return El contenido del archivo como cadena de texto o {@code null} si no se pudo leer
     */
    public static String leerArchivo(String ruta) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ruta));
            String lineaActual;
            String contenido = "";
            while ((lineaActual = br.readLine()) != null) {
                contenido = contenido + lineaActual + "\n";
            }
            br.close();
            return contenido;
        } catch (Exception e) {
            try { if (br != null) br.close(); } catch (Exception ex) { }
            return null;
        }
    }

    /**
     * Escribe contenido en un archivo.
     * <p>
     * Crea el archivo en la ruta indicada.
     * Si ocurre algún error durante la escritura, retorna {@code false}.
     * </p>
     * @param ruta La ubicación donde se guardará el archivo
     * @param contenido El texto que se escribirá en el archivo
     * @return {@code true} si la escritura fue exitosa, {@code false} si ocurrió algún error
     */
    public static boolean escribirArchivo(String ruta, String contenido) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(ruta);
            fw.write(contenido);
            fw.close();
            return true;
        } catch (Exception e) {
            try { if (fw != null) fw.close(); } catch (Exception ex) { }
            return false;
        }
    }

    /**
     * Escapa caracteres especiales en un texto para usarlo dentro de JSON.
     * <p>
     * Reemplaza las barras invertidas y las comillas dobles con sus
     * versiones escapadas para que el texto pueda incluirse en un JSON.
     * Si el texto es {@code null}, retorna una cadena vacía.
     * </p>
     * @param texto El texto que se desea escapar
     * @return El texto con los caracteres especiales escapados
     */
    public static String escape(String texto) {
        if (texto == null) return "";
        texto = texto.replace("\\", "\\\\");
        texto = texto.replace("\"", "\\\"");
        return texto;
    }

    /**
     * Desescapa caracteres especiales de un texto proveniente de JSON.
     * <p>
     * Restaura las comillas dobles y barras invertidas que fueron escapadas
     * en un JSON a su forma original.
     * Si el texto es {@code null}, retorna una cadena vacía.
     * </p>
     * @param texto El texto que se desea desescapar
     * @return El texto con los caracteres restaurados
     */
    public static String unescape(String texto) {
        if (texto == null) return "";
        texto = texto.replace("\\\"", "\"");
        texto = texto.replace("\\\\", "\\");
        return texto;
    }

    /**
     * Extrae el valor de una clave de tipo texto desde una cadena JSON.
     * Busca la clave especificada en el JSON y retorna su valor como texto.
     * @param json La cadena en formato JSON donde se buscará la clave
     * @param key El nombre de la clave cuyo valor se desea obtener
     * @return El valor asociado a la clave, o {@code null} si no se encuentra
     */
    public static String extraerString(String json, String key) {
        try {
            String patron = "\"" + key + "\"";
            int posicionClave = json.indexOf(patron);
            if (posicionClave < 0) return null;

            int dosP = json.indexOf(":", posicionClave);
            if (dosP < 0) return null;

            int q1 = json.indexOf("\"", dosP);
            if (q1 < 0) return null;

            int q2 = json.indexOf("\"", q1 + 1);
            while (q2 >= 0 && json.charAt(q2 - 1) == '\\') {
                q2 = json.indexOf("\"", q2 + 1);
            }
            if (q2 < 0) return null;

            String raw = json.substring(q1 + 1, q2);
            return unescape(raw);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extrae el valor de una clave de tipo número desde una cadena JSON.
     * <p>
     * Busca la clave especificada en el JSON y retorna su valor como entero.
     * Si la clave no existe o el valor no es un número válido,
     * retorna el valor de respaldo.
     * </p>
     * 
     * @param json La cadena en formato JSON donde se buscará la clave
     * @param key El nombre de la clave cuyo valor se desea obtener
     * @param fallback El valor que se retorna si no se puede extraer el número
     * @return El valor numérico asociado a la clave, o el valor de respaldo en caso de error
     */
    public static int extraerInt(String json, String key, int fallback) {
        try {
            String patron = "\"" + key + "\"";
            int posicionClave = json.indexOf(patron);
            if (posicionClave < 0) return fallback;

            int dosP = json.indexOf(":", posicionClave);
            if (dosP < 0) return fallback;

            int inicioNumero = dosP + 1;
            while (inicioNumero < json.length() && (json.charAt(inicioNumero) == ' ' || json.charAt(inicioNumero) == '\n' || json.charAt(inicioNumero) == '\r' || json.charAt(inicioNumero) == '\t')) inicioNumero++;

            int finNumero = inicioNumero;
            while (finNumero < json.length()) {
                char c = json.charAt(finNumero);
                if ((c >= '0' && c <= '9') || c == '-') finNumero++;
                else break;
            }
            String num = json.substring(inicioNumero, finNumero).trim();
            return Integer.parseInt(num);
        } catch (Exception e) {
            return fallback;
        }
    }
}
