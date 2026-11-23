package org.uaemex.gatewayservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SingleClusteringResponse(
        String linkageMethod,
        ClusteringResultDto result,
        List<GowerResult> gowerResults
) {
}
