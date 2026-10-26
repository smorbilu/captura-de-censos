#!/usr/bin/env bash
set -euo pipefail

if [ "$#" -ne 1 ]; then
    echo "Uso: $0 <cantidad_de_censos>   |   $0 --limpiar" >&2
    exit 1
fi

ARGUMENTO="$1"

if [ "$ARGUMENTO" != "--limpiar" ]; then
    if ! [[ "$ARGUMENTO" =~ ^[0-9]+$ ]] || [ "$ARGUMENTO" -lt 1 ]; then
        echo "La cantidad debe ser un entero mayor a 0" >&2
        exit 1
    fi
fi

RAIZ="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="$RAIZ/.env"

leer_env() {
    local valor
    valor="$(grep -E "^$1=" "$ENV_FILE" 2>/dev/null | head -1 | cut -d= -f2- | tr -d '\r' || true)"
    valor="${valor%\"}"
    valor="${valor#\"}"
    echo "${valor:-$2}"
}

export PGPASSWORD="$(leer_env DB_PASSWORD "")"

PSQL=(
    psql
    -h "$(leer_env DB_HOST localhost)"
    -p "$(leer_env DB_PORT 5432)"
    -U "$(leer_env DB_USER postgres)"
    -d "$(leer_env DB_NAME login_db)"
    -v ON_ERROR_STOP=1
    -q -t -A
)

"${PSQL[@]}" -f "$RAIZ/sql/censos_masivos.sql" > /dev/null

if [ "$ARGUMENTO" = "--limpiar" ]; then
    ELIMINADOS="$("${PSQL[@]}" -c "SELECT eliminar_censos_masivos();")"
    echo "Censos masivos eliminados: $ELIMINADOS"
else
    INSERTADOS="$("${PSQL[@]}" -c "SELECT generar_censos_masivos($ARGUMENTO);")"
    TOTAL="$("${PSQL[@]}" -c "SELECT COUNT(*) FROM censos WHERE origen = 'masivo';")"
    echo "Censos masivos insertados: $INSERTADOS (total masivos en la base: $TOTAL)"
fi
