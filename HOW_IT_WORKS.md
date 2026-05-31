# HOW_IT_WORKS.md — Cómo funciona BusNovaTech

> Esto es para vos, que entendés mejor con comparaciones que con definiciones técnicas.
> Sin Javadoc, sin rollos. Todo en español, todo con ejemplos del mundo real.

---

## ¿Qué hace todo el sistema?

Imaginate que sos el encargado de una terminal de buses. Llegan pasajeros, comprás tickets, los asignás a buses, y cuando los atendés, guardás el registro. Eso es BusNovaTech.

El sistema tiene buses, tickets de pasajeros, una fila de espera por cada bus, y un mapa de rutas. Todo se guarda en archivos JSON para que no se pierda al cerrar.

---

## La entrada al sistema: `ProyectoAvance1`

Es como el botón de encendido de una consola. Solo prende todo y pasa el control.

Configura el tema visual (FlatLaf), crea el objeto `Proyecto`, y lanza la pantalla de login. No hace nada más.

---

## El cerebro: `Proyecto.java`

Pensalo como el gerente de la terminal. Cada vez que apretás un botón en la pantalla, le estás pidiendo algo al gerente.

El gerente sabe cómo crear tickets, atender pasajeros, agregar buses, buscar rutas, y guardar todo antes de cerrar. Los paneles de pantalla solo le piden cosas — el gerente decide cómo hacerlas.

---

## Las estructuras de datos

### Lista enlazada — como una cadena de fichas

Una lista enlazada es como una cadena de dominós. Cada ficha sabe cuál es la siguiente, pero no puede saltar al medio directamente — tenés que recorrer desde el principio.

En este proyecto hay tres tipos:
- **`ListaBuses`** — la cadena de todos los buses del sistema
- **`ListaTickets`** — la cadena de tickets (pendientes o atendidos)
- **`ListaUsuarios`** — la cadena de usuarios que pueden hacer login

Cada elemento de la cadena es un "nodo" (`NodoBus`, `NodoTicketRepo`, `NodoUsuario`). El nodo guarda el dato y apunta al siguiente. Nada más.

---

### Cola de prioridad — como la fila del banco

Una cola normal es como la fila del supermercado: el primero que llega es el primero en ser atendido. Una cola de prioridad es como la fila del banco donde los adultos mayores pasan primero, aunque hayan llegado después.

**`ColaPrioridad`** tiene tres filas internas:
- `Preferencial (P)` — los primeros en salir siempre
- `Directo (D)` — salen después de que no queda ningún preferencial
- `Normal (N)` — los últimos en salir

Cada bus tiene su propia `ColaPrioridad`. Cuando llegás a la ventanilla del bus, la cola ya sabe en qué orden atenderte.

---

### Grafo — como Google Maps sin internet

Un grafo es como un mapa de ciudades con caminos entre ellas. Cada ciudad es un "vértice" (`NodoVertices`) y cada camino es una "arista" (`NodoArista`) con una distancia o peso.

**`GrafoBuses`** guarda todas las localidades de la terminal y las rutas entre ellas. Podés agregar localidades nuevas y conectarlas con distancias.

---

### El algoritmo de Dijkstra — el GPS del grafo

Dijkstra es como cuando el GPS calcula la ruta más rápida entre tu casa y el trabajo. Prueba todos los caminos posibles, pero siempre avanza por el más corto primero.

`GrafoBuses.rutaMasCorta()` hace exactamente eso. Usa cuatro arreglos paralelos (nombres, distancias, visitados, predecesores) para calcular el camino óptimo sin usar ninguna estructura de `java.util`.

El resultado es un texto tipo `"Heredia --> San José --> Alajuela"` con el costo total.

---

## Los nodos — las fichas individuales

Cada estructura tiene sus propias "fichas". Una ficha solo guarda un valor y apunta a la siguiente.

Hay seis tipos:
- `NodoBus` — ficha de un bus
- `NodoTicket` — ficha de ticket para la cola de prioridad
- `NodoTicketRepo` — ficha de ticket para las listas de repositorios
- `NodoUsuario` — ficha de usuario con nombre y contraseña
- `NodoVertices` — ficha de una localidad del grafo
- `NodoArista` — ficha de una ruta entre dos localidades

¿Por qué hay dos tipos de ficha para ticket? Porque la cola de prioridad y las listas de repositorios son estructuras distintas con distintas necesidades. `NodoTicketRepo` tiene `setValor()` para poder actualizarlo; `NodoTicket` no lo necesita.

---

## Los tickets — el ciclo de vida de un pasajero

