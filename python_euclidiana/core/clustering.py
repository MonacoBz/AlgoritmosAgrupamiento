from dataclasses import dataclass
from typing import List
import numpy as np

from schemas.dtos import (
    ClusteringResultDto,
    ClusterStepDto,
    LinkageMethod,
    RelationDto,
)


@dataclass
class _Cluster:
    id: int
    label: str
    elements: List[int]


def _round6(val: float) -> float:
    return round(float(val), 6)


def _compute_cluster_distance(
    c_a: _Cluster, c_b: _Cluster, dist_matrix: np.ndarray, method: LinkageMethod
) -> float:
    distances = [dist_matrix[i, j] for i in c_a.elements for j in c_b.elements]

    if method == LinkageMethod.VMC:
        return float(min(distances))
    if method == LinkageMethod.VML:
        return float(max(distances))
    return float(sum(distances) / len(distances))


def build_clustering_result(
    dist_matrix: np.ndarray, method: LinkageMethod
) -> ClusteringResultDto:
    n = dist_matrix.shape[0]

    matrix_list = [[_round6(val) for val in row] for row in dist_matrix]

    sorted_relations: List[RelationDto] = []
    for i in range(n):
        for j in range(i + 1, n):
            sorted_relations.append(
                RelationDto(elementA=i + 1, elementB=j + 1, distance=_round6(dist_matrix[i, j]))
            )
    sorted_relations.sort(key=lambda r: r.distance)

    active_clusters = [_Cluster(id=i, label=f"E{i + 1}", elements=[i]) for i in range(n)]
    history: List[ClusterStepDto] = []
    next_cluster_id = n
    step = 1

    while len(active_clusters) > 1:
        min_dist = float("inf")
        idx_a, idx_b = 0, 1

        for i in range(len(active_clusters)):
            for j in range(i + 1, len(active_clusters)):
                d = _compute_cluster_distance(
                    active_clusters[i], active_clusters[j], dist_matrix, method
                )
                if d < min_dist:
                    min_dist = d
                    idx_a, idx_b = i, j

        c_a = active_clusters[idx_a]
        c_b = active_clusters[idx_b]
        merged_elements = c_a.elements + c_b.elements
        merged = _Cluster(id=next_cluster_id, label=f"C{step}", elements=merged_elements)

        history.append(
            ClusterStepDto(
                step=step,
                clusterAId=c_a.id,
                clusterBId=c_b.id,
                clusterALabel=c_a.label,
                clusterBLabel=c_b.label,
                newClusterId=merged.id,
                newClusterLabel=merged.label,
                distance=_round6(min_dist),
                size=len(merged_elements),
            )
        )

        active_clusters.pop(idx_b)
        active_clusters.pop(idx_a)
        active_clusters.append(merged)

        next_cluster_id += 1
        step += 1

    return ClusteringResultDto(
        distanceMatrix=matrix_list,
        sortedRelations=sorted_relations,
        clusteringHistory=history,
    )