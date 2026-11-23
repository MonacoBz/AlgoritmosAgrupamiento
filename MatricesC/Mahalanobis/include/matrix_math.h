#ifndef MATRIX_MATH_H
#define MATRIX_MATH_H

double** alloc_matrix(int rows, int cols);
void free_matrix(double** mat, int rows);

/* Calcula la matriz de distancias de Mahalanobis (n x n).
   Retorna 0 si tuvo éxito, o -1 si la matriz de covarianza es singular (no invertible). */
int compute_mahalanobis_matrix(double** data, int n, int p, double** dist_matrix);

#endif