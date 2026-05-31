package com.mycompany.proyectoavance1.ui;

import com.mycompany.proyectoavance1.Proyecto;
import javax.swing.*;
import java.awt.*;

public class TicketFormPanel extends JPanel {

    private final Proyecto proyecto;
    private JTextField fNombre, fId, fEdad;
    private JComboBox<String> cbMoneda, cbServicio;
    private JToggleButton btnP, btnD, btnN;
    private JLabel lblResultado;

    private static final Color COLOR_P = new Color(123, 97, 190);
    private static final Color COLOR_D = new Color(25, 118, 210);

    public TicketFormPanel(Proyecto proyecto) {
        this.proyecto = proyecto;
        setBackground(AppTheme.BG);
        setLayout(new BorderLayout());

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(AppTheme.BG);
        norte.add(AppTheme.panelEncabezado("Nuevo Ticket"), BorderLayout.NORTH);
        norte.add(AppTheme.crearInfoPanel(
            "Complete el formulario para registrar un ticket de pasajero. " +
            "Elija el tipo de bus (P=Preferencial, D=Directo, N=Normal) y el servicio deseado. " +
            "El ticket se asignara al bus disponible mas adecuado."), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);
        add(construirFormulario(), BorderLayout.CENTER);
    }

    private JPanel construirFormulario() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setBackground(AppTheme.BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 28, 28, 28));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.BG_PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        fNombre    = AppTheme.field(20);
        fId        = AppTheme.field(12);
        fEdad      = AppTheme.field(6);
        cbMoneda   = AppTheme.combo(new String[]{"CRC", "USD"});
        cbServicio = AppTheme.combo(new String[]{"VIP", "REGULAR", "CARGA", "EJECUTIVO"});

        String[] etiquetas = {"Nombre del pasajero:", "Cedula / ID:", "Edad:", "Moneda:", "Servicio:"};
        JComponent[] campos = { fNombre, fId, fEdad, cbMoneda, cbServicio };

        for (int i = 0; i < campos.length; i++) {
            g.gridx = 0; g.gridy = i; g.gridwidth = 1;
            form.add(AppTheme.label(etiquetas[i]), g);
            g.gridx = 1;
            form.add(campos[i], g);
        }

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER);
        form.add(sep, g);

        g.gridy = 6;
        JLabel lblTipo = AppTheme.label("Tipo de bus:");
        lblTipo.setFont(AppTheme.HEADER);
        form.add(lblTipo, g);

        btnP = tipoBtn("P  Preferencial", COLOR_P,
            "Bus tipo Preferencial: mayor costo, servicio prioritario");
        btnD = tipoBtn("D  Directo",      COLOR_D,
            "Bus tipo Directo: sin paradas intermedias");
        btnN = tipoBtn("N  Normal",        AppTheme.GREEN,
            "Bus tipo Normal: servicio estandar");
        btnN.setSelected(true);
        actualizarColorTipo();

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(btnP); grupo.add(btnD); grupo.add(btnN);
        btnP.addItemListener(e -> actualizarColorTipo());
        btnD.addItemListener(e -> actualizarColorTipo());
        btnN.addItemListener(e -> actualizarColorTipo());

        JPanel tipoPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tipoPnl.setBackground(AppTheme.BG_PANEL);
        tipoPnl.add(btnP); tipoPnl.add(btnD); tipoPnl.add(btnN);
        g.gridy = 7;
        form.add(tipoPnl, g);

        JButton btnCrear = AppTheme.btnPrimary("Crear ticket",
            "Registrar el ticket con los datos ingresados");
        g.gridy = 8;
        form.add(btnCrear, g);

        lblResultado = new JLabel(" ");
        lblResultado.setFont(AppTheme.BODY);
        lblResultado.setForeground(AppTheme.TEXT_MUTED);
        g.gridy = 9;
        form.add(lblResultado, g);

        btnCrear.addActionListener(e -> enviar());
        wrapper.add(form);
        return wrapper;
    }

    private JToggleButton tipoBtn(String texto, Color color, String tooltip) {
        JToggleButton b = new JToggleButton(texto);
        b.setFont(AppTheme.BODY_BOLD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBackground(AppTheme.HOVER);
        b.setForeground(AppTheme.TEXT);
        b.setToolTipText(tooltip);
        b.putClientProperty("color", color);
        return b;
    }

    private void actualizarColorTipo() {
        for (JToggleButton b : new JToggleButton[]{btnP, btnD, btnN}) {
            Color c = (Color) b.getClientProperty("color");
            if (b.isSelected()) {
                b.setBackground(c);
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(AppTheme.HOVER);
                b.setForeground(AppTheme.TEXT);
            }
        }
    }

    private void enviar() {
        String nombre = fNombre.getText().trim();
        if (nombre.isEmpty()) { mostrarMsg("El nombre es obligatorio.", false); return; }
        int id, edad;
        try { id   = Integer.parseInt(fId.getText().trim()); }
        catch (Exception ex) { mostrarMsg("El ID debe ser un numero.", false); return; }
        try { edad = Integer.parseInt(fEdad.getText().trim()); }
        catch (Exception ex) { mostrarMsg("La edad debe ser un numero.", false); return; }
        String moneda   = (String) cbMoneda.getSelectedItem();
        String servicio = (String) cbServicio.getSelectedItem();
        char tipo = btnP.isSelected() ? 'P' : btnD.isSelected() ? 'D' : 'N';

        String resultado = proyecto.crearTicket(nombre, id, edad, moneda, servicio, tipo);
        boolean ok = resultado.startsWith("OK");
        mostrarMsg("<html>" + resultado.replace("\n", "<br>") + "</html>", ok);
        if (ok) limpiar();
    }

    private void mostrarMsg(String txt, boolean ok) {
        lblResultado.setText(txt);
        lblResultado.setForeground(ok ? AppTheme.GREEN : AppTheme.RED);
    }

    private void limpiar() {
        fNombre.setText("");
        fId.setText("");
        fEdad.setText("");
        cbMoneda.setSelectedIndex(0);
        cbServicio.setSelectedIndex(0);
        btnN.setSelected(true);
        actualizarColorTipo();
    }
}
