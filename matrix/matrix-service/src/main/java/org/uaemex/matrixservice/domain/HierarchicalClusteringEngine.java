package org.uaemex.matrixservice.domain;

import org.springframework.stereotype.Component;
import org.uaemex.matrixservice.dto.ClusterStepDto;
import org.uaemex.matrixservice.dto.ClusteringResultDto;
import org.uaemex.matrixservice.dto.LinkageMethod;
import org.uaemex.matrixservice.dto.RelationDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.uaemex.matrixservice.dto.LinkageMethod.*;

@Component
public class HierarchicalClusteringEngine {

    private static class Cluster {
        int id;
        String label;
        List<Integer> elements;

        Cluster(int id, String label, List<Integer> elements) {
            this.id = id;
            this.label = label;
            this.elements = elements;
        }
    }

    public ClusteringResultDto buildResult(double[][] distMatrix, LinkageMethod method) {
        int n = distMatrix.length;

        // 1. Convertir double[][] a List<List<Double>>
        List<List<Double>> matrixList = new ArrayList<>();
        for (double[] row : distMatrix) {
            List<Double> r = new ArrayList<>();
            for (double val : row) {
                r.add(round6(val));
            }
            matrixList.add(r);
        }

        // 2. Relaciones ordenadas (triángulo superior, 1-based)
        List<RelationDto> sortedRelations = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                sortedRelations.add(new RelationDto(i + 1, j + 1, round6(distMatrix[i][j])));
            }
        }
        sortedRelations.sort(Comparator.comparingDouble(RelationDto::distance));

        // 3. Agrupamiento jerárquico
        List<Cluster> activeClusters = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Integer> elems = new ArrayList<>();
            elems.add(i);
            activeClusters.add(new Cluster(i, "E" + (i + 1), elems));
        }

        List<ClusterStepDto> history = new ArrayList<>();
        int nextClusterId = n;
        int step = 1;

        while (activeClusters.size() > 1) {
            double minDist = Double.MAX_VALUE;
            int idxA = 0, idxB = 1;

            for (int i = 0; i < activeClusters.size(); i++) {
                for (int j = i + 1; j < activeClusters.size(); j++) {
                    double d = computeClusterDistance(activeClusters.get(i), activeClusters.get(j), distMatrix, method);
                    if (d < minDist) {
                        minDist = d;
                        idxA = i;
                        idxB = j;
                    }
                }
            }

            Cluster cA = activeClusters.get(idxA);
            Cluster cB = activeClusters.get(idxB);

            List<Integer> mergedElements = new ArrayList<>(cA.elements);
            mergedElements.addAll(cB.elements);

            String newLabel = "C" + step;
            Cluster merged = new Cluster(nextClusterId, newLabel, mergedElements);

            history.add(new ClusterStepDto(
                    step,
                    cA.id,
                    cB.id,
                    cA.label,
                    cB.label,
                    merged.id,
                    merged.label,
                    round6(minDist),
                    mergedElements.size()
            ));

            // Eliminar primero el índice mayor para no desfasar la lista
            activeClusters.remove(idxB);
            activeClusters.remove(idxA);
            activeClusters.add(merged);

            nextClusterId++;
            step++;
        }

        return new ClusteringResultDto(matrixList, sortedRelations, history);
    }

    private double computeClusterDistance(Cluster a, Cluster b, double[][] distMatrix, LinkageMethod method) {
        return switch (method) {
            case VMC -> {
                double min = Double.MAX_VALUE;
                for (int i : a.elements) {
                    for (int j : b.elements) {
                        min = Math.min(min, distMatrix[i][j]);
                    }
                }
                yield min;
            }
            case VML -> {
                double max = Double.MIN_VALUE;
                for (int i : a.elements) {
                    for (int j : b.elements) {
                        max = Math.max(max, distMatrix[i][j]);
                    }
                }
                yield max;
            }
            case PROMEDIO -> {
                double sum = 0.0;
                for (int i : a.elements) {
                    for (int j : b.elements) {
                        sum += distMatrix[i][j];
                    }
                }
                yield sum / (a.elements.size() * b.elements.size());
            }
        };
    }

    private double round6(double value) {
        return Math.round(value * 1_000_000.0) / 1_000_000.0;
    }
}
