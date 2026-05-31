/**
 * Clase encargada de centralizar el acceso a los repositorios
 * de persistencia del sistema.
 * <p>
 * Facilita la interacción con la configuración y los tickets almacenados en
 * archivos JSON.
 * </p>
 */
package com.mycompany.proyectoavance1;

public class Persistence {

    private ConfigRepository configRepository;
    private TicketRepository ticketRepository;
    private AtendidosRepository atendidosRepository;
    private ColaRepository colaRepository;
    private BusesRepository busesRepository;
    private GrafoRepository grafoRepository;

    /**
     * Constructor
     * Inicializa los cinco repositorios con sus archivos correspondientes.
     */
    public Persistence() {
        configRepository = new ConfigRepository("config.json");
        ticketRepository = new TicketRepository("tiquetes.json");
        atendidosRepository = new AtendidosRepository("atendidos.json");
        colaRepository = new ColaRepository("colas.json");
        busesRepository = new BusesRepository("buses.json");
        grafoRepository = new GrafoRepository("grafo.json");
    }

    public ConfigRepository getConfigRepository() {
        return configRepository;
    }

    public TicketRepository getTicketRepository() {
        return ticketRepository;
    }

    public AtendidosRepository getAtendidosRepository() {
        return atendidosRepository;
    }

    public ColaRepository getColasRepository() {
        return colaRepository;
    }
    public BusesRepository getBusesRepository() {
        return busesRepository;
    }
     public GrafoRepository getGrafoRepository() {
        return grafoRepository;
     }
}
