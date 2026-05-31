package com.mycompany.proyectoavance1.ui;

import com.mycompany.proyectoavance1.Proyecto;
import javax.swing.*;
import java.awt.*;

// Pantalla de inicio con tarjetas de metricas del sistema
public class DashboardPanel extends JPanel {

    private final Proyecto proyecto;
    private JLabel valBusesActivos, valPendientes, valAtendidos, valTerminal;

    public DashboardPanel(Proyecto proyecto) {
        this.proyecto = proyecto;
        setBackground(AppTheme.BG_PANEL);
        setLayout(new BorderLayout(0, 0));

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(AppTheme.BG_PANEL);
        norte.add(AppTheme.panelEncabezado("Inicio"), BorderLayout.NORTH);
        norte.add(AppTheme.crearInfoPanel(
            "Muestra un resumen en tiempo real del estado del sistema: " +
            "cuantos buses tienen clientes, cuantos tickets aguardan en cola, " +
            "cuantos han sido atendidos y el nombre de la terminal activa. " +
            "Use el boton Actualizar para refrescar los valores."), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        add(construirTarjetas(), BorderLayout.CENTER);
    }

    private JPanel construirTarjetas() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppTheme.BG_PANEL);
        wrapper.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(AppTheme.BG_PANEL);

        valBusesActivos = new JLabel("0");
        valPendientes   = new JLabel("0");
        valAtendidos    = new JLabel("0");
        valTerminal     = new JLabel("—");

        grid.add(tarjeta("Buses en actividad",  valBusesActivos, AppTheme.GREEN,
            "Buses que tienen al menos un cliente en atencion o en cola"));
        grid.add(tarjeta("Tickets en cola",     valPendientes,   AppTheme.ORANGE,
            "Total de tickets pendientes de atencion en todos los buses"));
        grid.add(tarjeta("Total atendidos",     valAtendidos,    AppTheme.GREEN_DARK,
            "Tickets que han sido completamente atendidos y registrados"));
        grid.add(tarjeta("Terminal activa",     valTerminal,     AppTheme.TEXT_SEC,
            "Nombre de la terminal configurada en el sistema"));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botones.setBackground(AppTheme.BG_PANEL);
        JButton btnRef = AppTheme.btnNeutral("Actualizar metricas",
            "Recargar los valores mostrados con los datos actuales del sistema");
        btnRef.addActionListener(e -> refrescar());
        botones.add(btnRef);

        wrapper.add(grid, BorderLayout.CENTER);
        wrapper.add(botones, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel tarjeta(String etiqueta, JLabel valor, Color colorValor, String tooltip) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(AppTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)));
        card.setToolTipText(tooltip);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(AppTheme.HEADER);
        lbl.setForeground(AppTheme.TEXT_SEC);
        card.add(lbl, BorderLayout.NORTH);

        valor.setFont(AppTheme.BIG);
        valor.setForeground(colorValor);
        card.add(valor, BorderLayout.CENTER);

        // Barra de color en la parte inferior de la tarjeta
        JPanel barra = new JPanel();
        barra.setBackground(colorValor);
        barra.setPreferredSize(new Dimension(0, 3));
        card.add(barra, BorderLayout.SOUTH);
        return card;
    }

    public void refrescar() {
        valBusesActivos.setText(String.valueOf(proyecto.getBusesActivos()));
        valPendientes.setText(String.valueOf(proyecto.getTotalTicketsPendientes()));
        valAtendidos.setText(String.valueOf(proyecto.getTotalAtendidos()));
        valTerminal.setText(proyecto.getNombreTerminal());
    }
}
