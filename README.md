# DSA-Shell

Java-based bus terminal management system built from scratch using custom Data Structures & Algorithms. Features shell automation scripts for build, testing and code analysis. Includes a GUI-enhanced branch with Swing dashboards.

## Shell Scripts

All scripts live at the project root, require Bash (Git Bash on Windows), and use color output.

### `build.sh` — Build & test automation

```bash
./build.sh              # Clean + compile (default)
./build.sh --test       # Compile + run tests
./build.sh --report     # Compile + code analysis (java.util violations, LOC count)
./build.sh --all        # Full pipeline: clean, compile, test, package, analysis
./build.sh --clean      # Clean build artifacts only
```

Detects Maven automatically — checks PATH, `M2_HOME`/`MAVEN_HOME`, and NetBeans bundled Maven. Logs go to `logs/build_TIMESTAMP.log`, reports to `reports/report_TIMESTAMP.txt`.

---

### `run.sh` — Compile and launch the app

```bash
./run.sh
```

Detects Maven automatically (same logic as `build.sh`), then runs `mvn clean compile exec:java` to launch the Swing UI. Prints clear instructions if Maven is not found. Logs to `logs/run_TIMESTAMP.log`.

---

### `reset-data.sh` — Reset all JSON data to empty state

```bash
./reset-data.sh
```

Asks for confirmation (`[s/N]`) before proceeding. Resets:

| File | Reset value |
|------|-------------|
| `buses.json` | `{}` |
| `tiquetes.json` | `{}` |
| `colas.json` | `{}` |
| `atendidos.json` | `{}` |
| `grafo.json` | `{"localidades":[]}` |

`config.json` (terminal name, user accounts) is **never touched**. The reset is logged with a timestamp to `logs/reset.log`.

---

### `backup.sh` — Timestamped backup of all JSON files

```bash
./backup.sh
```

Copies every `*.json` in the project root to `backups/backup_YYYY-MM-DD_HH-MM-SS/`. Prints a summary of files backed up and total size. Appends an entry to `logs/backup.log`.

Example output directory: `backups/backup_2026-05-31_13-07-03/`

---

### `watch.sh` — Monitor JSON files for changes

```bash
./watch.sh
```

Shows which file changed, when, and its new size. Press `Ctrl+C` to stop.

- **Linux**: uses `inotifywait` for real-time event-based monitoring (install with `sudo apt install inotify-tools`).
- **Windows Git Bash / macOS**: falls back to polling every 3 seconds automatically.

---

## Build output

| Path | Contents |
|------|----------|
| `target/` | Compiled classes and JAR |
| `logs/` | Build, run, reset, and backup logs |
| `reports/` | Code-analysis reports |
| `backups/` | Timestamped JSON backups |
