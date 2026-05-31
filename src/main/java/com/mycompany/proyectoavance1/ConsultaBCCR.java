package com.mycompany.proyectoavance1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/* CONSULTA BCCR */
public class ConsultaBCCR {

    /** Indicador del tipo de cambio de compra */
    private static final int INDICADOR_COMPRA = 317;
    
    /** Indicador del tipo de cambio de venta */
    private static final int INDICADOR_VENTA  = 318;

    /** URL del Web Service del BCCR */
    private static final String URL_WS =
            "https://gee.bccr.fi.cr/Indicadores/Suscripciones/WS/wsindicadoreseconomicos.asmx/ObtenerIndicadoresEconomicos";

    /** Valor predeterminado de compra */
    private static final double COMPRA_DEFAULT = 452.59;

    /** Valor predeterminado de venta */
    private static final double VENTA_DEFAULT  = 458.64;

    /** Fecha predeterminada */
    private static final String FECHA_DEFAULT  = "19/04/2026";

     /**
     * Se consulta en línea el tipo de cambio del dólar.
     * <p>
     * Realiza dos consultas al Web Service del BCCR (compra y venta)
     * Si la consulta falla, se retorna un resultado con valores predeterminados.
     * </p>
     * 
     * @param correo Correo registrado en el servicio del BCCR
     * @param token Token de acceso al Web Service
     * @return Texto con el tipo de cambio obtenido o valores predeterminados
     */
    public String consultarEnLinea(String correo, String token) {
        try {
            String fecha = obtenerFechaHoy();

            double compra = consultarIndicador(INDICADOR_COMPRA, fecha, correo, token);
            double venta  = consultarIndicador(INDICADOR_VENTA,  fecha, correo, token);

            if (compra <= 0 || venta <= 0) {
                return formatearFallback("Consulta en linea fallida. Mostrando valores predeterminados.");
            }

            return "=== Tipo de Cambio del Dolar (BCCR) ===\n"
                    + "Fecha:   " + fecha + "\n"
                    + "Compra:  " + compra + " colones\n"
                    + "Venta:   " + venta  + " colones\n"
                    + "\n(Fuente: Web Service BCCR en linea)";

        } catch (Exception e) {
            return formatearFallback("No se pudo conectar al servicio del BCCR. Mostrando valores predeterminados.");
        }
    }

    /**
     * Se obtiene el tipo de cambio utilizando valores predeterminados.
     * <p>
     * Este método se usa como respaldo cuando no se puede
     * consultar el servicio en línea.
     * </p>
     * 
     * @return Texto con valores predeterminados del tipo de cambio
     */
    public String consultarPredeterminado() {
        return formatearFallback(null);
    }

     /**
     * Consulta un indicador específico del BCCR.
     * <p>
     * Realiza una petición HTTP al Web Service con los parámetros indicados
     * y procesa la respuesta en formato XML.
     * </p>
     * 
     * @param indicador Código del indicador
     * @param fecha Fecha de consulta
     * @param correo Correo registrado
     * @param token Token de acceso
     * @return Valor del indicador o -1 si ocurre un error
     */
    private double consultarIndicador(int indicador, String fecha, String correo, String token) {
        try {
            String urlStr = URL_WS
                    + "?Indicador=" + indicador
                    + "&FechaInicio=" + fecha
                    + "&FechaFinal=" + fecha
                    + "&Nombre=BusNovaTech"
                    + "&SubNiveles=N"
                    + "&CorreoElectronico=" + correo
                    + "&Token=" + token;

            URL url = new URL(urlStr);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(5000);
            conexion.setReadTimeout(5000);

            int codigoRespuesta = conexion.getResponseCode();
            if (codigoRespuesta != 200) {
                return -1;
            }

            BufferedReader lector = new BufferedReader(
                    new InputStreamReader(conexion.getInputStream())
            );

            String linea;
            String respuesta = "";
            while ((linea = lector.readLine()) != null) {
                respuesta += linea;
            }
            lector.close();
            conexion.disconnect();

            return extraerValorXML(respuesta);

        } catch (Exception e) {
            return -1;
        }
    }

     /**
     * Extrae el valor numérico desde la respuesta XML del BCCR.
     * <p>
     * Busca la etiqueta {@code <NUM_VALOR>} dentro del XML y convierte
     * su contenido a tipo {@code double}.
     * </p>
     * 
     * @param xml Respuesta en formato XML
     * @return Valor numérico extraído o -1 si ocurre un error
     */
    private double extraerValorXML(String xml) {
        try {
            String etiquetaAbrir = "<NUM_VALOR>";
            String etiquetaCerrar = "</NUM_VALOR>";

            int inicio = xml.indexOf(etiquetaAbrir);
            if (inicio < 0) return -1;

            int fin = xml.indexOf(etiquetaCerrar, inicio);
            if (fin < 0) return -1;

            String valorStr = xml.substring(inicio + etiquetaAbrir.length(), fin).trim();
            // El BCCR usa coma como separador decimal
            valorStr = valorStr.replace(",", ".");
            return Double.parseDouble(valorStr);

        } catch (Exception e) {
            return -1;
        }
    }

    private String obtenerFechaHoy() {
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return hoy.format(formato);
    }


    /**
     * Genera un resultado con valores predeterminados.
     * <p>
     * Se utiliza cuando falla la consulta en línea.
     * </p>
     * 
     * @param mensajeExtra Mensaje adicional 
     * @return Texto con el tipo de cambio predeterminado
     */
    private String formatearFallback(String mensajeExtra) {
        String resultado = "";
        if (mensajeExtra != null) {
            resultado += mensajeExtra + "\n\n";
        }
        resultado += "=== Tipo de Cambio del Dolar (BCCR) ===\n"
                + "Fecha:   " + FECHA_DEFAULT + "\n"
                + "Compra:  " + COMPRA_DEFAULT + " colones\n"
                + "Venta:   " + VENTA_DEFAULT  + " colones\n"
                + "\n(Fuente: valores predeterminados)";
        return resultado;
    }
}
