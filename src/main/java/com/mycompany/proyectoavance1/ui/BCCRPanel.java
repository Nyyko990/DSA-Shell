package com.mycompany.proyectoavance1.ui;

import com.mycompany.proyectoavance1.Proyecto;
import javax.swing.*;
import java.awt.*;

public class BCCRPanel extends JPanel {

    private final Proyecto proyecto;
    private final JTextArea areaResultado;
    private final JTextField fCorreo;
    private final JTextField fToken;
    private final JLabel lblEstado;

    public BCCRPanel(Proyecto proyecto) {
        this.proyecto = proyecto;
        setBackground(AppTheme.BG);
        setLayout(new BorderLayout(0, 0));

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(AppTheme.BG);
        norte.add(AppTheme.panelEncabezado("Tipo de Cambio BCCR"), BorderLayout.NORTH);
        norte.add(AppTheme.crearInfoPanel(
            "Consulta el tipo de cambio del dolar publicado por el Banco Central de Costa Rica. " +
            "Ingrese su correo y token registrados en el API del BCCR para la consulta en linea, " +
            "o use las tasas predeterminadas del sistema si no dispone de credenciales."), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setBackground(AppTheme.BG);
        centro.setBorder(BorderFactory.createEmptyBorder(16, 28, 24, 28));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(AppTheme.BG_PANEL);
        formulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblSub = new JLabel("Consulta en linea — requiere credenciales del API del BCCR");
        lblSub.setFont(AppTheme.BODY);
        lblSub.setForeground(AppTheme.TEXT_SEC);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        formulario.add(lblSub, g);

        fCorreo = AppTheme.field(20);
        fToken  = AppTheme.field(20);
        fCorreo.setToolTipText("Correo electronico registrado en el servicio del BCCR");
        fToken.setToolTipText("Token de acceso proporcionado por el BCCR");

        g.gridwidth = 1; g.gridy = 1; g.gridx = 0;
        formulario.add(AppTheme.label("Correo registrado:"), g);
        g.gridx = 1; formulario.add(fCorreo, g);

        g.gridy = 2; g.gridx = 0;
        formulario.add(AppTheme.label("Token de acceso:"), g);
        g.gridx = 1; formulario.add(fToken, g);

        lblEstado = new JLabel(" ", SwingConstants.CENTER);
        lblEstado.setFont(AppTheme.SMALL);
        lblEstado.setForeground(AppTheme.TEXT_MUTED);
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        formulario.add(lblEstado, g);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(AppTheme.BG_PANEL);

        JButton btnEnLinea = AppTheme.btnPrimary("Consultar en linea",
            "Conectarse al API del BCCR con las credenciales ingresadas");
        JButton btnDefault = AppTheme.btnNeutral("Usar tasas predeterminadas",
            "Mostrar las tasas de cambio fijas configuradas en el sistema");

        btnEnLinea.addActionListener(e -> consultarEnLinea());
        btnDefault.addActionListener(e -> consultarDefault());

        btnPanel.add(btnEnLinea);
        btnPanel.add(btnDefault);

        g.gridy = 4;
        formulario.add(btnPanel, g);
        centro.add(formulario, BorderLayout.NORTH);

        areaResultado = AppTheme.textArea();
        areaResultado.setRows(10);
        areaResultado.setText("Presione un boton para consultar el tipo de cambio.");
        JScrollPane scroll = new JScrollPane(areaResultado);
        scroll.setBackground(AppTheme.CARD);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        centro.add(scroll, BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);
    }

    private void consultarEnLinea() {
        String correo = fCorreo.getText().trim();
        String token  = fToken.getText().trim();
        if (correo.isEmpty() || token.isEmpty()) {
            areaResultado.setText("Ingrese correo y token para la consulta en linea.");
            return;
        }
        lblEstado.setText("Consultando...");
        lblEstado.setForeground(AppTheme.ORANGE);
        areaResultado.setText("Conectando con el BCCR...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() {
                return proyecto.consultarBCCREnLinea(correo, token);
            }
            protected void done() {
                try {
                    String res = get();
                    areaResultado.setText(res);
                    lblEstado.setText("Consulta completada.");
                    lblEstado.setForeground(AppTheme.GREEN);
                } catch (Exception ex) {
                    areaResultado.setText("Error al consultar: " + ex.getMessage());
                    lblEstado.setText("Error de conexion.");
                    lblEstado.setForeground(AppTheme.RED);
                }
            }
        };
        worker.execute();
    }

    private void consultarDefault() {
        lblEstado.setText("Usando tasas predeterminadas.");
        lblEstado.setForeground(AppTheme.TEXT_MUTED);
        areaResultado.setText(proyecto.consultarBCCRPredeterminado());
    }
}