Un ticket pasa por tres estados, como un trámite en una oficina:

1. **Pendiente** — el pasajero acaba de llegar y espera
2. **En Atencion** — el inspector lo está atendiendo ahora mismo
3. **Atendido** (o **No Pagado**) — terminó, pagó o no pagó

El campo `tipoBus` del ticket (`P`, `D`, o `N`) decide a qué bus va. El sistema busca el bus de ese tipo que tenga la cola más corta.

---

## Los buses — cada uno con su inspector

Un bus es como una ventanilla de banco. Tiene:
- Un **inspector** que atiende de a uno a la vez (`ticketEnAtencion`)
- Una **fila de espera** para los que esperan turno (`filaEspera`)
- Un **tipo** que define su prioridad (P, D o N)

Si la ventanilla está libre y no hay nadie en la fila, el pasajero entra directo. Si no, espera.

---

## La persistencia — guardar todo en archivos

La persistencia es como el libro de registros de la terminal. Cada vez que algo cambia, se escribe en un archivo JSON para que no se pierda si cerrás el sistema.

Hay seis archivos, cada uno con su "repositorio":

| Archivo | Qué guarda | Cómo lo guarda |
|---|---|---|
| `config.json` | Nombre de la terminal, cantidad de buses, usuarios | Manual con `JsonUtilSimple` |
| `buses.json` | Todos los buses y su estado | Automático con Gson |
| `tiquetes.json` | Tickets pendientes | Automático con Gson |
| `atendidos.json` | Tickets ya atendidos | Automático con Gson |
| `colas.json` | Instantánea de colas (no se usa activamente) | Automático con Gson |
| `grafo.json` | Las localidades y rutas del mapa | Manual con `JsonUtilSimple` |

**Gson** es como una fotocopiadora automática — toma el objeto Java y lo convierte a JSON sin que vos tengas que hacer nada. **`JsonUtilSimple`** es como escribirlo a mano — más trabajo, pero sin depender de nada externo.

---

## JsonUtilSimple — el analizador de JSON artesanal

Es como buscar palabras en un texto con las manos, en lugar de usar `Ctrl+F`. Tiene métodos para leer un archivo, escribirlo, y extraer valores específicos (`extraerString`, `extraerInt`).

Se usa donde se quiere control total del formato, sin depender de Gson. Es parte del requisito del curso de no usar herramientas externas para las estructuras principales.

---

## La consulta del BCCR — el tipo de cambio

**`ConsultaBCCR`** es como llamar por teléfono al Banco Central de Costa Rica para preguntar cuánto vale el dólar hoy.

Si hay internet, consulta el web service oficial. Si no hay conexión o falla, usa valores predeterminados que vienen en el código (452.59 colones de compra, 458.64 de venta).

La consulta se hace en segundo plano (`SwingWorker`) para que la pantalla no se congele mientras espera la respuesta.

---

## La interfaz gráfica — la cara visible

La interfaz usa Swing con el tema FlatLaf (que hace que no se vea tan anticuado). Hay dos ventanas principales:

### `LoginFrame` — la puerta de entrada

Es la pantalla de usuario y contraseña. Tenés 3 intentos. Si fallás los tres, la ventana se cierra automáticamente después de 2 segundos.

Si es la primera vez que abrís el sistema (no hay `config.json`), aparece el wizard de configuración inicial donde ponés el nombre de la terminal, cuántos buses querés, y el primer usuario administrador.

---

### `MainFrame` — el escritorio principal

Es la ventana principal con una barra lateral izquierda y el contenido a la derecha. Es como las aplicaciones modernas con menú lateral.

Tiene 6 secciones (CardLayout — como una baraja donde solo ves una carta a la vez):

---

### `DashboardPanel` — el tablero de control

Cuatro tarjetas grandes con números: buses activos, tickets en cola, total atendidos, nombre de la terminal. Como el panel de instrumentos de un auto — mirás y en un segundo sabés cómo está todo.

---

### `TicketFormPanel` — crear un ticket nuevo

Formulario para registrar un pasajero: nombre, cédula, edad, moneda (CRC o USD), servicio (VIP, REGULAR, CARGA, EJECUTIVO), y tipo de bus.

Los botones de tipo (P/D/N) cambian de color al seleccionarlos — morado para Preferencial, azul para Directo, verde para Normal.

---

### `BusTablePanel` — gestionar los buses

Una tabla con todos los buses. Cada fila está coloreada según el tipo de bus. Podés seleccionar un bus y apretar "Atender siguiente" para atender al próximo pasajero de ese bus.

