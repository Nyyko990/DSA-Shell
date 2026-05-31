package com.mycompany.proyectoavance1.ui;

import com.mycompany.proyectoavance1.Proyecto;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private final Proyecto proyecto;
    private final CardLayout cards = new CardLayout();
    private final JPanel contenido = new JPanel(cards);

    private final DashboardPanel  pDash;
    private final TicketFormPanel pTickets;
    private final BusTablePanel   pBuses;
    private final GrafoPanel      pRutas;
    private final BCCRPanel       pBCCR;
    private final AtendidosPanel  pAtendidos;

    private JButton[] navBtns;
    private final String[] navCards = {
        "dashboard", "tickets", "buses", "rutas", "bccr", "atendidos"
    };

    public MainFrame(Proyecto proyecto) {
        this.proyecto = proyecto;
        setTitle("BusNovaTech — " + proyecto.getNombreTerminal());
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(860, 560));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { proyecto.salir(); System.exit(0); }
        });

        pDash      = new DashboardPanel(proyecto);
        pTickets   = new TicketFormPanel(proyecto);
        pBuses     = new BusTablePanel(proyecto);
        pRutas     = new GrafoPanel(proyecto);
        pBCCR      = new BCCRPanel(proyecto);
        pAtendidos = new AtendidosPanel(proyecto);

        contenido.setBackground(AppTheme.BG_PANEL);
        contenido.add(pDash,      "dashboard");
        contenido.add(pTickets,   "tickets");
        contenido.add(pBuses,     "buses");
        contenido.add(pRutas,     "rutas");
        contenido.add(pBCCR,      "bccr");
        contenido.add(pAtendidos, "atendidos");

        getContentPane().setBackground(AppTheme.BG);
        add(construirSidebar(), BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);
        mostrar("dashboard");
    }

    private JPanel construirSidebar() {
        JPanel side = new JPanel();
        side.setBackground(AppTheme.SIDEBAR);
        side.setPreferredSize(new Dimension(200, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER));

        // Bloque de nombre de la terminal
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 2));
        header.setBackground(AppTheme.SIDEBAR);
        header.setBorder(BorderFactory.createEmptyBorder(18, 16, 14, 16));
        header.setMaximumSize(new Dimension(200, 70));

        JLabel logo = new JLabel("BusNovaTech");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logo.setForeground(AppTheme.GREEN_DARK);

        JLabel terminal = new JLabel(proyecto.getNombreTerminal());
        terminal.setFont(AppTheme.SMALL);
        terminal.setForeground(AppTheme.TEXT_MUTED);

        header.add(logo);
        header.add(terminal);
        side.add(header);

        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER);
        sep.setMaximumSize(new Dimension(200, 1));
        side.add(sep);
        side.add(Box.createVerticalStrut(8));

        // Items de navegacion — texto plano, sin emojis
        String[][] nav = {
            {"Inicio",     "dashboard", "Ver métricas generales del sistema"},
            {"Tickets",    "tickets",   "Crear un nuevo ticket para un pasajero"},
            {"Buses",      "buses",     "Ver estado de buses y atender clientes"},
            {"Rutas",      "rutas",     "Gestionar el mapa de rutas del grafo"},
            {"BCCR",       "bccr",      "Consultar el tipo de cambio del Banco Central"},
            {"Atendidos",  "atendidos", "Ver historial de tickets atendidos"},
        };
        navBtns = new JButton[nav.length];
        for (int i = 0; i < nav.length; i++) {
            final int idx = i;
            JButton b = navBtn(nav[i][0], nav[i][2]);
            b.addActionListener(e -> { mostrar(navCards[idx]); resaltarNav(navBtns[idx]); });
            navBtns[i] = b;
            side.add(b);
        }

        side.add(Box.createVerticalGlue());

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(AppTheme.BORDER);
        sep2.setMaximumSize(new Dimension(200, 1));
        side.add(sep2);

        JButton btnSalir = navBtn("Salir", "Guardar todos los datos y cerrar la aplicacion");
        btnSalir.setForeground(AppTheme.ORANGE);
        btnSalir.setFont(AppTheme.BODY_BOLD);
        btnSalir.addActionListener(e -> { proyecto.salir(); System.exit(0); });
        side.add(btnSalir);
        side.add(Box.createVerticalStrut(10));
        return side;
    }

    private JButton navBtn(String texto, String tooltip) {
        JButton b = new JButton(texto);
        b.setBackground(AppTheme.SIDEBAR);
        b.setForeground(AppTheme.TEXT);
        b.setFont(AppTheme.BODY);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(200, 44));
        b.setPreferredSize(new Dimension(200, 44));
        b.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText(tooltip);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!AppTheme.GREEN.equals(b.getForeground())) b.setBackground(AppTheme.HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (!AppTheme.GREEN.equals(b.getForeground())) b.setBackground(AppTheme.SIDEBAR);
            }
        });
        return b;
    }

    private void mostrar(String card) {
        cards.show(contenido, card);
        switch (card) {
            case "dashboard" -> pDash.refrescar();
            case "buses"     -> pBuses.refrescar();
            case "atendidos" -> pAtendidos.refrescar();
            case "rutas"     -> pRutas.refrescar();
        }
    }

    private void resaltarNav(JButton activo) {
        if (navBtns == null) return;
        for (JButton b : navBtns) {
            b.setBackground(AppTheme.SIDEBAR);
            b.setForeground(AppTheme.TEXT);
            b.setFont(AppTheme.BODY);
        }
        activo.setBackground(AppTheme.SELECTED);
        activo.setForeground(AppTheme.GREEN);
        activo.setFont(AppTheme.BODY_BOLD);
    }
}
