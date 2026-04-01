#!/bin/bash

# ==============================================================================
# Konfigurazioa
# ==============================================================================

# 1. Javaren bertsioa hartzen du (17 erabili da proiekturako)
JAVA_EXEC=$(command -v java)

# 2. JAR-aren path
EXEKUTAGARRIA="/home/larrain/Desktop/MovieReviews/MovieReviews.jar"

# Datasetaren path
MOVIES_PATH="/home/larrain/Desktop/MovieReviews/movies_reviews"

# 4. Log karpeta
LOG_DIR="./log_experimetnalak"
mkdir -p "$LOG_DIR"

# ==============================================================================
# GRID SEARCH
# (Nota: Bash-en arrayak espazioak dituzte separadore bezala)
# ==============================================================================
PARAM_WORDS=(10000 50000 100000)      # p1: WordsToKeep
PARAM_STEM=(0 1)                 # p2: Use Stemmer
PARAM_TF=(1)                   # p3: Use TF
PARAM_IDF=(0)                  # p4: Use IDF
PARAM_WC=(1)                   # p5: Use Word Counts
PARAM_FEAT=(800)      # p6: Atributua kantitatea

echo "--- Froga bateria hasiko da ---"
echo "Emaitzak: $LOG_DIR"

# ==============================================================================
# BEKTORIZAZIO BEGIZTA
# ==============================================================================
for p1 in "${PARAM_WORDS[@]}"; do
    for p2 in "${PARAM_STEM[@]}"; do
        for p3 in "${PARAM_TF[@]}"; do
            for p4 in "${PARAM_IDF[@]}"; do
                for p5 in "${PARAM_WC[@]}"; do
                    for p6 in "${PARAM_FEAT[@]}"; do

                        #Logfile bat sortu izenean parametroen espezifikazioak ematen dituena
                        LOG_FILE="$LOG_DIR/res_W${p1}_S${p2}_TF${p3}_I${p4}_WC${p5}_F${p6}.txt"
                        
                        echo "Exekutatzen: W:$p1 S:$p2 TF:$p3 IDF:$p4 WC:$p5 F:$p6"

                        #Exekuzio komandoa
                        "$JAVA_EXEC" --add-opens java.base/java.lang=ALL-UNNAMED \
                            -jar "$EXEKUTAGARRIA" "$MOVIES_PATH" "$p1" "$p2" "$p3" "$p4" "$p5" "$p6" > "$LOG_FILE" 2>&1
                    done
                done
            done
        done
    done
done

echo "--- Frogak bukatu dira ---"