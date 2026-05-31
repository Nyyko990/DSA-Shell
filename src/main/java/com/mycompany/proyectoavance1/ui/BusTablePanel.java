package com.mycompany.proyectoavance1.ui;

import com.mycompany.proyectoavance1.Bus;
import com.mycompany.proyectoavance1.Proyecto;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

// Tabla de buses con codigo de color por tipo y controles de gestion
public class BusTablePanel extends JPanel {

    private final Proyecto proyecto;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JLabel lblMsg;

    private static final String[] COLUMNAS = {"Bus #", "Tipo", "En servicio", "En cola"};

    public BusTablePanel(Proyecto proyecto) {
        this.proyecto = proyecto;
        setBackground(AppTheme.BG_PANEL);
        setLayout(new BorderLayout(0, 0));

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(AppTheme.BG_PANEL);
        norte.add(AppTheme.panelEncabezado("Buses"), BorderLayout.NORTH);
        norte.add(AppTheme.crearInfoPanel(
            "Lista todos los buses del sistema con su estado actual. " +
            "Seleccione un bus y presione Atender para procesar el siguiente cliente. " +
            "Use los botones de agregar o eliminar para ajustar la flota. " +
            "Colores: morado = Preferencial, azul = Directo, verde = Normal."),
            BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        modelo = new DefaultTableModel(COLUMNAS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        estilizarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.getViewport().setBackground(AppTheme.CARD);

        JPanel scrollWrap = new JPanel(new BorderLayout());
        scrollWrap.setBackground(AppTheme.BG_PANEL);
        scrollWrap.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        scrollWrap.add(scroll, BorderLayout.CENTER);
        add(scrollWrap, BorderLayout.CENTER);

        lblMsg = new JLabel(" ");
        lblMsg.setFont(AppTheme.SMALL);
        lblMsg.setForeground(AppTheme.TEXT_MUTED);

        add(construirControles(), BorderLayout.SOUTH);
        refrescar();
    }

    private void estilizarTabla() {
        tabla.setBackground(AppTheme.CARD);
        tabla.setForeground(AppTheme.TEXT);
        tabla.setFont(AppTheme.BODY);
        tabla.setGridColor(AppTheme.BORDER);
        tabla.setRowHeight(36);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(240, 244, 241));
        header.setForeground(AppTheme.TEXT_SEC);
        header.setFont(AppTheme.BODY_BOLD);
        header.setReorderingAllowed(false);

        int[] anchos = {65, 80, 250, 90};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        BusRenderer renderer = new BusRenderer();
        for (int i = 0; i < COLUMNAS.length; i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private JPanel construirControles() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(AppTheme.BG_PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(12, 24, 20, 24));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setBackground(AppTheme.BG_PANEL);

        JCheckBox chkPagado = new JCheckBox("Marcar como pagado");
        chkPagado.setFont(AppTheme.BODY);
        chkPagado.setBackground(AppTheme.BG_PANEL);
        chkPagado.setSelected(true);
        chkPagado.setToolTipText("Indica si el cliente realizo el pago al ser atendido");

        JButton btnAtender  = AppTheme.btnPrimary("Atender siguiente",
            "Atender el proximo ticket del bus seleccionado en la tabla");
        JButton btnAgregar  = AppTheme.btnNeutral("+ Agregar buses",
            "Agregar uno o mas buses nuevos a la flota del sistema");
        JButton btnEliminar = AppTheme.btnSecondary("- Eliminar buses",
            "Eliminar buses vacios del final de la lista");

        btnAtender.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { mostrarMsg("Seleccione un bus de la tabla primero.", false); return; }
            int numBus = (int) modelo.getValueAt(fila, 0);
            String res = proyecto.atenderSiguiente(numBus, chkPagado.isSelected());
            mostrarMsg(res, res.startsWith("OK"));
            refrescar();
        });

        btnAgregar.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this,
                "Cantidad de buses a agregar:", "Agregar buses", JOptionPane.QUESTION_MESSAGE);
            if (input == null || input.trim().isEmpty()) return;
            try {
                String res = proyecto.agregarBuses(Integer.parseInt(input.trim()));
                mostrarMsg(res, res.startsWith("OK")); refrescar();
            } catch (Exception ex) { mostrarMsg("Ingrese un numero valido.", false); }
        });

        btnEliminar.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this,
                "Cantidad de buses a eliminar:", "Eliminar buses", JOptionPane.QUESTION_MESSAGE);
            if (input == null || input.trim().isEmpty()) return;
            try {
                String res = proyecto.eliminarBuses(Integer.parseInt(input.trim()));
                mostrarMsg(res, res.startsWith("OK")); refrescar();
            } catch (Exception ex) { mostrarMsg("Ingrese un numero valido.", false); }
        });

        botones.add(chkPagado);
        botones.add(Box.createHorizontalStrut(8));
        botones.add(btnAtender);
        botones.add(Box.createHorizontalStrut(16));
        botones.add(btnAgregar);
        botones.add(btnEliminar);

        p.add(botones, BorderLayout.NORTH);
        p.add(lblMsg,  BorderLayout.SOUTH);
        return p;
    }

    public void refrescar() {
        modelo.setRowCount(0);
        for (int i = 0; i < proyecto.getListaBuses().tamano(); i++) {
            Bus bus = proyecto.getListaBuses().obtenerBusEnPosicion(i);
            if (bus == null) continue;
            String enServicio = bus.getTicketEnAtencion() != null
                ? bus.getTicketEnAtencion().getNombre() : "libre";
            modelo.addRow(new Object[]{
                bus.getNumeroBus(),
                String.valueOf(bus.getTipoBus()),
                enServicio,
                bus.cantidadEnFila()
            });
        }
    }

    private void mostrarMsg(String txt, boolean ok) {
        lblMsg.setText(txt);
        lblMsg.setForeground(ok ? AppTheme.GREEN : AppTheme.RED);
    }

    // Colorea cada fila segun el tipo de bus (columna 1)
    private static class BusRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(
                JTable t, Object val, boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, foc, row, col);
            String tipoStr = (String) t.getModel().getValueAt(row, 1);
            char tipo = (tipoStr != null && !tipoStr.isEmpty()) ? tipoStr.charAt(0) : 'N';
            setBackground(AppTheme.filaBus(tipo, sel));
            setForeground(AppTheme.TEXT);
            setFont(AppTheme.BODY);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setOpaque(true);
            return this;
        }
    }
}
