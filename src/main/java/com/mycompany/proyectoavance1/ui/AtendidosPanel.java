package com.mycompany.proyectoavance1.ui;

import com.mycompany.proyectoavance1.Proyecto;
import com.mycompany.proyectoavance1.Ticket;
import com.mycompany.proyectoavance1.ListaTickets;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AtendidosPanel extends JPanel {

    private final Proyecto proyecto;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JTextField fBuscar;
    private List<String[]> todosLosTickets = new ArrayList<>();

    private static final String[] COLUMNAS = {
        "Nombre", "ID", "Servicio", "Tipo Bus", "Monto", "Estado", "Fecha atencion"
    };

    public AtendidosPanel(Proyecto proyecto) {
        this.proyecto = proyecto;
        setBackground(AppTheme.BG);
        setLayout(new BorderLayout(0, 0));

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(AppTheme.BG);
        norte.add(AppTheme.panelEncabezado("Atendidos"), BorderLayout.NORTH);
        norte.add(AppTheme.crearInfoPanel(
            "Historial de todos los tickets que ya fueron atendidos. " +
            "Use el campo de busqueda para filtrar por nombre o tipo de servicio. " +
            "Presione Actualizar para ver los cambios mas recientes."), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        modelo = new DefaultTableModel(COLUMNAS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        estilizarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(AppTheme.CARD);
        scroll.getViewport().setBackground(AppTheme.CARD);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        fBuscar = AppTheme.field(30);
        fBuscar.putClientProperty("JTextField.placeholderText", "Buscar por nombre o servicio...");
        fBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        JButton btnRefresh = AppTheme.btnNeutral("Actualizar",
            "Recargar la lista de tickets atendidos");
        btnRefresh.addActionListener(e -> refrescar());

        JPanel barraFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barraFiltro.setBackground(AppTheme.BG);
        barraFiltro.setBorder(BorderFactory.createEmptyBorder(12, 28, 8, 28));
        barraFiltro.add(AppTheme.label("Buscar:"));
        barraFiltro.add(fBuscar);
        barraFiltro.add(Box.createHorizontalStrut(8));
        barraFiltro.add(btnRefresh);

        JPanel centro = new JPanel(new BorderLayout(0, 0));
        centro.setBackground(AppTheme.BG);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 28, 24, 28));
        centro.add(barraFiltro, BorderLayout.NORTH);
        centro.add(scroll, BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);
        refrescar();
    }

    private void estilizarTabla() {
        tabla.setBackground(AppTheme.CARD);
        tabla.setForeground(AppTheme.TEXT);
        tabla.setFont(AppTheme.BODY);
        tabla.setGridColor(AppTheme.BORDER);
        tabla.setRowHeight(32);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(AppTheme.SIDEBAR);
        header.setForeground(AppTheme.TEXT);
        header.setFont(AppTheme.HEADER);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String estado = (String) t.getModel().getValueAt(row, 5);
                Color bg;
                if (sel) {
                    bg = AppTheme.HOVER;
                } else if ("Atendido".equals(estado)) {
                    bg = AppTheme.ROW_N;
                } else if ("No Pagado".equals(estado)) {
                    bg = AppTheme.ORANGE_LIGHT;
                } else {
                    bg = AppTheme.CARD;
                }
                setBackground(bg);
                setForeground(AppTheme.TEXT);
                setFont(AppTheme.BODY);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setOpaque(true);
                return this;
            }
        };
        for (int i = 0; i < COLUMNAS.length; i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public void refrescar() {
        todosLosTickets = new ArrayList<>();
        ListaTickets lista = proyecto.getAtendidos();
        if (lista != null) {
            for (int i = 0; i < lista.tamano(); i++) {
                Ticket t = lista.obtener(i);
                if (t == null) continue;
                todosLosTickets.add(new String[]{
                    t.getNombre(),
                    String.valueOf(t.getId()),
                    t.getServicio(),
                    String.valueOf(t.getTipoBus()),
                    String.format("%.2f", t.getMontoCobrado()),
                    t.getEstadoTicket(),
                    t.getFechaHoraAtencion()
                });
            }
        }
        filtrar();
    }

    private void filtrar() {
        String texto = fBuscar.getText().trim().toLowerCase();
        modelo.setRowCount(0);
        for (String[] fila : todosLosTickets) {
            if (texto.isEmpty()
                    || fila[0].toLowerCase().contains(texto)
                    || fila[2].toLowerCase().contains(texto)) {
                modelo.addRow(fila);
            }
        }
    }
}