También podés agregar buses nuevos o eliminar los que estén vacíos.

---

### `GrafoPanel` — el mapa de rutas

Es la parte más visual del sistema. Muestra el grafo como un círculo de nodos conectados con flechas. Los nodos son las localidades; las flechas son las rutas con su distancia.

Cuando calculás la ruta más corta con Dijkstra, las flechas del camino óptimo se ponen verdes.

Abajo hay tres pestañas: agregar localidad, agregar ruta, y buscar ruta más corta.

---

### `BCCRPanel` — el tipo de cambio

Panel para consultar cuánto vale el dólar según el Banco Central. Podés ingresar tu correo y token del API del BCCR para una consulta en vivo, o simplemente usar los valores predeterminados del sistema.

---

### `AtendidosPanel` — el historial

Tabla con todos los tickets que ya fueron atendidos. Podés filtrar por nombre o tipo de servicio escribiendo en el campo de búsqueda. Las filas verdes son tickets pagados; las naranjas son "No Pagado".

---

## Los scripts de shell — las herramientas del desarrollador

Los scripts son como atajos de teclado para tareas comunes del proyecto.

### `build.sh` — construir el proyecto

Es como apretar "Compilar" en el IDE, pero desde la terminal. Busca Maven automáticamente (primero en el PATH, después en NetBeans), limpia, compila, empaqueta, y genera un reporte de análisis.

```bash
./build.sh          # solo compilar
./build.sh --all    # todo: limpiar, compilar, testear, empaquetar, analizar
./build.sh --report # también busca imports de java.util prohibidos
```

---

### `run.sh` — ejecutar la aplicación

Es como hacer doble clic en el `.jar`, pero desde la terminal. Detecta Maven, compila, y lanza la interfaz gráfica.

```bash
./run.sh
```

---

### `reset-data.sh` — borrar todos los datos

Es como el botón "Restaurar valores de fábrica" de un celular. Vacía todos los archivos JSON excepto `config.json` (que tiene los usuarios y el nombre de la terminal).

Te pide confirmación antes de proceder, para que no lo hagas por accidente.

```bash
./reset-data.sh
```

---

### `backup.sh` — hacer una copia de seguridad

Es como hacer una foto del estado actual del sistema. Copia todos los archivos `.json` a una carpeta con fecha y hora.

```bash
./backup.sh
# crea: backups/backup_2026-05-31_13-07-00/
```

---

### `watch.sh` — espiar los archivos en tiempo real

Es como tener una alarma que suena cada vez que un archivo cambia. Muestra qué archivo cambió, a qué hora, y cuánto pesa ahora.

En Linux usa `inotifywait` para eventos instantáneos. En Windows Git Bash o Mac, hace un chequeo cada 3 segundos (polling).

```bash
./watch.sh
# presioná Ctrl+C para detener
```

---

## ¿Por qué no se usa `java.util`?

Es una restricción del curso CS-304. La idea es que aprendás a construir tus propias estructuras de datos desde cero — sin depender de `ArrayList`, `HashMap`, `LinkedList`, etc.

Implementar las cosas vos mismo te obliga a entender cómo funcionan por dentro: cómo se encadenan los nodos, cómo se maneja la memoria, cómo implementar Dijkstra sin un `PriorityQueue` listo.

El script `build.sh --report` escanea todos los archivos `.java` buscando `import java.util.` y te avisa si encontró algo.

---

## Resumen en una línea por concepto

| Concepto | Analogía |
|---|---|
| Lista enlazada | Cadena de dominós donde cada ficha apunta a la siguiente |
| Cola de prioridad | Fila del banco donde los adultos mayores pasan primero |
| Grafo | Mapa de ciudades con caminos y distancias |
| Dijkstra | GPS que calcula la ruta más corta |
| Nodo | La ficha individual de cualquier estructura |
| Repositorio | El libro de registros que guarda todo en un archivo |
| Gson | Fotocopiadora automática que convierte objetos a JSON |
| JsonUtilSimple | Escribir el JSON a mano, carácter por carácter |
| BCCR | Llamada al banco para preguntar cuánto vale el dólar |
| `Proyecto.java` | El gerente que recibe todas las órdenes |
| `LoginFrame` | La puerta de entrada con contraseña |
| `MainFrame` | El escritorio con menú lateral |
| `build.sh` | El botón de compilar desde la terminal |
| `reset-data.sh` | Restaurar valores de fábrica |
| `backup.sh` | Sacar una foto del estado actual |
| `watch.sh` | Alarma que suena cuando cambia un archivo |
