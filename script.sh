#!/bin/bash

# ==============================================================================
# CONFIGURACIÓN DEL ENTORNO
# ==============================================================================

# 1. Localizar Java (Asegúrate de tener Java 11+ para las opciones --add-opens)
JAVA_EXEC=$(command -v java)

# 2. Ruta al ejecutable JAR y Classpath
# IMPORTANTE: Si estás en Windows (Git Bash), usa ";" como separador. En Linux/Mac usa ":"
EXEKUTAGARRIA="/home/larrain/Desktop/MovieReviews/MovieReviews.jar"
CP=".;$EXEKUTAGARRIA"

# 3. Parámetros de entrada
# Cambia esta ruta a la ubicación de tu dataset
MOVIES_PATH="/home/larrain/Desktop/MovieReviews/movies_reviews"
MAIN="MainDatuZientzia"

# 4. Carpeta de resultados
LOG_DIR="./logs_experimentos"
mkdir -p "$LOG_DIR"

# ==============================================================================
# DEFINICIÓN DEL ESPACIO DE BÚSQUEDA (GRID SEARCH)
# (Nota: En Bash los arrays se separan por ESPACIOS, no por comas)
# ==============================================================================
PARAM_WORDS=(10000 50000 100000)      # p1: WordsToKeep
PARAM_STEM=(0 1)                 # p2: Use Stemmer
PARAM_TF=(0 1)                   # p3: Use TF
PARAM_IDF=(0 1)                  # p4: Use IDF
PARAM_WC=(0)                   # p5: Use Word Counts
PARAM_FEAT=(1000)      # p6: Algún parámetro extra (ej. Atributos)

echo "--- Iniciando batería de pruebas para MainDatuZientzia ---"
echo "Resultados en: $LOG_DIR"

# ==============================================================================
# BUCLES DE EJECUCIÓN
# ==============================================================================
for p1 in "${PARAM_WORDS[@]}"; do
    for p2 in "${PARAM_STEM[@]}"; do
        for p3 in "${PARAM_TF[@]}"; do
            for p4 in "${PARAM_IDF[@]}"; do
                for p5 in "${PARAM_WC[@]}"; do
                    for p6 in "${PARAM_FEAT[@]}"; do

                        # Crear un nombre de log único basado en los parámetros
                        LOG_FILE="$LOG_DIR/res_W${p1}_S${p2}_TF${p3}_I${p4}_WC${p5}_F${p6}.txt"
                        
                        echo "Ejecutando configuración: W:$p1 S:$p2 TF:$p3 IDF:$p4 WC:$p5 F:$p6"

                        # Ejecución de Java
                        # --add-opens es necesario para que Weka acceda a funciones internas de Java
                        # Por esto otro:
                        "$JAVA_EXEC" --add-opens java.base/java.lang=ALL-UNNAMED \
                            -jar "$EXEKUTAGARRIA" "$MOVIES_PATH" "$p1" "$p2" "$p3" "$p4" "$p5" "$p6" > "$LOG_FILE" 2>&1
                    done
                done
            done
        done
    done
done

echo "--- Todas las combinaciones han sido procesadas ---"