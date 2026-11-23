package org.uaemex.gatewayservice.dto;

import org.uaemex.gatewayservice.domain.Matrix;

import java.util.List;

public record MatrixReport(
        Matrix matrix,
        List<String> report
) {
}
