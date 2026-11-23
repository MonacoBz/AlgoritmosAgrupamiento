package org.uaemex.gatewayservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MatrixResponseDto(
        JaccardAndSockalResponse jaccardAndSockal,
        SingleClusteringResponse clusteringResponse
) {
}
