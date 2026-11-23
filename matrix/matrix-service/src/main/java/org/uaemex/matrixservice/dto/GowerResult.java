package org.uaemex.matrixservice.dto;

public record GowerResult(
        Integer rowIndexJ,
        Integer rowIndexI,
        Double totalSimilirality,
        Double distance
) {
}
