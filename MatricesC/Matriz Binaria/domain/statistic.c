#include "headers/statistic.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <float.h>

static double **computeDistanceMatrix(const BinaryMatrix *m, DistanceMethod method) {
    int n = m->rows;
    double **dist = (double **)malloc(n * sizeof(double *));
    for (int i = 0; i < n; i++) {
        dist[i] = (double *)calloc(n, sizeof(double));
    }

    for (int i = 0; i < n; i++) {
        dist[i][i] = 0.0;
        for (int j = i + 1; j < n; j++) {
            int a = 0, b = 0, c = 0, d = 0;
            for (int k = 0; k < m->cols; k++) {
                int v1 = m->data[i][k];
                int v2 = m->data[j][k];
                if (v1 == 1 && v2 == 1) a++;
                else if (v1 == 1 && v2 == 0) b++;
                else if (v1 == 0 && v2 == 1) c++;
                else d++;
            }

            double similarity = 0.0;
            if (method == DIST_JACCARD) {
                int denom = a + b + c;
                similarity = (denom == 0) ? 1.0 : (double)a / (double)denom;
            } else { // DIST_SOKAL (Sokal-Michener)
                int denom = a + b + c + d;
                similarity = (denom == 0) ? 1.0 : (double)(a + d) / (double)denom;
            }

            double distance = 1.0 - similarity;
            dist[i][j] = distance;
            dist[j][i] = distance;
        }
    }
    return dist;
}

static void freeDistanceMatrix(double **matrix, int n) {
    if (!matrix) return;
    for (int i = 0; i < n; i++) {
        free(matrix[i]);
    }
    free(matrix);
}

static int compareRelations(const void *p1, const void *p2) {
    const Relation *r1 = (const Relation *)p1;
    const Relation *r2 = (const Relation *)p2;
    if (r1->distance < r2->distance) return -1;
    if (r1->distance > r2->distance) return 1;
    if (r1->elementA != r2->elementA) return r1->elementA - r2->elementA;
    return r1->elementB - r2->elementB;
}

static Relation *computeSortedRelations(double **distMatrix, int n, int *totalPairs) {
    *totalPairs = (n * (n - 1)) / 2;
    Relation *relations = (Relation *)malloc((*totalPairs) * sizeof(Relation));

    int idx = 0;
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            relations[idx].elementA = i + 1;
            relations[idx].elementB = j + 1;
            relations[idx].distance = distMatrix[i][j];
            idx++;
        }
    }

    qsort(relations, *totalPairs, sizeof(Relation), compareRelations);
    return relations;
}

static ClusterStep *computeClustering(double **distMatrix, int n, LinkageMethod linkage, int *totalSteps) {
    *totalSteps = n - 1;
    ClusterStep *steps = (ClusterStep *)malloc((*totalSteps) * sizeof(ClusterStep));

    double **work = (double **)malloc(n * sizeof(double *));
    for (int i = 0; i < n; i++) {
        work[i] = (double *)malloc(n * sizeof(double));
        memcpy(work[i], distMatrix[i], n * sizeof(double));
    }

    int *active = (int *)malloc(n * sizeof(int));
    int *clusterId = (int *)malloc(n * sizeof(int));
    int *clusterSize = (int *)malloc(n * sizeof(int));
    char (*clusterLabel)[16] = malloc(n * sizeof(*clusterLabel));

    for (int i = 0; i < n; i++) {
        active[i] = 1;
        clusterId[i] = i;
        clusterSize[i] = 1;
        snprintf(clusterLabel[i], 16, "E%d", i + 1);
    }

    for (int s = 0; s < n - 1; s++) {
        int u = -1, v = -1;
        double minDist = DBL_MAX;

        for (int i = 0; i < n; i++) {
            if (!active[i]) continue;
            for (int j = i + 1; j < n; j++) {
                if (!active[j]) continue;
                if (work[i][j] < minDist) {
                    minDist = work[i][j];
                    u = i;
                    v = j;
                }
            }
        }

        steps[s].step = s + 1;
        steps[s].clusterAId = clusterId[u];
        steps[s].clusterBId = clusterId[v];
        strcpy(steps[s].clusterALabel, clusterLabel[u]);
        strcpy(steps[s].clusterBLabel, clusterLabel[v]);
        steps[s].newClusterId = n + s;
        snprintf(steps[s].newClusterLabel, 16, "C%d", s + 1);
        steps[s].distance = minDist;
        steps[s].size = clusterSize[u] + clusterSize[v];

        for (int k = 0; k < n; k++) {
            if (!active[k] || k == u || k == v) continue;
            double dUK = work[u][k];
            double dVK = work[v][k];
            double newDist = 0.0;

            if (linkage == LINK_VMC) {
                newDist = (dUK < dVK) ? dUK : dVK;
            } else if (linkage == LINK_VML) {
                newDist = (dUK > dVK) ? dUK : dVK;
            } else {
                newDist = (clusterSize[u] * dUK + clusterSize[v] * dVK) / (double)(clusterSize[u] + clusterSize[v]);
            }

            work[u][k] = newDist;
            work[k][u] = newDist;
        }

        clusterId[u] = n + s;
        clusterSize[u] += clusterSize[v];
        snprintf(clusterLabel[u], 16, "C%d", s + 1);
        active[v] = 0;
    }

    freeDistanceMatrix(work, n);
    free(active);
    free(clusterId);
    free(clusterSize);
    free(clusterLabel);

    return steps;
}

