#!/usr/bin/env bash
# =============================================================================
# BusNovaTech — Build & Test Automation Script
# Autor: Nyyko
# Uso:   ./build.sh [--clean] [--test] [--report] [--all]
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
BUILD_DIR="target"
LOG_DIR="logs"
REPORT_DIR="reports"
TIMESTAMP=$(date '+%Y-%m-%d_%H-%M-%S')
LOG_FILE="${LOG_DIR}/build_${TIMESTAMP}.log"
REPORT_FILE="${REPORT_DIR}/report_${TIMESTAMP}.txt"
MAVEN_CMD=""   # se resuelve dinámicamente en preflight_checks

# ── Contadores ───────────────────────────────────────────────────────────────
STEPS_TOTAL=0
STEPS_PASSED=0
STEPS_FAILED=0
START_TIME=$(date +%s)

# ── Helpers ──────────────────────────────────────────────────────────────────
log()     { echo -e "$1" | tee -a "$LOG_FILE"; }
info()    { log "${CYAN}[INFO]${RESET}  $1"; }
success() { log "${GREEN}[OK]${RESET}    $1"; STEPS_PASSED=$((STEPS_PASSED + 1)); }
warn()    { log "${YELLOW}[WARN]${RESET}  $1"; }
error()   { log "${RED}[FAIL]${RESET}  $1"; STEPS_FAILED=$((STEPS_FAILED + 1)); }
header()  { log "\n${BOLD}${CYAN}════════════════════════════════════════${RESET}"; log "${BOLD}  $1${RESET}"; log "${BOLD}${CYAN}════════════════════════════════════════${RESET}"; }

step() {
    STEPS_TOTAL=$((STEPS_TOTAL + 1))
    log "\n${BOLD}[STEP $STEPS_TOTAL]${RESET} $1"
}

run_cmd() {
    local desc="$1"; shift
    if "$@" >> "$LOG_FILE" 2>&1; then
        success "$desc"
        return 0
    else
        error "$desc"
        return 1
    fi
}

elapsed() {
    local end=$(date +%s)
    echo $((end - START_TIME))
}

# ── Detección automática de Maven ────────────────────────────────────────────
find_maven() {
    # 1. mvn en el PATH
    if command -v mvn &>/dev/null; then echo "mvn"; return 0; fi

    # 2. Variables de entorno
    [[ -n "${M2_HOME:-}"    && -x "${M2_HOME}/bin/mvn"    ]] && { echo "${M2_HOME}/bin/mvn";    return 0; }
    [[ -n "${MAVEN_HOME:-}" && -x "${MAVEN_HOME}/bin/mvn" ]] && { echo "${MAVEN_HOME}/bin/mvn"; return 0; }

    # 3. NetBeans bundled Maven — rutas de Windows en Git Bash
    local nb_base="/c/Program Files"
    for nb_dir in \
        "Apache NetBeans" \
        "NetBeans-21" "NetBeans-22" "NetBeans-23" "NetBeans-24" \
        "Apache NetBeans 21" "Apache NetBeans 22" "Apache NetBeans 23" "Apache NetBeans 24"
    do
        local c="${nb_base}/${nb_dir}/java/maven/bin/mvn"
        [[ -x "$c" ]] && { echo "$c"; return 0; }
    done

    # 4. Maven standalone en rutas frecuentes
    for p in \
        "${LOCALAPPDATA:-$HOME/AppData/Local}/Programs/NetBeans/java/maven/bin/mvn" \
        "$HOME/.sdkman/candidates/maven/current/bin/mvn" \
        "/c/opt/maven/bin/mvn" "/c/tools/maven/bin/mvn" \
        "/usr/local/bin/mvn" "/usr/share/maven/bin/mvn"
    do
        [[ -x "$p" ]] && { echo "$p"; return 0; }
    done

    # 5. Búsqueda dinámica en Program Files como último recurso
    local dyn
    dyn=$(find "/c/Program Files" -name "mvn" -maxdepth 6 -type f 2>/dev/null | head -1 || true)
    [[ -n "$dyn" ]] && { echo "$dyn"; return 0; }

    return 1
}

