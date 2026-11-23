package org.uaemex.matrixservice.dto;

import java.util.List;

public record ClusteringResultDto(
        List<List<Double>> distanceMatrix,
        List<RelationDto> sortedRelations,
        List<ClusterStepDto> clusteringHistory
) {
}
