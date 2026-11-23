package org.uaemex.cleanerservice.models.dto;

public record GowerResult(
        Integer rowIndexJ,
        Integer rowIndexI,
        Double totalSimilirality,
        Double distance
) {
}
