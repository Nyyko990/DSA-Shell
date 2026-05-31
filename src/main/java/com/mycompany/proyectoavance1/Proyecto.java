package com.mycompany.proyectoavance1;

import com.mycompany.proyectoavance1.ui.LoginFrame;
import java.util.ArrayList;

// Controlador de lógica del sistema — sin dependencia de UI directa
public class Proyecto {

    private Configuracion config;
    private ListaBuses listaBuses;
    private Persistence persistence;
    private GrafoBuses grafo;

    public Proyecto() {
        listaBuses = new ListaBuses();
        persistence = new Persistence();
        config = null;
        grafo = null;
    }

    // Carga datos desde disco y lanza LoginFrame en el EDT
    public void iniciar() {
        config = persistence.getConfigRepository().cargar();
        if (config == null) {
            config = new Configuracion();
        }
        inicializarBuses();
        grafo = persistence.getGrafoRepository().cargar();
        if (grafo == null) {
            grafo = new GrafoBuses();
        }
        // Ya estamos en el EDT (invocado desde ProyectoAvance1 vía SwingUtilities.invokeLater)
        new LoginFrame(this).setVisible(true);
    }

    // true = no hay config válida guardada → debe mostrarse el wizard de setup
    public boolean requiereConfiguracion() {
        return !config.tieneConfigValida();
    }

    // Persiste configuración inicial proporcionada por el wizard de setup
    public void configurarSistema(String nombreTerminal, int totalBuses, String usuario, String contrasena) {
        config.setNombreTerminal(nombreTerminal);
        config.setCantidadBuses(totalBuses);
        config.agregarUsuario(usuario, contrasena);
        persistence.getConfigRepository().guardar(config);
        inicializarBuses();
    }

    public boolean intentarLogin(String usuario, String contrasena) {
        return config.validarLogin(usuario, contrasena);
    }

    public String getNombreTerminal() {
        return config != null ? config.getNombreTerminal() : "BusNovaTech";
    }

    public int getCantidadBuses() {
        return config != null ? config.getCantidadBuses() : 0;
    }

    private void inicializarBuses() {
        ListaBuses guardados = persistence.getBusesRepository().cargarBuses();
        if (guardados != null && !guardados.estaVacia()) {
            listaBuses = guardados;
            return;
        }
        listaBuses = new ListaBuses();
        int n = 1;
        while (n <= config.getCantidadBuses()) {
            char tipo = 'N';
            if (n == 1) tipo = 'P';
            else if (n == 2) tipo = 'D';
            listaBuses.agregarBus(new Bus(n, tipo));
            n++;
        }
        persistence.getBusesRepository().guardarBuses(listaBuses);
    }

    private Bus buscarBusDisponiblePorTipo(char tipo) {
        Bus mejor = null;
        int menorCola = -1;
        int pos = 0;
        while (pos < listaBuses.tamano()) {
            Bus bus = listaBuses.obtenerBusEnPosicion(pos);
            if (bus != null && bus.getTipoBus() == tipo) {
                if (bus.estaLibre() && bus.getFilaEspera().estaVacia()) return bus;
                int cola = bus.cantidadEnFila();
                if (mejor == null || cola < menorCola) {
                    mejor = bus;
                    menorCola = cola;
                }
            }
            pos++;
        }
        return mejor;
    }

    // Retorna "OK:..." o "ERROR:..." para que el panel muestre el resultado
    public String crearTicket(String nombre, int id, int edad, String moneda, String servicio, char tipo) {
        Ticket ticket = Ticket.crearNuevo(nombre, id, edad, moneda, servicio, tipo);
        ticket.setTerminalCompra(config.getNombreTerminal());
        Bus bus = buscarBusDisponiblePorTipo(tipo);
        if (bus == null) return "ERROR: No hay bus tipo " + tipo + " disponible.";
        if (bus.estaLibre() && bus.getFilaEspera().estaVacia()) {
            bus.asignarAtencionDirecta(ticket);
        } else {
            bus.agregarAFila(ticket);
        }
        persistence.getTicketRepository().agregarTicket(ticket);
        persistence.getBusesRepository().guardarBuses(listaBuses);
        return "OK: Bus #" + bus.getNumeroBus() + " asignado.\n" + ticket.resumen();
    }

