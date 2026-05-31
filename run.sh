#!/usr/bin/env bash
# =============================================================================
# BusNovaTech — Compilar y ejecutar la aplicación
# Autor: Nyyko
# Uso:   ./run.sh
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
LOG_DIR="logs"
TIMESTAMP=$(date '+%Y-%m-%d_%H-%M-%S')
LOG_FILE="${LOG_DIR}/run_${TIMESTAMP}.log"

# ── Helpers ──────────────────────────────────────────────────────────────────
log()     { echo -e "$1" | tee -a "$LOG_FILE"; }
info()    { log "${CYAN}[INFO]${RESET}  $1"; }
success() { log "${GREEN}[OK]${RESET}    $1"; }
warn()    { log "${YELLOW}[WARN]${RESET}  $1"; }
error()   { log "${RED}[FAIL]${RESET}  $1"; }
header()  { log "\n${BOLD}${CYAN}════════════════════════════════════════${RESET}"; log "${BOLD}  $1${RESET}"; log "${BOLD}${CYAN}════════════════════════════════════════${RESET}"; }

# ── Detectar Maven en rutas comunes ──────────────────────────────────────────
find_maven() {
    # 1. mvn disponible en el PATH (instalación estándar o SDKMAN)
    if command -v mvn &>/dev/null; then
        echo "mvn"
        return 0
    fi

    # 2. Variables de entorno explícitas
    if [[ -n "${M2_HOME:-}" && -x "${M2_HOME}/bin/mvn" ]]; then
        echo "${M2_HOME}/bin/mvn"
        return 0
    fi
    if [[ -n "${MAVEN_HOME:-}" && -x "${MAVEN_HOME}/bin/mvn" ]]; then
        echo "${MAVEN_HOME}/bin/mvn"
        return 0
    fi

    # 3. NetBeans bundled Maven — versiones comunes en Windows (Git Bash paths)
    local nb_base="/c/Program Files"
    for nb_dir in \
        "NetBeans-21" "NetBeans-22" "NetBeans-23" "NetBeans-24" \
        "Apache NetBeans 21" "Apache NetBeans 22" "Apache NetBeans 23" "Apache NetBeans 24"
    do
        local candidate="${nb_base}/${nb_dir}/java/maven/bin/mvn"
        if [[ -x "$candidate" ]]; then
            echo "$candidate"
            return 0
        fi
    done

    # 4. NetBeans en AppData del usuario (instalación por usuario)
    local appdata_nb="${LOCALAPPDATA:-$HOME/AppData/Local}/Programs/NetBeans"
    if [[ -d "$appdata_nb" ]]; then
        local found
        found=$(find "$appdata_nb" -name "mvn" -type f 2>/dev/null | head -1 || true)
        if [[ -n "$found" ]]; then
            echo "$found"
            return 0
        fi
    fi

    # 5. Maven standalone en rutas frecuentes
    local standalone_paths=(
        "/c/opt/maven/bin/mvn"
        "/c/tools/maven/bin/mvn"
        "$HOME/.sdkman/candidates/maven/current/bin/mvn"
        "/usr/local/bin/mvn"
        "/usr/share/maven/bin/mvn"
    )
    for path in "${standalone_paths[@]}"; do
        if [[ -x "$path" ]]; then
            echo "$path"
            return 0
        fi
    done

    # 6. Búsqueda dinámica en Program Files como último recurso
    local dyn
    dyn=$(find "/c/Program Files" -name "mvn" -maxdepth 6 -type f 2>/dev/null | head -1 || true)
    if [[ -n "$dyn" ]]; then
        echo "$dyn"
        return 0
    fi

    return 1   # Maven no encontrado
}

main() {
    mkdir -p "$LOG_DIR"

    header "${PROJECT_NAME} — Compilar y Ejecutar"
    info "Inicio:     $TIMESTAMP"
    info "Directorio: $(pwd)"

    # Verificar raíz del proyecto
    if [[ ! -f "pom.xml" ]]; then
        error "pom.xml no encontrado. ¿Estás en la raíz del proyecto?"
        exit 1
    fi

    # Verificar Java
    if ! command -v java &>/dev/null; then
        error "Java no encontrado. Instalá JDK 21 antes de continuar."
        exit 1
    fi
    local java_ver
    java_ver=$(java -version 2>&1 | head -1)
    success "Java: $java_ver"

    # Detectar Maven
    local maven_cmd
    if maven_cmd=$(find_maven); then
        success "Maven: $maven_cmd"
    else
        error "Maven no encontrado en el sistema."
        echo ""
        warn "¿Cómo solucionarlo?"
        warn "  1. Instalá Maven:            https://maven.apache.org/download.cgi"
        warn "  2. Agregalo al PATH:         export PATH=\$PATH:/ruta/a/maven/bin"
        warn "  3. Usá NetBeans (incluye Maven integrado) y ejecutá desde el IDE."
        warn "  4. Definí la variable M2_HOME apuntando a tu instalación de Maven."
        exit 1
    fi

    # Compilar y lanzar la aplicación Swing
    header "Compilando y ejecutando"
    info "Comando: $maven_cmd clean compile exec:java"
    echo ""

    # No redirigimos stdout/stderr al log porque exec:java abre la UI Swing
    # y el usuario necesita ver el output de Maven en la terminal
    if "$maven_cmd" clean compile exec:java; then
        echo ""
        log ""
        success "Aplicación cerrada normalmente."
        echo "[$TIMESTAMP] Ejecución completada OK" >> "$LOG_FILE"
    else
        echo ""
        error "La aplicación terminó con error. Revisá el output de Maven arriba."
        echo "[$TIMESTAMP] Ejecución terminó con error" >> "$LOG_FILE"
        exit 1
    fi
}

main "$@"
