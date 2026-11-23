#include "headers/cleaner.h"
#include <stdlib.h>
#include <stdio.h>

int assign_value(const char* text){
    double numero = atof(text);
    if (numero >= 0.5)return 1;
    return 0;
}

int clean_matriz(int binary_matriz[ROWS][COLUMNS], char dirty_matriz[ROWS][COLUMNS][50],int total_rows, int total_columns){
    for(int i = 0; i < total_rows; i++){
        for(int j = 0; j < total_columns; j++){
            int value = assign_value(dirty_matriz[i][j]);
            printf("%d",value);
            binary_matriz[i][j] = value;
        }
        printf("\n");
    }
    return 1;
}

