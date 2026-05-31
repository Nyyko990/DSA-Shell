# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**BusNovaTech** — a Java 21 terminal/bus-ticketing system for Costa Rica. All data structures (linked lists, priority queue, graph) are implemented from scratch; `java.util` collections are intentionally forbidden (CS-304 course requirement). The only external dependency is Gson 2.10.1 (JSON serialization).

## Build & Run

```bash
# Build and package (requires Maven 3.x + JDK 21)
mvn clean package -q

# Run the application
java -jar target/ProyectoBusNovaTech-1.0-SNAPSHOT.jar

# Or via Maven exec
mvn exec:java

# Full build script (pre-flight checks, compile, package, code analysis report)
bash build.sh
```

There are no automated tests in this project. The build script runs static analysis and generates a report but does not execute test suites.

Entry point: `com.mycompany.proyectoavance1.ProyectoAvance1` (configured in `pom.xml` as `exec.mainClass`).

## Architecture

### Layer overview

```
ProyectoAvance1 (main)
    └── Proyecto          ← orchestrator: menus, business logic, all user interaction
         ├── Data structures: ListaBuses, ColaPrioridad, ListaTickets, ListaUsuarios, GrafoBuses
         ├── Models: Bus, Ticket, Configuracion
         ├── Persistence   ← facade over 6 repositories
         │    ├── ConfigRepository   → config.json     (JsonUtilSimple)
         │    ├── TicketRepository   → tiquetes.json   (Gson)
         │    ├── AtendidosRepository→ atendidos.json  (Gson)
         │    ├── BusesRepository    → buses.json      (Gson)
         │    ├── GrafoRepository    → grafo.json      (JsonUtilSimple)
         │    └── ColaRepository     → colas.json      (Gson)
         ├── ConsultaBCCR  ← live USD/CRC exchange rates from Costa Rican Central Bank API
         └── InputJOP      ← all user I/O via Swing JOptionPane dialogs
```

### Custom data structures

| Class | Type | Used for |
|---|---|---|
| `ColaPrioridad` | Priority queue (3 internal linked lists: P/D/N) | Ticket assignment per bus type |
| `ListaBuses` | Singly linked list | Bus roster |
| `ListaTickets` | Singly linked list | Pending and served tickets |
| `ListaUsuarios` | Singly linked list | User authentication |
| `GrafoBuses` | Directed weighted graph | Route/locality management; includes Dijkstra's algorithm |

Node classes (`NodoBus`, `NodoTicket`, `NodoTicketRepo`, `NodoUsuario`, `NodoVertices`, `NodoArista`) are simple wrappers with a `siguiente`/`next` pointer — never use `java.util` node types.

### JSON strategy

Two parsers coexist deliberately:
- **Gson**: used for complex/nested structures (`Ticket`, `Bus`, queues).
- **JsonUtilSimple**: hand-rolled parser used for `config.json` and `grafo.json` to avoid external-lib dependency where manual parsing is feasible. Provides `leerArchivo`, `escribirArchivo`, `extraerString`, `extraerInt`, `escape`, `unescape`.

### Bus types and ticket priority

Buses are typed `P` (Preferential), `D` (Direct), or `N` (Normal). `ColaPrioridad` dequeues in P → D → N order. A ticket's `tipoBus` field determines which bus it routes to. `Proyecto` contains the routing logic.

### Persistence model

All repositories keep an in-memory cache and flush to disk on every mutating operation. `Persistence.java` is the single access point — never instantiate repositories directly from `Proyecto`.

### UI model

All interaction is through `InputJOP` (Swing `JOptionPane`). There is no console readline-style input. Range validation, text validation, and option matching are handled in `InputJOP` methods (`leerTextoNoVacio`, `leerEnteroRango`, `leerOpcionTexto`).

## Runtime data files

`config.json`, `buses.json`, `tiquetes.json`, `atendidos.json`, `colas.json`, `grafo.json` — all live in the project root. On first run with no `config.json`, `Proyecto` prompts the admin to configure the system from scratch.
