#include "headers/reader.h"
#include <stdio.h>
#include <stdlib.h>

int readMatrixFromStdin(BinaryMatrix *matrix) {
    if (scanf("%d %d", &matrix->rows, &matrix->cols) != 2) {
        return 0;
    }
    if (matrix->rows < 2 || matrix->cols < 1) {
        return 0;
    }

    matrix->data = (int **)malloc(matrix->rows * sizeof(int *));
    if (!matrix->data) return 0;

    for (int i = 0; i < matrix->rows; i++) {
        matrix->data[i] = (int *)malloc(matrix->cols * sizeof(int));
        if (!matrix->data[i]) {
            for (int k = 0; k < i; k++) free(matrix->data[k]);
            free(matrix->data);
            return 0;
        }
        for (int j = 0; j < matrix->cols; j++) {
            if (scanf("%d", &matrix->data[i][j]) != 1) {
                for (int k = 0; k <= i; k++) free(matrix->data[k]);
                free(matrix->data);
                return 0;
            }
        }
    }
    return 1;
}

void freeBinaryMatrix(BinaryMatrix *matrix) {
    if (!matrix || !matrix->data) return;
    for (int i = 0; i < matrix->rows; i++) {
        free(matrix->data[i]);
    }
    free(matrix->data);
    matrix->data = NULL;
}