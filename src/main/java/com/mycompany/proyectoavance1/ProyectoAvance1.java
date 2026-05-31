package com.mycompany.proyectoavance1;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

// Punto de entrada — configura FlatLaf antes de cualquier componente Swing
public class ProyectoAvance1 {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TabbedPane.selectedBackground", new Color(232, 245, 237));

        SwingUtilities.invokeLater(() -> {
            Proyecto proyecto = new Proyecto();
            proyecto.iniciar();
        });
    }
}
