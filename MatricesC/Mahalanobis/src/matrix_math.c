#include "matrix_math.h"
#include <math.h>
#include <stdlib.h>

double** alloc_matrix(int rows, int cols) {
    double** mat = (double**)malloc(rows * sizeof(double*));
    for (int i = 0; i < rows; i++) {
        mat[i] = (double*)calloc(cols, sizeof(double));
    }
    return mat;
}

void free_matrix(double** mat, int rows) {
    if (!mat) return;
    for (int i = 0; i < rows; i++) {
        free(mat[i]);
    }
    free(mat);
}

static int invert_matrix_gauss_jordan(double** src, double** inv, int p) {
    double** aug = alloc_matrix(p, 2 * p);

    for (int i = 0; i < p; i++) {
        for (int j = 0; j < p; j++) {
            aug[i][j] = src[i][j];
            aug[i][j + p] = (i == j) ? 1.0 : 0.0;
        }
    }

    for (int i = 0; i < p; i++) {
        // Pivoteo parcial para estabilidad numérica
        int pivot_row = i;
        for (int r = i + 1; r < p; r++) {
            if (fabs(aug[r][i]) > fabs(aug[pivot_row][i])) {
                pivot_row = r;
            }
        }

        if (fabs(aug[pivot_row][i]) < 1e-12) {
            free_matrix(aug, p);
            return -1; // Matriz singular
        }

        if (pivot_row != i) {
            double* temp = aug[i];
            aug[i] = aug[pivot_row];
            aug[pivot_row] = temp;
        }

        double pivot = aug[i][i];
        for (int j = 0; j < 2 * p; j++) {
            aug[i][j] /= pivot;
        }

        for (int r = 0; r < p; r++) {
            if (r != i) {
                double factor = aug[r][i];
                for (int j = 0; j < 2 * p; j++) {
                    aug[r][j] -= factor * aug[i][j];
                }
            }
        }
    }

    for (int i = 0; i < p; i++) {
        for (int j = 0; j < p; j++) {
            inv[i][j] = aug[i][j + p];
        }
    }

    free_matrix(aug, p);
    return 0;
}

int compute_mahalanobis_matrix(double** data, int n, int p, double** dist_matrix) {
    if (n <= 1 || p <= 0) return -1;

    // 1. Vector de medias
    double* mean = (double*)calloc(p, sizeof(double));
    for (int j = 0; j < p; j++) {
        for (int i = 0; i < n; i++) {
            mean[j] += data[i][j];
        }
        mean[j] /= n;
    }

    // 2. Matriz de covarianza muestral S (p x p)
    double** cov = alloc_matrix(p, p);
    for (int a = 0; a < p; a++) {
        for (int b = a; b < p; b++) {
            double sum = 0.0;
            for (int i = 0; i < n; i++) {
                sum += (data[i][a] - mean[a]) * (data[i][b] - mean[b]);
            }
            cov[a][b] = sum / (n - 1);
            cov[b][a] = cov[a][b];
        }
    }

    // 3. Inversa de covarianza S^-1
    double** inv_cov = alloc_matrix(p, p);
    if (invert_matrix_gauss_jordan(cov, inv_cov, p) != 0) {
        free(mean);
        free_matrix(cov, p);
        free_matrix(inv_cov, p);
        return -1;
    }

    // 4. Distancias de Mahalanobis entre cada par (i, j)
    double* diff = (double*)malloc(p * sizeof(double));
    for (int i = 0; i < n; i++) {
        dist_matrix[i][i] = 0.0;
        for (int j = i + 1; j < n; j++) {
            for (int k = 0; k < p; k++) {
                diff[k] = data[i][k] - data[j][k];
            }

            double quad = 0.0;
            for (int a = 0; a < p; a++) {
                for (int b = 0; b < p; b++) {
                    quad += diff[a] * inv_cov[a][b] * diff[b];
                }
            }

            double d = sqrt(quad > 0.0 ? quad : 0.0);
            dist_matrix[i][j] = d;
            dist_matrix[j][i] = d;
        }
    }

    free(mean);
    free(diff);
    free_matrix(cov, p);
    free_matrix(inv_cov, p);
    return 0;
}