    public double obtenerMontoPorServicio(String servicio) {
        if (servicio == null) return 0;
        if (servicio.equals("VIP"))      return 5000;
        if (servicio.equals("REGULAR"))  return 3000;
        if (servicio.equals("CARGA"))    return 7000;
        if (servicio.equals("EJECUTIVO"))return 6000;
        return 0;
    }

    // Peek del siguiente ticket (sin modificar estado): ticket actual o primero en cola
    public Ticket getProximoTicket(int numeroBus) {
        Bus bus = listaBuses.buscarBusPorNumero(numeroBus);
        if (bus == null) return null;
        Ticket t = bus.getTicketEnAtencion();
        if (t != null) return t;
        if (!bus.getFilaEspera().estaVacia()) return bus.getFilaEspera().obtener(0);
        return null;
    }

    // Atiende el siguiente ticket del bus; retorna "OK:nombre|monto|pagado" o "ERROR:..."
    public String atenderSiguiente(int numeroBus, boolean pagado) {
        Bus bus = listaBuses.buscarBusPorNumero(numeroBus);
        if (bus == null) return "ERROR: No existe bus #" + numeroBus + ".";
        Ticket ticket = bus.getTicketEnAtencion();
        if (ticket == null) {
            if (bus.getFilaEspera().estaVacia()) return "ERROR: Bus #" + numeroBus + " sin tickets.";
            ticket = bus.getFilaEspera().sacarPrimero();
            bus.asignarAtencionDirecta(ticket);
            ticket = bus.getTicketEnAtencion();
        }
        double monto = obtenerMontoPorServicio(ticket.getServicio());
        ticket.marcarAtencion(config.getNombreTerminal(), numeroBus, monto, pagado);
        persistence.getTicketRepository().actualizarTicket(ticket);
        if (pagado) persistence.getAtendidosRepository().agregarAtendido(ticket);
        bus.finalizarAtencion();
        persistence.getBusesRepository().guardarBuses(listaBuses);
        return "OK:" + ticket.getNombre() + "|" + monto + "|" + pagado;
    }

    public ListaBuses getListaBuses() { return listaBuses; }

    public ListaTickets getAtendidos() {
        return persistence.getAtendidosRepository().obtenerAtendidos();
    }

    public GrafoBuses getGrafo() { return grafo; }

    public Configuracion getConfig() { return config; }

    public int getBusesActivos() {
        if (listaBuses == null) return 0;
        int activos = 0;
        int pos = 0;
        while (pos < listaBuses.tamano()) {
            Bus bus = listaBuses.obtenerBusEnPosicion(pos);
            if (bus != null && !bus.estaCompletamenteVacio()) activos++;
            pos++;
        }
        return activos;
    }

    public int getTotalTicketsPendientes() {
        if (listaBuses == null) return 0;
        int total = 0;
        int pos = 0;
        while (pos < listaBuses.tamano()) {
            Bus bus = listaBuses.obtenerBusEnPosicion(pos);
            if (bus != null) {
                if (bus.getTicketEnAtencion() != null) total++;
                total += bus.cantidadEnFila();
            }
            pos++;
        }
        return total;
    }

    public int getTotalAtendidos() {
        ListaTickets lista = getAtendidos();
        return lista != null ? lista.tamano() : 0;
    }

    public String agregarUsuario(String usuario, String contrasena) {
        if (config.existeUsuario(usuario)) return "ERROR: El usuario ya existe.";
        config.agregarUsuario(usuario, contrasena);
        persistence.getConfigRepository().guardar(config);
        return "OK: Usuario '" + usuario + "' agregado.";
    }

    public String agregarBuses(int cantidad) {
        int base = listaBuses.obtenerNumeroMayorBus();
        int i = 1;
        while (i <= cantidad) {
            int num = base + i;
            char tipo = 'N';
            if (num == 1) tipo = 'P';
            else if (num == 2) tipo = 'D';
            listaBuses.agregarBus(new Bus(num, tipo));
            i++;
        }
        config.setCantidadBuses(config.getCantidadBuses() + cantidad);
        persistence.getConfigRepository().guardar(config);
        persistence.getBusesRepository().guardarBuses(listaBuses);
        return "OK: " + cantidad + " bus(es) agregado(s). Total: " + config.getCantidadBuses();
    }

