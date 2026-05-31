package com.mycompany.proyectoavance1.ui;

import javax.swing.*;
import java.awt.*;

// Paleta y fabricas de componentes — tema claro, acento verde/naranja
class AppTheme {

    // Colores base
    static final Color BG           = Color.WHITE;
    static final Color BG_PANEL     = new Color(247, 250, 248);
    static final Color SIDEBAR      = new Color(241, 246, 243);
    static final Color CARD         = Color.WHITE;
    static final Color HOVER        = new Color(226, 242, 232);
    static final Color SELECTED     = new Color(210, 236, 220);
    static final Color GREEN        = new Color(46, 125, 79);
    static final Color GREEN_LIGHT  = new Color(232, 245, 237);
    static final Color GREEN_DARK   = new Color(27, 94, 52);
    static final Color ORANGE       = new Color(230, 81, 0);
    static final Color ORANGE_LIGHT = new Color(255, 243, 224);
    static final Color TEXT         = new Color(30, 30, 30);
    static final Color TEXT_SEC     = new Color(80, 80, 80);
    static final Color TEXT_MUTED   = new Color(145, 145, 145);
    static final Color BORDER       = new Color(210, 220, 213);
    static final Color RED          = new Color(198, 40, 40);

    // Colores de fila de tabla por tipo de bus
    static final Color ROW_P     = new Color(243, 237, 253);
    static final Color ROW_P_SEL = new Color(218, 205, 244);
    static final Color ROW_D     = new Color(231, 241, 255);
    static final Color ROW_D_SEL = new Color(196, 218, 252);
    static final Color ROW_N     = new Color(229, 247, 233);
    static final Color ROW_N_SEL = new Color(193, 233, 202);

    // Fuentes — 14px cuerpo, 16px encabezado
    static final Font TITLE      = new Font("Segoe UI", Font.BOLD, 20);
    static final Font HEADER     = new Font("Segoe UI", Font.BOLD, 16);
    static final Font BODY       = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font BODY_BOLD  = new Font("Segoe UI", Font.BOLD, 14);
    static final Font SMALL      = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    static final Font BIG        = new Font("Segoe UI", Font.BOLD, 32);
    static final Font MONO       = new Font("Consolas", Font.PLAIN, 13);

    // Boton primario verde redondeado
    static JButton btnPrimary(String texto, String tooltip) {
        JButton b = new JButton(texto);
        b.setBackground(GREEN);
        b.setForeground(Color.WHITE);
        b.setFont(BODY_BOLD);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText(tooltip);
        b.putClientProperty("JButton.buttonType", "roundRect");
        return b;
    }

    // Boton secundario naranja redondeado
    static JButton btnSecondary(String texto, String tooltip) {
        JButton b = new JButton(texto);
        b.setBackground(ORANGE);
        b.setForeground(Color.WHITE);
        b.setFont(BODY_BOLD);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText(tooltip);
        b.putClientProperty("JButton.buttonType", "roundRect");
        return b;
    }

    // Boton neutro sin color personalizado
    static JButton btnNeutral(String texto, String tooltip) {
        JButton b = new JButton(texto);
        b.setFont(BODY);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText(tooltip);
        return b;
    }

    static JTextField field(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(BODY);
        return tf;
    }

    static JPasswordField pwField() {
        JPasswordField pf = new JPasswordField(15);
        pf.setFont(BODY);
        return pf;
    }

    static JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(TEXT);
        l.setFont(BODY);
        return l;
    }

    static JComboBox<String> combo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(BODY);
        return cb;
    }

    static JTextArea textArea() {
        JTextArea ta = new JTextArea();
        ta.setFont(MONO);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        return ta;
    }

    // Cabecera de seccion con titulo y borde inferior
    static JPanel panelEncabezado(String titulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            BorderFactory.createEmptyBorder(20, 24, 16, 24)));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(TITLE);
        lbl.setForeground(GREEN_DARK);
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    // Banner colapsable de informacion con borde izquierdo verde
    static JPanel crearInfoPanel(String descripcion) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(GREEN_LIGHT);
        outer.setBorder(BorderFactory.createMatteBorder(0, 4, 1, 0, GREEN));

        JLabel desc = new JLabel(
            "<html><div style='width:580px;padding:2px 0'>" + descripcion + "</div></html>");
        desc.setFont(SMALL);
        desc.setForeground(GREEN_DARK);
        desc.setBorder(BorderFactory.createEmptyBorder(0, 20, 8, 12));
        desc.setVisible(false);

        JButton toggle = new JButton("Informacion  [+]");
        toggle.setBackground(GREEN_LIGHT);
        toggle.setForeground(GREEN_DARK);
        toggle.setFont(SMALL_BOLD);
        toggle.setBorderPainted(false);
        toggle.setFocusPainted(false);
        toggle.setHorizontalAlignment(SwingConstants.LEFT);
        toggle.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.setToolTipText("Mostrar u ocultar la descripcion de esta seccion");
        toggle.addActionListener(e -> {
            boolean vis = !desc.isVisible();
            desc.setVisible(vis);
            toggle.setText(vis ? "Informacion  [-]" : "Informacion  [+]");
            outer.revalidate();
            outer.repaint();
        });

        outer.add(toggle, BorderLayout.NORTH);
        outer.add(desc, BorderLayout.CENTER);
        return outer;
    }

    // Color por tipo de bus para etiquetas y acentos
    static Color busTipo(char t) {
        return t == 'P' ? new Color(123, 97, 190) : t == 'D' ? new Color(25, 118, 210) : GREEN;
    }

    static Color filaBus(char t, boolean sel) {
        if (t == 'P') return sel ? ROW_P_SEL : ROW_P;
        if (t == 'D') return sel ? ROW_D_SEL : ROW_D;
        return sel ? ROW_N_SEL : ROW_N;
    }
}
