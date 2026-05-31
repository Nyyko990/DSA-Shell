package com.mycompany.proyectoavance1.ui;

import com.mycompany.proyectoavance1.Proyecto;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GrafoPanel extends JPanel {

    private final Proyecto proyecto;

    private List<String>  nombresNodos  = new ArrayList<>();
    private List<int[]>   aristas       = new ArrayList<>();
    private List<Boolean> aristasEnRuta = new ArrayList<>();

    private JComboBox<String> cbOrigenRuta, cbDestinoRuta;
    private JComboBox<String> cbOrigenBuscar, cbDestinoBuscar;
    private JLabel lblResultadoRuta;
    private JPanel canvas;

    private static final int RADIO_NODO = 22;
    private static final Color COLOR_NODO_BORDE = new Color(25, 118, 210);

    public GrafoPanel(Proyecto proyecto) {
        this.proyecto = proyecto;
        setBackground(AppTheme.BG);
        setLayout(new BorderLayout(0, 0));

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(AppTheme.BG);
        norte.add(AppTheme.panelEncabezado("Rutas"), BorderLayout.NORTH);
        norte.add(AppTheme.crearInfoPanel(
            "Visualizacion del grafo de rutas entre localidades. " +
            "Agregue localidades y conexiones desde las pestanas inferiores. " +
            "Use 'Ruta mas corta' para calcular el camino optimo entre dos puntos (Dijkstra). " +
            "Las aristas resaltadas en verde muestran la ruta encontrada."), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        canvas = construirCanvas();
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, canvas, construirFormularios());
        split.setResizeWeight(0.6);
        split.setBackground(AppTheme.BG);
        split.setBorder(null);
        split.setDividerSize(4);
        split.getLeftComponent().setBackground(AppTheme.BG);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(AppTheme.BG);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 28, 24, 28));
        centro.add(split, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    private JPanel construirCanvas() {
        JPanel c = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGrafo((Graphics2D) g);
            }
        };
        c.setBackground(Color.WHITE);
        c.setMinimumSize(new Dimension(0, 280));
        c.setPreferredSize(new Dimension(0, 320));
        c.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        return c;
    }

    private JPanel construirFormularios() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(AppTheme.BG);
        tabs.setForeground(AppTheme.TEXT);
        tabs.setFont(AppTheme.BODY);

        tabs.addTab("Agregar localidad", tabLocalidad());
        tabs.addTab("Agregar ruta",      tabRuta());
        tabs.addTab("Ruta mas corta",    tabBuscar());

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.BG);
        p.add(tabs, BorderLayout.CENTER);
        return p;
    }

    private JPanel tabLocalidad() {
        JPanel p = formPanel();
        GridBagConstraints g = gbc();

        JTextField fNombre = AppTheme.field(18);
        JLabel lblRes = resultLabel();

        g.gridy = 0; g.gridx = 0; p.add(AppTheme.label("Nombre de la localidad:"), g);
        g.gridx = 1; p.add(fNombre, g);

        JButton btn = AppTheme.btnPrimary("Agregar localidad",
            "Registrar una nueva localidad en el grafo de rutas");
        g.gridy = 1; g.gridx = 0; g.gridwidth = 2; p.add(btn, g);
        g.gridy = 2; p.add(lblRes, g);

        btn.addActionListener(e -> {
            String res = proyecto.agregarLocalidad(fNombre.getText().trim());
            mostrar(lblRes, res);
            if (res.startsWith("OK")) { fNombre.setText(""); refrescar(); }
        });
        return p;
    }

    private JPanel tabRuta() {
        JPanel p = formPanel();
        GridBagConstraints g = gbc();

        cbOrigenRuta  = AppTheme.combo(new String[]{});
        cbDestinoRuta = AppTheme.combo(new String[]{});
        JTextField fPeso = AppTheme.field(8);
        JLabel lblRes = resultLabel();

        g.gridy = 0; g.gridx = 0; p.add(AppTheme.label("Origen:"),    g); g.gridx = 1; p.add(cbOrigenRuta, g);
        g.gridy = 1; g.gridx = 0; p.add(AppTheme.label("Destino:"),   g); g.gridx = 1; p.add(cbDestinoRuta, g);
        g.gridy = 2; g.gridx = 0; p.add(AppTheme.label("Distancia:"), g); g.gridx = 1; p.add(fPeso, g);

        JButton btn = AppTheme.btnPrimary("Agregar ruta",
            "Crear una conexion dirigida entre dos localidades con la distancia indicada");
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2; p.add(btn, g);
        g.gridy = 4; p.add(lblRes, g);

        btn.addActionListener(e -> {
            String ori = (String) cbOrigenRuta.getSelectedItem();
            String dst = (String) cbDestinoRuta.getSelectedItem();
            int peso;
            try { peso = Integer.parseInt(fPeso.getText().trim()); }
            catch (Exception ex) { mostrar(lblRes, "ERROR: Distancia invalida."); return; }
            String res = proyecto.agregarRuta(ori != null ? ori : "", dst != null ? dst : "", peso);
            mostrar(lblRes, res);
            if (res.startsWith("OK")) { fPeso.setText(""); refrescar(); }
        });
        return p;
    }

    private JPanel tabBuscar() {
        JPanel p = formPanel();
        GridBagConstraints g = gbc();

        cbOrigenBuscar  = AppTheme.combo(new String[]{});
        cbDestinoBuscar = AppTheme.combo(new String[]{});
        lblResultadoRuta = resultLabel();

        g.gridy = 0; g.gridx = 0; p.add(AppTheme.label("Origen:"),  g); g.gridx = 1; p.add(cbOrigenBuscar, g);
        g.gridy = 1; g.gridx = 0; p.add(AppTheme.label("Destino:"), g); g.gridx = 1; p.add(cbDestinoBuscar, g);

        JButton btn = AppTheme.btnPrimary("Buscar ruta mas corta",
            "Calcular el camino de menor distancia usando el algoritmo de Dijkstra");
        g.gridy = 2; g.gridx = 0; g.gridwidth = 2; p.add(btn, g);
        g.gridy = 3; p.add(lblResultadoRuta, g);

        btn.addActionListener(e -> {
            String ori = (String) cbOrigenBuscar.getSelectedItem();
            String dst = (String) cbDestinoBuscar.getSelectedItem();
            if (ori == null || dst == null) return;
            String res = proyecto.buscarRutaMasCorta(ori, dst);
            lblResultadoRuta.setText("<html>" + res.replace("\n", "<br>") + "</html>");
            lblResultadoRuta.setForeground(res.startsWith("Ruta") ? AppTheme.GREEN : AppTheme.RED);
            resaltarRuta(res);
            canvas.repaint();
        });
        return p;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(AppTheme.BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return p;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        g.gridwidth = 1;
        return g;
    }

    private JLabel resultLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(AppTheme.SMALL);
        l.setForeground(AppTheme.TEXT_MUTED);
        return l;
    }

    private void mostrar(JLabel l, String txt) {
        l.setText(txt);
        l.setForeground(txt.startsWith("OK") ? AppTheme.GREEN : AppTheme.RED);
    }

    public void refrescar() {
        parsearGrafo();
        actualizarCombos();
        canvas.repaint();
    }

    private void parsearGrafo() {
        nombresNodos  = new ArrayList<>();
        aristas       = new ArrayList<>();
        aristasEnRuta = new ArrayList<>();

        String json = proyecto.getJsonGrafo();
        if (json == null || json.trim().isEmpty()) return;

        List<Integer> posNombres = new ArrayList<>();
        int p = 0;
        while (true) {
            int i = json.indexOf("\"nombre\"", p);
            if (i < 0) break;
            posNombres.add(i);
            p = i + 8;
        }

        for (int pos : posNombres) {
            String v = leerValorStr(json, pos);
            if (v != null) nombresNodos.add(v);
        }

        for (int i = 0; i < posNombres.size(); i++) {
            int desde = posNombres.get(i) + 8;
            int hasta = (i + 1 < posNombres.size()) ? posNombres.get(i + 1) : json.length();
            String bloque = json.substring(desde, hasta);

            int rp = 0;
            while (true) {
                int di = bloque.indexOf("\"destino\"", rp);
                if (di < 0) break;
                String dest = leerValorStr(bloque, di);
                double peso = leerValorDouble(bloque, "\"peso\"", di);
                if (dest != null && peso > 0) {
                    int destIdx = nombresNodos.indexOf(dest);
                    if (destIdx >= 0) {
                        aristas.add(new int[]{i, destIdx, (int) peso});
                        aristasEnRuta.add(false);
                    }
                }
                rp = di + 9;
            }
        }
    }

    private String leerValorStr(String json, int desde) {
        int colon = json.indexOf(':', desde);
        if (colon < 0) return null;
        int q1 = json.indexOf('"', colon + 1);
        if (q1 < 0) return null;
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 < 0) return null;
        return json.substring(q1 + 1, q2);
    }

    private double leerValorDouble(String json, String key, int desde) {
        int ki = json.indexOf(key, desde);
        if (ki < 0) return -1;
        int colon = json.indexOf(':', ki + key.length());
        if (colon < 0) return -1;
        int ini = colon + 1;
        while (ini < json.length() && " \n\r\t".indexOf(json.charAt(ini)) >= 0) ini++;
        int fin = ini;
        while (fin < json.length() && (Character.isDigit(json.charAt(fin)) || json.charAt(fin) == '.')) fin++;
        if (fin <= ini) return -1;
        try { return Double.parseDouble(json.substring(ini, fin)); } catch (Exception e) { return -1; }
    }

    private void actualizarCombos() {
        String[] items = nombresNodos.toArray(new String[0]);
        recargarCombo(cbOrigenRuta,    items);
        recargarCombo(cbDestinoRuta,   items);
        recargarCombo(cbOrigenBuscar,  items);
        recargarCombo(cbDestinoBuscar, items);
    }

    private void recargarCombo(JComboBox<String> cb, String[] items) {
        if (cb == null) return;
        cb.removeAllItems();
        for (String s : items) cb.addItem(s);
    }

    private void resaltarRuta(String resultado) {
        for (int i = 0; i < aristasEnRuta.size(); i++) aristasEnRuta.set(i, false);
        if (!resultado.startsWith("Ruta mas corta")) return;
        String[] lineas = resultado.split("\n");
        if (lineas.length < 2) return;
        String[] nods = lineas[1].split(" --> ");
        for (int i = 0; i < nods.length - 1; i++) {
            String ori = nods[i].trim();
            String dst = nods[i + 1].trim();
            int oi = nombresNodos.indexOf(ori);
            int di = nombresNodos.indexOf(dst);
            if (oi < 0 || di < 0) continue;
            for (int j = 0; j < aristas.size(); j++) {
                int[] a = aristas.get(j);
                if (a[0] == oi && a[1] == di) aristasEnRuta.set(j, true);
            }
        }
    }

    private void dibujarGrafo(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = canvas.getWidth(), h = canvas.getHeight();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);

        int n = nombresNodos.size();
        if (n == 0) {
            g2.setColor(AppTheme.TEXT_MUTED);
            g2.setFont(AppTheme.BODY);
            String msg = "No hay localidades. Agregue localidades desde la pestana inferior.";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
            return;
        }

        int cx = w / 2, cy = h / 2;
        int r = Math.min(w, h) / 2 - RADIO_NODO - 30;
        if (r < 40) r = 40;

        int[][] pos = calcularPosiciones(n, cx, cy, r);

        for (int i = 0; i < aristas.size(); i++) {
            int[] a = aristas.get(i);
            boolean enRuta = i < aristasEnRuta.size() && aristasEnRuta.get(i);
            dibujarArista(g2, pos[a[0]], pos[a[1]], a[2], enRuta);
        }

        for (int i = 0; i < n; i++) {
            dibujarNodo(g2, pos[i][0], pos[i][1], nombresNodos.get(i));
        }
    }

    private int[][] calcularPosiciones(int n, int cx, int cy, int r) {
        int[][] pos = new int[n][2];
        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n - Math.PI / 2;
            pos[i][0] = cx + (int) (r * Math.cos(angulo));
            pos[i][1] = cy + (int) (r * Math.sin(angulo));
        }
        return pos;
    }

    private void dibujarNodo(Graphics2D g2, int x, int y, String nombre) {
        g2.setColor(new Color(0, 0, 0, 25));
        g2.fillOval(x - RADIO_NODO + 2, y - RADIO_NODO + 2, RADIO_NODO * 2, RADIO_NODO * 2);
        g2.setColor(AppTheme.SIDEBAR);
        g2.fillOval(x - RADIO_NODO, y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);
        g2.setColor(COLOR_NODO_BORDE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(x - RADIO_NODO, y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(AppTheme.TEXT);
        g2.setFont(AppTheme.SMALL);
        FontMetrics fm = g2.getFontMetrics();
        String label = nombre.length() > 6 ? nombre.substring(0, 5) + "." : nombre;
        g2.drawString(label, x - fm.stringWidth(label) / 2, y + fm.getAscent() / 2 - 1);
    }

    private void dibujarArista(Graphics2D g2, int[] src, int[] dst, int peso, boolean enRuta) {
        double dx = dst[0] - src[0], dy = dst[1] - src[1];
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len < 1) return;
        double nx = dx / len, ny = dy / len;

        int x1 = (int) (src[0] + nx * (RADIO_NODO + 2));
        int y1 = (int) (src[1] + ny * (RADIO_NODO + 2));
        int x2 = (int) (dst[0] - nx * (RADIO_NODO + 4));
        int y2 = (int) (dst[1] - ny * (RADIO_NODO + 4));

        Color color = enRuta ? AppTheme.GREEN : AppTheme.BORDER;
        float grosor = enRuta ? 2.5f : 1.5f;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(grosor));
        g2.drawLine(x1, y1, x2, y2);

        double angulo = Math.atan2(dy, dx);
        int al = 10;
        double aa = Math.PI / 7;
        g2.drawLine(x2, y2,
            (int) (x2 - al * Math.cos(angulo - aa)),
            (int) (y2 - al * Math.sin(angulo - aa)));
        g2.drawLine(x2, y2,
            (int) (x2 - al * Math.cos(angulo + aa)),
            (int) (y2 - al * Math.sin(angulo + aa)));
        g2.setStroke(new BasicStroke(1f));

        int mx = (x1 + x2) / 2, my = (y1 + y2) / 2;
        String pesoStr = String.valueOf(peso);
        g2.setFont(AppTheme.SMALL);
        FontMetrics fm = g2.getFontMetrics();
        int pw = fm.stringWidth(pesoStr) + 4;
        g2.setColor(Color.WHITE);
        g2.fillRect(mx - pw / 2, my - fm.getAscent(), pw, fm.getHeight());
        g2.setColor(enRuta ? AppTheme.GREEN : AppTheme.TEXT_SEC);
        g2.drawString(pesoStr, mx - fm.stringWidth(pesoStr) / 2, my);
    }
}
