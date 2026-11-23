package org.uaemex.matrixservice.dto;

public record JaccardAndSockalResponse(
        String linkageMethod,
        ClusteringResultDto jaccard,
        ClusteringResultDto sokal
) {
}
