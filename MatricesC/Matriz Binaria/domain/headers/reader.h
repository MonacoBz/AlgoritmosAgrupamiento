#ifndef READER_H
#define READER_H

typedef struct {
    int rows;
    int cols;
    int **data;
} BinaryMatrix;

int readMatrixFromStdin(BinaryMatrix *matrix);
void freeBinaryMatrix(BinaryMatrix *matrix);

#endif