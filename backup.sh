#!/usr/bin/env bash
# =============================================================================
# BusNovaTech — Backup con marca de tiempo de todos los archivos JSON
# Autor: Nyyko
# Uso:   ./backup.sh
# =============================================================================

set -euo pipefail

# ── Colores ──────────────────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# ── Config ───────────────────────────────────────────────────────────────────
PROJECT_NAME="BusNovaTech"
BACKUP_BASE="backups"
LOG_DIR="logs"
TIMESTAMP=$(date '+%Y-%m-%d_%H-%M-%S')
BACKUP_DIR="${BACKUP_BASE}/backup_${TIMESTAMP}"   # carpeta única por ejecución
LOG_FILE="${LOG_DIR}/backup.log"

# ── Helpers ──────────────────────────────────────────────────────────────────
log()     { echo -e "$1" | tee -a "$LOG_FILE"; }
info()    { log "${CYAN}[INFO]${RESET}  $1"; }
success() { log "${GREEN}[OK]${RESET}    $1"; }
warn()    { log "${YELLOW}[WARN]${RESET}  $1"; }
error()   { log "${RED}[FAIL]${RESET}  $1"; }
header()  { log "\n${BOLD}${CYAN}════════════════════════════════════════${RESET}"; log "${BOLD}  $1${RESET}"; log "${BOLD}${CYAN}════════════════════════════════════════${RESET}"; }

main() {
    mkdir -p "$LOG_DIR" "$BACKUP_DIR"

    header "${PROJECT_NAME} — Backup de Datos"
    info "Fecha:   $TIMESTAMP"
    info "Destino: $BACKUP_DIR"

    # Verificar raíz del proyecto
    if [[ ! -f "pom.xml" ]]; then
        error "pom.xml no encontrado. ¿Estás en la raíz del proyecto?"
        exit 1
    fi

    header "Copiando archivos JSON"

    local files_backed=0

    # Iterar sobre todos los .json en la raíz del proyecto
    for json_file in *.json; do
        [[ -f "$json_file" ]] || continue   # saltar si el glob no matchea nada

        cp "$json_file" "${BACKUP_DIR}/${json_file}"
        local size
        size=$(du -sh "$json_file" | cut -f1)
        success "${json_file}  (${size})"
        files_backed=$((files_backed + 1))
    done

    # Sin archivos — advertir y salir limpiamente
    if [[ $files_backed -eq 0 ]]; then
        warn "No se encontraron archivos .json en la raíz del proyecto."
        rmdir "$BACKUP_DIR" 2>/dev/null || true    # limpiar carpeta vacía
        exit 0
    fi

    # Calcular tamaño total de la carpeta de backup
    local total_size
    total_size=$(du -sh "$BACKUP_DIR" | cut -f1)

    header "Resumen"
    info "Archivos respaldados : $files_backed"
    info "Tamaño total         : $total_size"
    info "Ubicación            : $BACKUP_DIR"

    # Entrada en el log de backup para historial
    echo "[$TIMESTAMP] Backup: $files_backed archivo(s) → $BACKUP_DIR (total: $total_size)" >> "$LOG_FILE"

    echo ""
    success "✅  Backup completado exitosamente."
}

main "$@"
