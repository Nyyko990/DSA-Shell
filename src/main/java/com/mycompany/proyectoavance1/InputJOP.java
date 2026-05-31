/**
 * Clase utilitaria para capturar datos mediante cuadros de diálogo.
 * <p>
 * Proporciona métodos para leer textos, números enteros y opciones
 * válidas, aplicando validaciones básicas para evitar entradas vacías
 * o incorrectas.
 * </p>
 */
package com.mycompany.proyectoavance1;

 /**
 * Solicita al usuario un texto que no esté vacío.
 * <p>
 * Muestra un cuadro de diálogo con el mensaje indicado y sigue preguntando
 * hasta que el usuario ingrese un texto que no sea nulo ni esté vacío.
 * Si el usuario cancela, se le solicita nuevamente.
 * </p>
 * @param mensaje El texto que se muestra al usuario para indicar qué ingresar
 * @return El texto ingresado por el usuario, sin espacios al inicio ni al final
 */
public class InputJOP {
    public String leerTextoNoVacio(String mensaje) {
        while (true) {
            String textoIngresado = javax.swing.JOptionPane.showInputDialog(mensaje);
            if (textoIngresado == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "intenta de nuevo.");
                continue;
            }
            textoIngresado = textoIngresado.trim();
            if (!textoIngresado.equals("")) return textoIngresado;
                javax.swing.JOptionPane.showMessageDialog(null, "Entrada invalida.");
        }
    }

    /**
     * Solicita al usuario un número entero dentro de un rango específico.
     * <p>
     * Muestra un cuadro de diálogo y sigue preguntandohasta que el usuario 
     * ingrese un número válido..
     * </p>
     * @param mensaje El texto que se muestra al usuario para indicar qué ingresar
     * @param min El valor mínimo permitido 
     * @param max El valor máximo permitido
     * @return El número entero ingresado por el usuario, dentro del rango
     */
    public int leerEnteroRango(String mensaje, int min, int max) {
        while (true) {
            String textoIngresado = javax.swing.JOptionPane.showInputDialog(mensaje);
            if (textoIngresado == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "Intenta de nuevo.");
                continue;
            }
            textoIngresado = textoIngresado.trim();

            int valorIngresado = parseEnteroSeguro(textoIngresado, Integer.MIN_VALUE);
            if (valorIngresado == Integer.MIN_VALUE) {
                javax.swing.JOptionPane.showMessageDialog(null, "numero invalido");
                continue;
            }

            if (valorIngresado < min || valorIngresado > max) {
                javax.swing.JOptionPane.showMessageDialog(null, "Fuera de rango (" + min + " a " + max + ").");
                continue;
            }
            return valorIngresado;
        }
    }

    /**
     * Solicita al usuario que elija entre dos opciones de texto.
     * <p>
     * Muestra un cuadro de diálogo y sigue preguntando hasta que el usuario
     * ingrese exactamente una de las dos opciones válidas.
     * </p>
     * @param mensaje El texto que se muestra al usuario para indicar qué elegir
     * @param op1 La primera opción válida
     * @param op2 La segunda opción válida
     * @return La opción seleccionada por el usuario
     */
    public String leerOpcionTexto(String mensaje, String op1, String op2) {
        while (true) {
            String textoIngresado = javax.swing.JOptionPane.showInputDialog(mensaje);
            if (textoIngresado == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "Intenta de nuevo.");
                continue;
            }
            textoIngresado = textoIngresado.trim();
            if (textoIngresado.equals(op1) || textoIngresado.equals(op2)) return textoIngresado;
                javax.swing.JOptionPane.showMessageDialog(null, "Intenta de nuevo.");
        }
    }

    /**
     * Solicita al usuario que elija entre tres opciones de texto.
     * <p>
     * Muestra un cuadro de diálogo y sigue preguntando hasta que se
     * ingrese una de las tres opciones válidas.
     * </p>
     * @param mensaje El texto que se muestra al usuario para indicar qué elegir
     * @param op1 La primera opción válida
     * @param op2 La segunda opción válida
     * @param op3 La tercera opción válida
     * @return La opción seleccionada por el usuario 
     */
    public String leerOpcionTexto(String mensaje, String op1, String op2, String op3) {
        while (true) {
            String textoIngresado = javax.swing.JOptionPane.showInputDialog(mensaje);
            if (textoIngresado == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "Intenta de nuevo.");
                continue;
            }
            textoIngresado = textoIngresado.trim();
            if (textoIngresado.equals(op1) || textoIngresado.equals(op2) || textoIngresado.equals(op3)) return textoIngresado;
                javax.swing.JOptionPane.showMessageDialog(null, "Intenta de nuevo.");
        }
    }

    /**
     * Solicita al usuario que elija entre cinco opciones de texto.
     * <p>
     * Muestra un cuadro de diálogo y sigue preguntando hasta que el usuario
     * ingrese una de las cinco opciones válidas.
     * </p>
     * @param mensaje El texto que se muestra al usuario para indicar qué elegir
     * @param op1 La primera opción válida
     * @param op2 La segunda opción válida
     * @param op3 La tercera opción válida
     * @param op4 La cuarta opción válida
     * @param op5 La quinta opción válida
     * @return La opción seleccionada por el usuario
     */
    public String leerOpcionTexto(String mensaje, String op1, String op2, String op3, String op4, String op5) {
        while (true) {
            String textoIngresado = javax.swing.JOptionPane.showInputDialog(mensaje);
            if (textoIngresado == null) {
                javax.swing.JOptionPane.showMessageDialog(null, "Intenta de nuevo.");
                continue;
            }
            textoIngresado = textoIngresado.trim();
            if (textoIngresado.equals(op1) || textoIngresado.equals(op2) || textoIngresado.equals(op3) || textoIngresado.equals(op4) || textoIngresado.equals(op5)) return textoIngresado;
                javax.swing.JOptionPane.showMessageDialog(null, "Intenta de nuevo.");
        }
    }

    /**
     * Convierte un texto a número entero de forma segura.
     * <p>
     * Intenta convertir la cadena a entero. Si la conversión falla,
     * retorna el valor de respaldo en lugar de lanzar una excepción.
     * </p>
     * @param textoIngresado La cadena de texto que se desea convertir
     * @param fallback El valor que se retorna si la conversión falla
     * @return El número entero resultante, o el valor de respaldo si hubo error
     */
    public int parseEnteroSeguro(String textoIngresado, int fallback) {
        try {
            return Integer.parseInt(textoIngresado);
        } catch (Exception e) {
            return fallback;
        }
    }
}