# ── Setup de directorios ──────────────────────────────────────────────────────
init_dirs() {
    mkdir -p "$LOG_DIR" "$REPORT_DIR"
    # Header del log
    echo "============================================" > "$LOG_FILE"
    echo "  $PROJECT_NAME Build Log — $TIMESTAMP"     >> "$LOG_FILE"
    echo "============================================" >> "$LOG_FILE"
}

# ── Verificaciones previas ────────────────────────────────────────────────────
preflight_checks() {
    header "Pre-flight Checks"

    step "Verificando Java"
    if command -v java &>/dev/null; then
        local jver
        jver=$(java -version 2>&1 | head -1)
        success "Java encontrado: $jver"
    else
        error "Java no encontrado. Instalá JDK antes de continuar."
        exit 1
    fi

    step "Verificando Maven"
    if MAVEN_CMD=$(find_maven); then
        local mver
        mver=$("$MAVEN_CMD" -version 2>&1 | head -1)
        success "Maven encontrado: $mver"
        info "Ruta: $MAVEN_CMD"
    else
        error "Maven no encontrado. Instalalo o definí M2_HOME / MAVEN_HOME."
        exit 1
    fi

    step "Verificando pom.xml"
    if [[ -f "pom.xml" ]]; then
        success "pom.xml encontrado"
    else
        error "pom.xml no encontrado. ¿Estás en la raíz del proyecto?"
        exit 1
    fi

    step "Verificando directorio src/"
    if [[ -d "src" ]]; then
        local src_count
        src_count=$(find src -name "*.java" | wc -l)
        success "src/ encontrado ($src_count archivos .java)"
    else
        error "Directorio src/ no encontrado."
        exit 1
    fi
}

# ── Clean ─────────────────────────────────────────────────────────────────────
do_clean() {
    header "Clean"
    step "Limpiando build anterior (mvn clean)"
    run_cmd "mvn clean exitoso" "$MAVEN_CMD" clean
}

# ── Compile ───────────────────────────────────────────────────────────────────
do_compile() {
    header "Compile"
    step "Compilando proyecto (mvn compile)"
    if run_cmd "Compilación exitosa" "$MAVEN_CMD" compile; then
        local class_count
        class_count=$(find "$BUILD_DIR" -name "*.class" 2>/dev/null | wc -l)
        info "Clases generadas: $class_count"
    else
        error "La compilación falló. Revisá el log: $LOG_FILE"
        return 1
    fi
}

