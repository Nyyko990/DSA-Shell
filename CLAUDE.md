# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DSA-Shell is a Java 21 bus terminal management system (BusNovaTech) built for CS-304 (Data Structures & Algorithms) at Universidad Fidelitas, Costa Rica. The name reflects two pillars:
- **DSA**: All data structures implemented from scratch — `java.util` collections are forbidden in core logic
- **Shell**: Lifecycle automation via five Bash scripts

## Branches

- `OGmain` — original unmodified course submission (no Swing UI, minimal scripts)
- `OPmain` — adds full Swing UI (`ui/` package)
- `SHmain` — based on OPmain; adds all 5 shell scripts — **this is the current branch**

## Build & Run Commands

### Shell scripts (preferred)
```bash
./build.sh              # Clean + compile (default)
./build.sh --test       # Compile + run tests
./build.sh --report     # Compile + code analysis (java.util violations, LOC, TODOs)
./build.sh --all        # Full pipeline: clean, compile, test, package, analysis
./run.sh                # Auto-detect Maven, then: mvn clean compile exec:java
```

`build.sh` and `run.sh` auto-detect Maven — checks PATH, `M2_HOME`/`MAVEN_HOME`, then NetBeans bundled Maven at `/c/Program Files/Apache NetBeans/java/maven/bin/mvn`.

### Other scripts
```bash
./reset-data.sh   # Reset buses/tickets/queues/graph JSON to empty (keeps config.json)
./backup.sh       # Copy all *.json to backups/backup_YYYY-MM-DD_HH-MM-SS/
./watch.sh        # Monitor JSON files for changes (inotifywait or 3s polling fallback)
```

### Direct Maven
```bash
mvn compile
mvn package -DskipTests
mvn exec:java
mvn test -Dtest=ClassName#methodName   # no tests exist yet in this branch
```

Build artifacts: `target/` | Logs: `logs/` | Reports: `reports/` | Backups: `backups/`

## Critical Constraint

**Never import `java.util.*` in core logic.** All collections use hand-rolled implementations. `build.sh --report` flags violations automatically.

**Allowed `java.util` uses** (UI bridge only, not DSA):
- `Proyecto.getArrayLocalidades()` — `ArrayList` to convert graph node names to `String[]` for Swing `JComboBox`
- `GrafoPanel.java` — `ArrayList`/`List` for internal canvas rendering state
- `AtendidosPanel.java` — `ArrayList`/`List` for in-memory text filter cache

## Architecture

**Entry point**: `ProyectoAvance1.java` — configures FlatLaf theme, creates `Proyecto`, calls `proyecto.iniciar()` on the Swing EDT.

**Controller**: `Proyecto.java` — single orchestrator. Every UI action calls a method here. Returns `"OK:..."` or `"ERROR:..."` strings to panels so they can show colored feedback without knowing business logic.

**Startup flow**: `ProyectoAvance1` → `Proyecto.iniciar()` → loads JSON → `LoginFrame` (setup wizard if no config) → `MainFrame` → 6 panels via `CardLayout`.

### All Classes

#### Entry & Controller
| Class | Role |
|---|---|
| `ProyectoAvance1` | `main()` — FlatLaf setup, launch `Proyecto` on EDT |
| `Proyecto` | Ticket lifecycle, bus management, graph ops, BCCR queries, persistence flush on exit |

#### Domain Models
| Class | Role |
|---|---|
| `Bus` | One bus: number, type (P/D/N), `inspectorOcupado`, `ticketEnAtencion`, `filaEspera` (ListaTickets) |
| `Ticket` | Passenger ticket: name, ID, age, currency, service type, bus type, timestamps, payment state |
| `Configuracion` | Terminal name, bus count, `ListaUsuarios`; custom `toJSON()`/`fromJSON()` via `JsonUtilSimple` |

#### Data Structures (custom — no java.util)
| Class | Role |
|---|---|
| `ListaBuses` | Singly linked list of `Bus` using `NodoBus` |
| `ListaTickets` | Singly linked list of `Ticket` using `NodoTicketRepo` — used by bus queues and repositories |
| `ListaUsuarios` | Singly linked list of username/password pairs using `NodoUsuario` |
| `ColaPrioridad` | Three internal linked-list queues (P/D/N) using `NodoTicket`; `desencolar()` drains P → D → N |
| `GrafoBuses` | Directed weighted graph via adjacency lists (`NodoVertices`+`NodoArista`); Dijkstra with primitive arrays |

**Important**: Two node types for tickets — `NodoTicket` is used only inside `ColaPrioridad`; `NodoTicketRepo` is used by `ListaTickets`. They are not interchangeable.

#### Node Classes
| Class | Role |
|---|---|
| `NodoBus` | Wraps `Bus` + `siguiente` pointer |
| `NodoTicket` | Wraps `Ticket` — used exclusively by `ColaPrioridad` |
| `NodoTicketRepo` | Wraps `Ticket` with mutable `setValor()` — used by `ListaTickets` and repositories |
| `NodoUsuario` | Wraps username + password + `siguiente` |
| `NodoVertices` | Graph vertex: name + `primeraArista` (adjacency list head) + `siguiente` vertex |
| `NodoArista` | Graph edge: destination name + weight + `siguiente` edge |

