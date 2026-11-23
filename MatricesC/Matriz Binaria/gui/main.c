#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include "../domain/headers/reader.h"
#include "../domain/headers/statistic.h"

static int equalsIgnoreCase(const char *a, const char *b) {
    while (*a && *b) {
        if (tolower((unsigned char)*a) != tolower((unsigned char)*b)) return 0;
        a++;
        b++;
    }
    return *a == '\0' && *b == '\0';
}

int main(int argc, char *argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Uso: %s <vmc|vml|promedio>\n", argv[0]);
        return 1;
    }

    LinkageMethod linkMethod;
    const char *linkName;
    if (equalsIgnoreCase(argv[1], "vmc")) {
        linkMethod = LINK_VMC;
        linkName = "VMC";
    } else if (equalsIgnoreCase(argv[1], "vml")) {
        linkMethod = LINK_VML;
        linkName = "VML";
    } else if (equalsIgnoreCase(argv[1], "promedio")) {
        linkMethod = LINK_PROMEDIO;
        linkName = "PROMEDIO";
    } else {
        fprintf(stderr, "Tecnica de eslabonamiento no valida: %s (usa vmc, vml o promedio)\n", argv[1]);
        return 1;
    }

    BinaryMatrix matrix = {0, 0, NULL};
    if (!readMatrixFromStdin(&matrix)) {
        fprintf(stderr, "Error al leer la matriz binaria desde stdin (minimo 2 filas y 1 columna).\n");
        return 1;
    }

    MethodResult jaccard = computeMethodResult(&matrix, DIST_JACCARD, linkMethod);
    MethodResult sokal = computeMethodResult(&matrix, DIST_SOKAL, linkMethod);

    printDualResultJson(linkName, &jaccard, &sokal, matrix.rows);

    freeMethodResult(&jaccard, matrix.rows);
    freeMethodResult(&sokal, matrix.rows);
    freeBinaryMatrix(&matrix);

    return 0;
}