package org.uaemex.matrixservice.dto;

import org.uaemex.matrixservice.model.Matrix;

public record MatrixRequestDto(
        Matrix m,
        LinkageMethod method
) {
}
