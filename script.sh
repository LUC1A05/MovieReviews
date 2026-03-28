#! /bin/bash

# 1. Intentar localizar Java automáticamente
JAVA_EXEC=$(command -v java)
EXEKUTAGARRIA="../MovieReviews.jar"
CP=".;$EXEKUTAGARRIA"

# Ajustarlo al PATH de cada uno
MOVIES_PATH="C:\Users\asier\OneDrive\Escritorio\movies_reviews"
MAIN="MainDatuZientzia"

PARAM1=(500, 1000, 1500)
PARAM2=(0, 1)
PARAM3=(0, 1)
PARAM4=(0, 1)
PARAM5=(0, 1)
PARAM6=(1000, 1500, 2000)

echo "--- MainDatuZientzia exekutatzen ---"

for p1 in "${PARAM1[@]}"; do
    for p2 in "${PARAM2[@]}"; do
        for p3 in "${PARAM3[@]}"; do
            for p4 in "${PARAM4[@]}"; do
                for p5 in "${PARAM5[@]}"; do
                    for p6 in "${PARAM6[@]}"; do

				"$JAVA_EXEC" --add-opens java.base/java.lang=ALL-UNNAMED \
            					-cp "$CP" \
            					"$MAIN" $MOVIES_PATH "$p1" "$p2" "$p3" "$p4" "$p5" "$p6"

			done
		done
	done
	done
done
done

echo "--- Ejecución finalizada ---"