#### Persistence
| Class | Role |
|---|---|
| `Persistence` | Facade — instantiates and exposes all 6 repositories |
| `ConfigRepository` | `config.json` via `JsonUtilSimple` + `Configuracion.fromJSON()`/`toJSON()` |
| `BusesRepository` | `buses.json` via **Gson** |
| `TicketRepository` | `tiquetes.json` via **Gson**; `actualizarTicket()` matches by ID + purchase timestamp |
| `AtendidosRepository` | `atendidos.json` via **Gson** |
| `ColaRepository` | `colas.json` via **Gson** — instantiated but never called by `Proyecto` (placeholder) |
| `GrafoRepository` | `grafo.json` via `JsonUtilSimple` + `GrafoBuses.aJson()`/`desdeJson()` |

All repositories cache in memory and flush on every mutation. `Proyecto.salir()` does a final flush of everything before `System.exit(0)`.

#### Utilities & External
| Class | Role |
|---|---|
| `JsonUtilSimple` | Manual JSON parser: `leerArchivo`, `escribirArchivo`, `extraerString`, `extraerInt`, `escape`, `unescape` |
| `ConsultaBCCR` | HTTP GET to BCCR web service (indicadores 317=compra, 318=venta); fallback: 452.59/458.64 CRC per USD |
| `InputJOP` | Legacy `JOptionPane` input helper (range, text, options) — not used by the current Swing UI |

### UI Package (`ui/`)
| Class | Role |
|---|---|
| `AppTheme` | Color constants (green/orange accent), font constants, factory methods (`btnPrimary`, `btnSecondary`, `field`, `combo`, `panelEncabezado`, `crearInfoPanel`, `busTipo`, `filaBus`) |
| `LoginFrame` | 460×420 login dialog; 3 attempts → auto-close timer; shows setup wizard panel if `Proyecto.requiereConfiguracion()` |
| `MainFrame` | 1180×720 main window; 200px sidebar (BoxLayout) + `CardLayout` content; 6 nav items; `mostrar()` calls `refrescar()` on dashboard, buses, atendidos, rutas |
| `DashboardPanel` | 4 metric cards: buses activos, tickets en cola, total atendidos, nombre terminal |
| `TicketFormPanel` | Form with toggle buttons (P/D/N type), dropdowns for moneda (CRC/USD) and servicio (VIP/REGULAR/CARGA/EJECUTIVO) |
| `BusTablePanel` | `JTable` color-coded by bus type (purple=P, blue=D, green=N); "Atender siguiente" + pagado checkbox; add/remove buses |
| `GrafoPanel` | `JSplitPane`: custom `Graphics2D` canvas (circular layout, directed arrows, weight labels, Dijkstra path highlighted green) + tabbed forms below |
| `BCCRPanel` | Exchange rate lookup; online query runs in `SwingWorker` to avoid blocking EDT |
| `AtendidosPanel` | `JTable` with live text filter (name or service); green rows = Atendido, orange = No Pagado |

## Ticket Lifecycle

`Pendiente` → `En Atencion` → `Atendido` (or `No Pagado`)

1. `Proyecto.crearTicket()` finds best bus via `buscarBusDisponiblePorTipo()` — prefers free bus with empty queue, else bus with shortest queue
2. Free bus + empty queue → `bus.asignarAtencionDirecta(ticket)` sets `inspectorOcupado=true`
3. Otherwise → `bus.agregarAFila(ticket)` appends to `bus.filaEspera` (`ListaTickets`)
4. `Proyecto.atenderSiguiente(numeroBus, pagado)` — dequeues next ticket if inspector is free, calls `ticket.marcarAtencion()`, persists to `atendidos.json` if paid
5. `bus.finalizarAtencion()` clears `ticketEnAtencion` and `inspectorOcupado`
6. `Proyecto.salir()` flushes all repositories before exit

**Service prices** (in `obtenerMontoPorServicio()`): VIP=5000, REGULAR=3000, CARGA=7000, EJECUTIVO=6000 CRC

**Bus minimum**: `eliminarBuses()` enforces ≥3 buses; refuses to delete a bus with active tickets.

## Dijkstra in GrafoBuses

`rutaMasCorta()` allocates four parallel primitive arrays at call time:
- `String[] nombres`, `double[] distancia` (init 99999), `boolean[] visitado`, `int[] predecesor`

Path reconstruction uses a manual stack `int[] pila` iterated in reverse. `GrafoPanel` parses the `" --> "` separator in the result string to highlight edges green on the canvas.

## JSON Files (project root)

| File | Serializer | Empty reset value |
|---|---|---|
| `config.json` | JsonUtilSimple | (keep — has users) |
| `buses.json` | Gson | `{}` |
| `tiquetes.json` | Gson | `{}` |
| `atendidos.json` | Gson | `{}` |
| `colas.json` | Gson | `{}` |
| `grafo.json` | JsonUtilSimple | `{"localidades":[]}` |
