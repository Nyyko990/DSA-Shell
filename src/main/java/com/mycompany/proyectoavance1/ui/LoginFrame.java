package com.mycompany.proyectoavance1.ui;

import com.mycompany.proyectoavance1.Proyecto;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private final Proyecto proyecto;
    private int intentos = 3;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    public LoginFrame(Proyecto proyecto) {
        this.proyecto = proyecto;
        setTitle("BusNovaTech — Acceso");
        setSize(460, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { salir(); }
        });
        root.setBackground(AppTheme.BG);
        add(root);
        root.add(panelLogin(), "login");
        if (proyecto.requiereConfiguracion()) {
            root.add(panelSetup(), "setup");
            cards.show(root, "setup");
        } else {
            cards.show(root, "login");
        }
    }

    private JPanel panelLogin() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(AppTheme.BG);

        // Tarjeta centrada con borde sutil
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            BorderFactory.createEmptyBorder(32, 36, 28, 36)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Logo / titulo
        JLabel titulo = new JLabel("BusNovaTech", SwingConstants.CENTER);
        titulo.setFont(AppTheme.TITLE);
        titulo.setForeground(AppTheme.GREEN_DARK);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.insets = new Insets(0, 8, 4, 8);
        card.add(titulo, g);

        JLabel sub = new JLabel("Sistema de gestion de terminal", SwingConstants.CENTER);
        sub.setFont(AppTheme.SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);
        g.gridy = 1; g.insets = new Insets(0, 8, 20, 8);
        card.add(sub, g);

        // Separador visual
        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER);
        g.gridy = 2; g.insets = new Insets(0, 0, 16, 0);
        card.add(sep, g);

        g.insets = new Insets(6, 8, 6, 8);
        JTextField fUser = AppTheme.field(18);
        JPasswordField fPass = AppTheme.pwField();

        g.gridwidth = 1; g.gridy = 3; g.gridx = 0;
        card.add(AppTheme.label("Usuario:"), g);
        g.gridx = 1; card.add(fUser, g);

        g.gridy = 4; g.gridx = 0;
        card.add(AppTheme.label("Contrasena:"), g);
        g.gridx = 1; card.add(fPass, g);

        // Indicador textual de intentos restantes
        JLabel lblIntentos = new JLabel("Intentos restantes: 3 de 3", SwingConstants.CENTER);
        lblIntentos.setFont(AppTheme.SMALL_BOLD);
        lblIntentos.setForeground(AppTheme.GREEN);
        g.gridwidth = 2; g.gridx = 0; g.gridy = 5; g.insets = new Insets(12, 8, 4, 8);
        card.add(lblIntentos, g);

        JLabel lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(AppTheme.SMALL);
        lblError.setForeground(AppTheme.RED);
        g.gridy = 6; g.insets = new Insets(0, 8, 8, 8);
        card.add(lblError, g);

        JButton btnLogin = AppTheme.btnPrimary("Ingresar al sistema", "Ingresar con las credenciales indicadas");
        g.gridy = 7; g.insets = new Insets(4, 8, 0, 8);
        card.add(btnLogin, g);

        ActionListener onLogin = e -> {
            String u  = fUser.getText().trim();
            String pw = new String(fPass.getPassword()).trim();
            if (proyecto.intentarLogin(u, pw)) {
                dispose();
                SwingUtilities.invokeLater(() -> new MainFrame(proyecto).setVisible(true));
            } else {
                intentos--;
                fPass.setText("");
                if (intentos <= 0) {
                    lblIntentos.setText("Acceso bloqueado");
                    lblIntentos.setForeground(AppTheme.RED);
                    lblError.setText("Demasiados intentos. Cerrando...");
                    btnLogin.setEnabled(false);
                    Timer t = new Timer(2000, ev -> salir());
                    t.setRepeats(false); t.start();
                } else {
                    lblIntentos.setText("Intentos restantes: " + intentos + " de 3");
                    lblIntentos.setForeground(intentos == 1 ? AppTheme.ORANGE : AppTheme.GREEN);
                    lblError.setText("Credenciales incorrectas.");
                }
            }
        };
        btnLogin.addActionListener(onLogin);
        fPass.addActionListener(onLogin);

        outer.add(card);
        return outer;
    }

    private JPanel panelSetup() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(AppTheme.BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            BorderFactory.createEmptyBorder(28, 36, 24, 36)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Configuracion inicial", SwingConstants.CENTER);
        titulo.setFont(AppTheme.TITLE);
        titulo.setForeground(AppTheme.GREEN_DARK);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.insets = new Insets(0, 8, 20, 8);
        card.add(titulo, g);

        JTextField fTerminal = AppTheme.field(18);
        JTextField fBuses    = AppTheme.field(6);
        JTextField fUsuario  = AppTheme.field(18);
        JPasswordField fClave = AppTheme.pwField();

        String[] etiquetas = {
            "Nombre de la terminal:",
            "Cantidad de buses (minimo 3):",
            "Usuario administrador:",
            "Contrasena:"
        };
        JComponent[] campos = { fTerminal, fBuses, fUsuario, fClave };

        g.insets = new Insets(6, 8, 6, 8);
        for (int i = 0; i < 4; i++) {
            g.gridwidth = 1; g.gridy = i + 1; g.gridx = 0;
            card.add(AppTheme.label(etiquetas[i]), g);
            g.gridx = 1; card.add(campos[i], g);
        }

        JLabel lblErr = new JLabel(" ", SwingConstants.CENTER);
        lblErr.setFont(AppTheme.SMALL);
        lblErr.setForeground(AppTheme.RED);
        g.gridy = 5; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(8, 8, 4, 8);
        card.add(lblErr, g);

        JButton btnGuardar = AppTheme.btnPrimary(
            "Guardar y continuar",
            "Guardar la configuracion inicial y acceder al sistema");
        g.gridy = 6; g.insets = new Insets(4, 8, 0, 8);
        card.add(btnGuardar, g);

        btnGuardar.addActionListener(e -> {
            String terminal = fTerminal.getText().trim();
            String busStr   = fBuses.getText().trim();
            String usuario  = fUsuario.getText().trim();
            String clave    = new String(fClave.getPassword()).trim();
            if (terminal.isEmpty() || usuario.isEmpty() || clave.isEmpty()) {
                lblErr.setText("Todos los campos son obligatorios."); return;
            }
            int buses;
            try { buses = Integer.parseInt(busStr); }
            catch (Exception ex) { lblErr.setText("Cantidad de buses invalida."); return; }
            if (buses < 3) { lblErr.setText("Se requieren al menos 3 buses."); return; }
            proyecto.configurarSistema(terminal, buses, usuario, clave);
            cards.show(root, "login");
        });

        outer.add(card);
        return outer;
    }

    private void salir() { proyecto.salir(); System.exit(0); }
}
