package org.uaemex.gatewayservice.dto;

import org.uaemex.gatewayservice.domain.Matrix;

public record MatrixRequestTypeDto(
        Matrix m,
        LinkageMethod method,
        MatrixResolver resolver
) {
}
