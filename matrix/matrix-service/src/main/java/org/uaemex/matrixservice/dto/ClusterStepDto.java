package org.uaemex.matrixservice.dto;

public record ClusterStepDto(
        int step,
        int clusterAId,
        int clusterBId,
        String clusterALabel,
        String clusterBLabel,
        int newClusterId,
        String newClusterLabel,
        double distance,
        int size
) {
}
