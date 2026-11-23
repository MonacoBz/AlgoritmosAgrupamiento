package org.uaemex.gatewayservice.dto;

public record JaccardAndSockalResponse(
        String linkageMethod,
        ClusteringResultDto jaccard,
        ClusteringResultDto sokal
) {
}
