package org.uaemex.gatewayservice.dto;


import org.uaemex.gatewayservice.domain.Matrix;

public record MatrixRequestDto(
        Matrix m,
        LinkageMethod method
) {
}
