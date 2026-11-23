package org.uaemex.cleanerservice.models.dto;

import org.uaemex.cleanerservice.models.Matrix;

import java.util.List;

public record GowerResponseDto(
        Matrix matrixData,
        List<GowerResult> gowerResults
) {
}