# ── Test ──────────────────────────────────────────────────────────────────────
do_test() {
    header "Test"
    step "Ejecutando tests (mvn test)"

    if run_cmd "Tests ejecutados" "$MAVEN_CMD" test; then
        success "Todos los tests pasaron"
    else
        warn "Algunos tests fallaron. Revisá el log para detalles."
    fi

    # Parsear resultado de Surefire si existe
    local surefire_dir="${BUILD_DIR}/surefire-reports"
    if [[ -d "$surefire_dir" ]]; then
        local total=0 failures=0 errors=0 skipped=0
        for xml in "$surefire_dir"/*.xml; do
            [[ -f "$xml" ]] || continue
            total=$((total     + $(grep -oP 'tests="\K[0-9]+'    "$xml" 2>/dev/null | head -1 || echo 0)))
            failures=$((failures + $(grep -oP 'failures="\K[0-9]+' "$xml" 2>/dev/null | head -1 || echo 0)))
            errors=$((errors   + $(grep -oP 'errors="\K[0-9]+'   "$xml" 2>/dev/null | head -1 || echo 0)))
            skipped=$((skipped + $(grep -oP 'skipped="\K[0-9]+'  "$xml" 2>/dev/null | head -1 || echo 0)))
        done
        info "Tests: total=$total  failures=$failures  errors=$errors  skipped=$skipped"
    fi
}

# ── Package ───────────────────────────────────────────────────────────────────
do_package() {
    header "Package"
    step "Empaquetando JAR (mvn package -DskipTests)"
    if run_cmd "JAR generado" "$MAVEN_CMD" package -DskipTests; then
        local jar
        jar=$(find "$BUILD_DIR" -maxdepth 1 -name "*.jar" | head -1)
        if [[ -n "$jar" ]]; then
            local size
            size=$(du -sh "$jar" | cut -f1)
            info "JAR: $jar ($size)"
        fi
    fi
}

# ── Análisis de código ─────────────────────────────────────────────────────────
do_analysis() {
    header "Code Analysis"

    step "Contando líneas de código"
    local total_lines=0
    local java_files=0
    while IFS= read -r file; do
        lines=$(wc -l < "$file")
        total_lines=$((total_lines + lines))
        java_files=$((java_files + 1))
    done < <(find src -name "*.java")
    success "Archivos Java: $java_files | Líneas totales: $total_lines"

    step "Detectando uso de java.util (restricción del curso)"
    local violations
    violations=$(grep -rn "import java\.util\." src/ 2>/dev/null || true)
    if [[ -z "$violations" ]]; then
        success "Sin imports de java.util ✓ (cumple restricción CS-304)"
    else
        warn "Se encontraron imports de java.util:"
        echo "$violations" | while IFS= read -r line; do
            warn "  $line"
        done
    fi

    step "Buscando TODOs / FIXMEs pendientes"
    local todos
    todos=$(grep -rn "TODO\|FIXME\|HACK\|XXX" src/ 2>/dev/null || true)
    if [[ -z "$todos" ]]; then
        success "Sin TODOs pendientes"
    else
        local todo_count
        todo_count=$(echo "$todos" | wc -l)
        warn "$todo_count comentario(s) pendiente(s) encontrado(s)"
        echo "$todos" | head -5 | while IFS= read -r line; do
            warn "  $line"
        done
    fi
}

# ── Reporte final ──────────────────────────────────────────────────────────────
generate_report() {
    header "Reporte Final"

    local duration
    duration=$(elapsed)
    local status_icon
    if [[ $STEPS_FAILED -eq 0 ]]; then
        status_icon="✅ BUILD EXITOSO"
    else
        status_icon="❌ BUILD CON ERRORES"
    fi

    cat > "$REPORT_FILE" <<EOF
============================================
  $PROJECT_NAME — Build Report
  $TIMESTAMP
============================================

STATUS:       $status_icon
Duración:     ${duration}s
Steps total:  $STEPS_TOTAL
  ✓ Pasados:  $STEPS_PASSED
  ✗ Fallidos: $STEPS_FAILED

Log completo: $LOG_FILE
============================================
EOF

    cat "$REPORT_FILE" | tee -a "$LOG_FILE"
    info "Reporte guardado en: $REPORT_FILE"

    if [[ $STEPS_FAILED -eq 0 ]]; then
        log "\n${GREEN}${BOLD}  ✅  BUILD COMPLETO en ${duration}s${RESET}\n"
        exit 0
    else
        log "\n${RED}${BOLD}  ❌  BUILD FALLIDO — $STEPS_FAILED error(s)${RESET}\n"
        exit 1
    fi
}

# ── Ayuda ─────────────────────────────────────────────────────────────────────
usage() {
    echo -e "${BOLD}Uso:${RESET} ./build.sh [opciones]"
    echo ""
    echo "Opciones:"
    echo "  --clean    Limpiar archivos de build anteriores"
    echo "  --test     Compilar y correr tests"
    echo "  --report   Incluir análisis de código"
    echo "  --all      Todo lo anterior (recomendado)"
    echo "  --help     Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  ./build.sh --all"
    echo "  ./build.sh --clean --test"
    exit 0
}

# ── Entry point ───────────────────────────────────────────────────────────────
main() {
    local do_clean_flag=false
    local do_test_flag=false
    local do_report_flag=false

    # Parse args
    if [[ $# -eq 0 ]]; then
        do_clean_flag=true
        do_test_flag=false
        do_report_flag=false
    fi

    for arg in "$@"; do
        case "$arg" in
            --clean)  do_clean_flag=true ;;
            --test)   do_clean_flag=true; do_test_flag=true ;;
            --report) do_report_flag=true ;;
            --all)    do_clean_flag=true; do_test_flag=true; do_report_flag=true ;;
            --help|-h) usage ;;
            *) warn "Argumento desconocido: $arg"; usage ;;
        esac
    done

    init_dirs

    header "${PROJECT_NAME} Build Script"
    info "Inicio: $TIMESTAMP"
    info "Directorio: $(pwd)"

    preflight_checks
    $do_clean_flag && do_clean
    do_compile
    $do_test_flag && do_test
    do_package
    $do_report_flag && do_analysis
    generate_report
}

main "$@"
