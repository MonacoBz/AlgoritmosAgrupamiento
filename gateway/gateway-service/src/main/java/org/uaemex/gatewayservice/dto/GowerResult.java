package org.uaemex.gatewayservice.dto;

public record GowerResult(
        Integer rowIndexJ,
        Integer rowIndexI,
        Double totalSimilirality,
        Double distance
) {
}
