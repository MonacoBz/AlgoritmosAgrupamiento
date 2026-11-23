package org.uaemex.matrixservice.dto;

import org.uaemex.matrixservice.model.Matrix;

import java.util.List;

public record GowerResponseDto(
        Matrix matrixData,
        List<GowerResult> gowerResults
) {
}
