package org.uaemex.cleanerservice.models.dto;

import org.uaemex.cleanerservice.models.Matrix;

import java.util.List;

public record MatrixReport(
        Matrix matrix,
        List<String> report
) {
}
