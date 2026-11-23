#include "clustering.h"
#include "matrix_math.h"
#include "types.h"
#include <stdio.h>
#include <string.h>

static LinkageMethod parse_method(const char* str) {
    if (strcmp(str, "VMC") == 0) return VMC;
    if (strcmp(str, "VML") == 0) return VML;
    return PROMEDIO;
}

int main(void) {
    int n, p;
    char method_str[32];

    if (scanf("%d %d %31s", &n, &p, method_str) != 3) {
        fprintf(stderr, "Error: formato esperado '<rows> <cols> <METHOD>'.\n");
        return 1;
    }

    double** data = alloc_matrix(n, p);
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < p; j++) {
            if (scanf("%lf", &data[i][j]) != 1) {
                fprintf(stderr, "Error leyendo la matriz en (%d, %d).\n", i, j);
                free_matrix(data, n);
                return 1;
            }
        }
    }

    double** dist_matrix = alloc_matrix(n, n);
    if (compute_mahalanobis_matrix(data, n, p, dist_matrix) != 0) {
        fprintf(stderr, "Error: la matriz de covarianza es singular (no invertible). Asegurate de tener mas filas que columnas y sin columnas constantes/colineales.\n");
        free_matrix(data, n);
        free_matrix(dist_matrix, n);
        return 1;
    }

    run_clustering_and_print_json(dist_matrix, n, parse_method(method_str));

    free_matrix(data, n);
    free_matrix(dist_matrix, n);
    return 0;
}