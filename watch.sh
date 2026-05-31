#!/usr/bin/env bash
# =============================================================================
# BusNovaTech — Monitor de cambios en archivos JSON en tiempo real
# Autor: Nyyko
# Uso:   ./watch.sh
#   Requiere inotifywait (Linux) o cae a modo polling cada 3s (Windows/Mac)
# =============================================================================

# Sin set -e: queremos sobrevivir Ctrl+C y errores en el loop de polling
set -uo pipefail

# ── Colores ──────────────────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# ── Config ───────────────────────────────────────────────────────────────────
PROJECT_NAME="BusNovaTech"
POLL_INTERVAL=3     # segundos entre verificaciones en modo polling

# ── Helpers (sin log a archivo — watch es interactivo) ───────────────────────
info()    { echo -e "${CYAN}[INFO]${RESET}    $1"; }
change()  { echo -e "${GREEN}[CAMBIO]${RESET}  $1"; }
warn()    { echo -e "${YELLOW}[WARN]${RESET}    $1"; }
error()   { echo -e "${RED}[FAIL]${RESET}    $1"; }
header()  { echo -e "\n${BOLD}${CYAN}════════════════════════════════════════${RESET}"; \
            echo -e "${BOLD}  $1${RESET}"; \
            echo -e "${BOLD}${CYAN}════════════════════════════════════════${RESET}"; }

# ── Limpieza al recibir Ctrl+C ────────────────────────────────────────────────
_tmpdir=""
cleanup() {
    echo ""
    info "Monitor detenido. Hasta luego."
    [[ -n "$_tmpdir" && -d "$_tmpdir" ]] && rm -rf "$_tmpdir"   # eliminar snapshots temporales
    exit 0
}
trap cleanup INT TERM

# ── Modo inotifywait: evento por evento, sin polling ─────────────────────────
watch_inotify() {
    info "Usando ${BOLD}inotifywait${RESET} — monitoreo en tiempo real."
    info "Presioná Ctrl+C para detener."
    echo ""

    inotifywait -m -e modify,create,delete \
        --format '%T %w%f %e' \
        --timefmt '%H:%M:%S' \
        ./*.json 2>/dev/null \
    | while IFS=' ' read -r ts file event; do
        local size="N/A"
        [[ -f "$file" ]] && size=$(du -sh "$file" | cut -f1)
        change "[${ts}] ${BOLD}${file}${RESET} — ${event} | tamaño: ${size}"
    done
}

# ── Modo polling: compara tamaño en bytes cada POLL_INTERVAL segundos ─────────
# Usa archivos temporales en lugar de arrays asociativos (compatible bash 3.2+)
watch_poll() {
    warn "inotifywait no disponible — usando polling cada ${POLL_INTERVAL}s (Git Bash / Mac)."
    info "Presioná Ctrl+C para detener."
    echo ""

    # Directorio temporal para guardar el tamaño anterior de cada archivo
    _tmpdir=$(mktemp -d)

    # Snapshot inicial de todos los .json presentes
    for f in *.json; do
        [[ -f "$f" ]] || continue
        wc -c < "$f" | tr -d ' ' > "${_tmpdir}/${f}.size"   # guardar bytes sin espacios
    done

    while true; do
        sleep "$POLL_INTERVAL"

        # Verificar archivos existentes por cambios de tamaño
        for f in *.json; do
            [[ -f "$f" ]] || continue

            local curr prev snap
            curr=$(wc -c < "$f" | tr -d ' ')
            snap="${_tmpdir}/${f}.size"

            if [[ -f "$snap" ]]; then
                prev=$(cat "$snap")
            else
                prev="-1"   # archivo nuevo: no existía antes
            fi

            if [[ "$curr" != "$prev" ]]; then
                local ts human
                ts=$(date '+%H:%M:%S')
                human=$(du -sh "$f" | cut -f1)

                if [[ "$prev" == "-1" ]]; then
                    change "[${ts}] ${BOLD}${f}${RESET} — archivo nuevo | tamaño: ${human}"
                else
                    change "[${ts}] ${BOLD}${f}${RESET} — modificado | ${prev}B → ${curr}B (${human})"
                fi

                echo "$curr" > "$snap"   # actualizar snapshot
            fi
        done
    done
}

main() {
    header "${PROJECT_NAME} — Monitor de Archivos JSON"

    # Verificar raíz del proyecto
    if [[ ! -f "pom.xml" ]]; then
        error "pom.xml no encontrado. ¿Estás en la raíz del proyecto?"
        exit 1
    fi

    # Mostrar archivos que serán monitoreados
    info "Archivos monitoreados:"
    for f in *.json; do
        [[ -f "$f" ]] || { warn "  (ningún .json encontrado aún)"; break; }
        local sz
        sz=$(du -sh "$f" | cut -f1)
        info "  • ${f}  (${sz})"
    done
    echo ""

    # Elegir modo según disponibilidad de inotifywait
    if command -v inotifywait &>/dev/null; then
        watch_inotify
    else
        watch_poll
    fi
}

main "$@"