    public String eliminarBuses(int cantidad) {
        if (config.getCantidadBuses() <= 3) return "ERROR: Mínimo 3 buses requeridos.";
        int eliminados = 0;
        while (eliminados < cantidad) {
            Bus ultimo = listaBuses.obtenerUltimoBus();
            if (ultimo == null) break;
            if (!ultimo.estaCompletamenteVacio()) {
                return "PARCIAL:" + eliminados + " eliminado(s). Bus #" + ultimo.getNumeroBus() + " tiene tickets activos.";
            }
            if (!listaBuses.eliminarUltimoBus()) break;
            eliminados++;
        }
        config.setCantidadBuses(config.getCantidadBuses() - eliminados);
        persistence.getConfigRepository().guardar(config);
        persistence.getBusesRepository().guardarBuses(listaBuses);
        return "OK: " + eliminados + " bus(es) eliminado(s). Total: " + config.getCantidadBuses();
    }

    public String agregarLocalidad(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return "ERROR: Nombre vacío.";
        if (grafo.existeLocalidad(nombre)) return "ERROR: La localidad ya existe.";
        grafo.agregarLocalidad(nombre);
        persistence.getGrafoRepository().guardar(grafo);
        return "OK: Localidad '" + nombre + "' agregada.";
    }

    public String agregarRuta(String origen, String destino, int peso) {
        if (!grafo.existeLocalidad(origen)) return "ERROR: Origen '" + origen + "' no existe.";
        if (!grafo.existeLocalidad(destino)) return "ERROR: Destino '" + destino + "' no existe.";
        if (origen.equalsIgnoreCase(destino)) return "ERROR: Origen y destino son iguales.";
        grafo.agregarRuta(origen, destino, peso);
        persistence.getGrafoRepository().guardar(grafo);
        return "OK: Ruta " + origen + " → " + destino + " (peso: " + peso + ") agregada.";
    }

    public String buscarRutaMasCorta(String origen, String destino) {
        if (grafo == null || grafo.estaVacio()) return "ERROR: El grafo está vacío.";
        if (!grafo.existeLocalidad(origen)) return "ERROR: Origen no existe.";
        if (!grafo.existeLocalidad(destino)) return "ERROR: Destino no existe.";
        return grafo.rutaMasCorta(origen, destino);
    }

    public String getJsonGrafo() {
        return grafo != null ? grafo.aJson() : "{\"localidades\":[]}";
    }

    // Retorna array de nombres de localidades (para ComboBoxes del grafo)
    public String[] getArrayLocalidades() {
        if (grafo == null || grafo.estaVacio()) return new String[0];
        String lista = grafo.listarLocalidades();
        if (lista.equals("(ninguna)") || lista.trim().isEmpty()) return new String[0];
        String[] lineas = lista.split("\n");
        ArrayList<String> nombres = new ArrayList<>();
        for (String linea : lineas) {
            String n = linea.trim();
            if (n.startsWith("- ")) n = n.substring(2);
            if (!n.isEmpty()) nombres.add(n);
        }
        return nombres.toArray(new String[0]);
    }

    // Consulta de tipo de cambio (puede ser lenta por red — usar SwingWorker en el panel)
    public String consultarBCCREnLinea(String correo, String token) {
        return new ConsultaBCCR().consultarEnLinea(correo, token);
    }

    public String consultarBCCRPredeterminado() {
        return new ConsultaBCCR().consultarPredeterminado();
    }

    // Persiste todo y libera recursos antes de salir
    public void salir() {
        if (config != null) persistence.getConfigRepository().guardar(config);
        persistence.getTicketRepository().guardarListaCompleta();
        persistence.getAtendidosRepository().guardarListaCompleta();
        if (listaBuses != null) persistence.getBusesRepository().guardarBuses(listaBuses);
        if (grafo != null) persistence.getGrafoRepository().guardar(grafo);
    }
}
