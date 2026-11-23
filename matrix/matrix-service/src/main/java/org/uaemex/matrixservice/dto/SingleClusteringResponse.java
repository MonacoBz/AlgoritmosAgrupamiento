package org.uaemex.matrixservice.dto;

import java.util.List;

public record SingleClusteringResponse(
        String linkageMethod,
        ClusteringResultDto result,
        List<GowerResult> gowerResults
) {
}
