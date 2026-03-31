#!/bin/bash

JAVA_EXEC=$(command -v java)
EXEKUTAGARRIA="/home/larrain/Desktop/MovieReviews/MovieReviews.jar"
CP=".;$EXEKUTAGARRIA"
MOVIES_PATH="/home/larrain/Desktop/MovieReviews/movies_reviews"
MAIN="MainDatuZientzia"
LOG_DIR="./logs_test_a"
mkdir -p "$LOG_DIR"

# Test A: Diccionario consistente, parámetros actuales
# W=1M, S=0 (sin stemmer), TF=1, IDF=1, WC=1, F=800

p1=1000000
p2=0
p3=1
p4=1
p5=1
p6=800

LOG_FILE="$LOG_DIR/TestA_W${p1}_S${p2}_TF${p3}_I${p4}_WC${p5}_F${p6}.txt"

echo "=== TEST A: Diccionario Consistente (hiztegia_train.txt) ==="
echo "Config: W:$p1 S:$p2 TF:$p3 IDF:$p4 WC:$p5 F:$p6"
echo "Guardando en: $LOG_FILE"
echo "Inicio: $(date)"

"$JAVA_EXEC" --add-opens java.base/java.lang=ALL-UNNAMED \
    -jar "$EXEKUTAGARRIA" "$MOVIES_PATH" "$p1" "$p2" "$p3" "$p4" "$p5" "$p6" > "$LOG_FILE" 2>&1

echo "Fin: $(date)"
echo ""
echo "Resultado (última línea con F-MEASURE):"
grep -A 2 "--- F-MEASURE ---" "$LOG_FILE" | tail -2