MethodResult computeMethodResult(const BinaryMatrix *m, DistanceMethod distMethod, LinkageMethod linkMethod) {
    MethodResult res;
    res.distMatrix = computeDistanceMatrix(m, distMethod);
    res.relations = computeSortedRelations(res.distMatrix, m->rows, &res.totalPairs);
    res.history = computeClustering(res.distMatrix, m->rows, linkMethod, &res.totalSteps);
    return res;
}

void freeMethodResult(MethodResult *res, int n) {
    if (!res) return;
    free(res->history);
    free(res->relations);
    freeDistanceMatrix(res->distMatrix, n);
    res->history = NULL;
    res->relations = NULL;
    res->distMatrix = NULL;
}

static void printMethodResultBlock(const MethodResult *res, int n) {
    printf("{\n");

    // e) Matriz de distancias
    printf("    \"distanceMatrix\": [\n");
    for (int i = 0; i < n; i++) {
        printf("      [");
        for (int j = 0; j < n; j++) {
            printf("%.6f%s", res->distMatrix[i][j], (j < n - 1) ? ", " : "");
        }
        printf("]%s\n", (i < n - 1) ? "," : "");
    }
    printf("    ],\n");

    // g) Lista de relaciones de menor a mayor distancia
    printf("    \"sortedRelations\": [\n");
    for (int i = 0; i < res->totalPairs; i++) {
        printf("      {\"elementA\": %d, \"elementB\": %d, \"distance\": %.6f}%s\n",
               res->relations[i].elementA, res->relations[i].elementB, res->relations[i].distance,
               (i < res->totalPairs - 1) ? "," : "");
    }
    printf("    ],\n");

    // f) y h) Historial y datos para el dendrograma
    printf("    \"clusteringHistory\": [\n");
    for (int i = 0; i < res->totalSteps; i++) {
        printf("      {\"step\": %d, \"clusterAId\": %d, \"clusterBId\": %d, \"clusterALabel\": \"%s\", \"clusterBLabel\": \"%s\", \"newClusterId\": %d, \"newClusterLabel\": \"%s\", \"distance\": %.6f, \"size\": %d}%s\n",
               res->history[i].step,
               res->history[i].clusterAId, res->history[i].clusterBId,
               res->history[i].clusterALabel, res->history[i].clusterBLabel,
               res->history[i].newClusterId, res->history[i].newClusterLabel,
               res->history[i].distance, res->history[i].size,
               (i < res->totalSteps - 1) ? "," : "");
    }
    printf("    ]\n");
    printf("  }");
}

void printDualResultJson(
    const char *linkageMethodName,
    const MethodResult *jaccard,
    const MethodResult *sokal,
    int n
) {
    printf("{\n");
    printf("  \"linkageMethod\": \"%s\",\n", linkageMethodName);
    printf("  \"jaccard\": ");
    printMethodResultBlock(jaccard, n);
    printf(",\n");
    printf("  \"sokal\": ");
    printMethodResultBlock(sokal, n);
    printf("\n}\n");
}