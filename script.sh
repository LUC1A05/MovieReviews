#! /bin/bash

# 1. Intentar localizar Java automáticamente
JAVA_EXEC=$(command -v java)
EXEKUTAGARRIA="../MovieReviews.jar"
CP=".;$EXEKUTAGARRIA"

# Ajustarlo al PATH de cada uno
MOVIES_PATH="C:\Users\asier\OneDrive\Escritorio\movies_reviews"

echo "--- MainDatuZientzia exekutatzen ---"
echo "Usando Classpath: $CP"

"$JAVA_EXEC" --add-opens java.base/java.lang=ALL-UNNAMED \
            -cp "$CP" \
            MainDatuZientzia $MOVIES_PATH

echo "--- Ejecución finalizada ---"
