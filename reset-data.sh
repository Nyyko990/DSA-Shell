#!/usr/bin/env bash
# =============================================================================
# BusNovaTech — Reseteo de datos JSON al estado vacío inicial
# Autor: Nyyko
# Uso:   ./reset-data.sh
# =============================================================================

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
LOG_DIR="logs"
TIMESTAMP=$(date '+%Y-%m-%d_%H-%M-%S')
LOG_FILE="${LOG_DIR}/reset.log"

# ── Helpers ──────────────────────────────────────────────────────────────────
log()     { echo -e "$1" | tee -a "$LOG_FILE"; }
info()    { log "${CYAN}[INFO]${RESET}  $1"; }
success() { log "${GREEN}[OK]${RESET}    $1"; }
warn()    { log "${YELLOW}[WARN]${RESET}  $1"; }
error()   { log "${RED}[FAIL]${RESET}  $1"; }
header()  { log "\n${BOLD}${CYAN}════════════════════════════════════════${RESET}"; log "${BOLD}  $1${RESET}"; log "${BOLD}${CYAN}════════════════════════════════════════${RESET}"; }

# ── Escribe contenido vacío en un archivo JSON ────────────────────────────────
write_empty() {
    local file="$1"
    local content="$2"
    echo "$content" > "$file"             # sobreescribe con estructura vacía
    success "Reseteado: $file"
}

main() {
    mkdir -p "$LOG_DIR"

    header "${PROJECT_NAME} — Reset de Datos"
    info "Fecha:      $TIMESTAMP"
    info "Directorio: $(pwd)"

    # Verificar raíz del proyecto
    if [[ ! -f "pom.xml" ]]; then
        error "pom.xml no encontrado. ¿Estás en la raíz del proyecto?"
        exit 1
    fi

    # Advertencia sobre lo que se perderá
    echo ""
    warn "ADVERTENCIA: Esta operación borrará todos los datos de:"
    warn "  • buses.json     — lista de buses"
    warn "  • tiquetes.json  — tickets pendientes"
    warn "  • colas.json     — colas de espera"
    warn "  • atendidos.json — historial de tickets completados"
    warn "  • grafo.json     — rutas del grafo"
    warn "  config.json NO será modificado (usuarios y nombre del terminal)."
    echo ""

    # Confirmación explícita antes de proceder
    echo -ne "${YELLOW}¿Confirmar reset? [s/N]: ${RESET}"
    read -r confirm || confirm=""          # read -r para no interpretar backslashes

    if [[ "${confirm,,}" != "s" ]]; then   # comparación case-insensitive
        info "Reset cancelado por el usuario."
        exit 0
    fi

    header "Reseteando archivos"

    # Listas enlazadas vacías — Gson serializa cabeza nula como objeto vacío
    write_empty "buses.json"     "{}"
    write_empty "tiquetes.json"  "{}"
    write_empty "colas.json"     "{}"
    write_empty "atendidos.json" "{}"

    # El grafo usa un arreglo de localidades, no un nodo cabeza
    write_empty "grafo.json"     '{"localidades":[]}'

    # Registro de la operación en el log persistente
    echo "[$TIMESTAMP] Reset ejecutado — todos los archivos de datos vaciados" >> "$LOG_FILE"

    echo ""
    success "✅  Reset completo. Reiniciá la aplicación para empezar con datos limpios."
    info "Log: $LOG_FILE"
}

main "$@"
