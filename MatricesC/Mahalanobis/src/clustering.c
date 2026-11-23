#include "clustering.h"
#include <float.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct {
    int id;
    char label[16];
    int* elements;
    int size;
} Cluster;

static double round6(double val) {
    return round(val * 1000000.0) / 1000000.0;
}

static int compare_relations(const void* a, const void* b) {
    const Relation* ra = (const Relation*)a;
    const Relation* rb = (const Relation*)b;
    if (ra->distance < rb->distance) return -1;
    if (ra->distance > rb->distance) return 1;
    return 0;
}

static const char* method_to_string(LinkageMethod method) {
    switch (method) {
        case VMC: return "VMC";
        case VML: return "VML";
        default:  return "PROMEDIO";
    }
}

static double compute_cluster_distance(Cluster* a, Cluster* b, double** dist_matrix, LinkageMethod method) {
    if (method == VMC) {
        double min_d = DBL_MAX;
        for (int i = 0; i < a->size; i++) {
            for (int j = 0; j < b->size; j++) {
                double d = dist_matrix[a->elements[i]][b->elements[j]];
                if (d < min_d) min_d = d;
            }
        }
        return min_d;
    } else if (method == VML) {
        double max_d = -1.0;
        for (int i = 0; i < a->size; i++) {
            for (int j = 0; j < b->size; j++) {
                double d = dist_matrix[a->elements[i]][b->elements[j]];
                if (d > max_d) max_d = d;
            }
        }
        return max_d;
    } else {
        double sum = 0.0;
        for (int i = 0; i < a->size; i++) {
            for (int j = 0; j < b->size; j++) {
                sum += dist_matrix[a->elements[i]][b->elements[j]];
            }
        }
        return sum / (a->size * b->size);
    }
}

void run_clustering_and_print_json(double** dist_matrix, int n, LinkageMethod method) {
    // 1. Relaciones ordenadas
    int num_rel = n * (n - 1) / 2;
    Relation* relations = (Relation*)malloc(num_rel * sizeof(Relation));
    int idx = 0;
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            relations[idx].elementA = i + 1;
            relations[idx].elementB = j + 1;
            relations[idx].distance = round6(dist_matrix[i][j]);
            idx++;
        }
    }
    qsort(relations, num_rel, sizeof(Relation), compare_relations);

    // 2. Agrupamiento jerárquico
    Cluster* active = (Cluster*)malloc(n * sizeof(Cluster));
    int active_count = n;
    for (int i = 0; i < n; i++) {
        active[i].id = i;
        snprintf(active[i].label, sizeof(active[i].label), "E%d", i + 1);
        active[i].elements = (int*)malloc(sizeof(int));
        active[i].elements[0] = i;
        active[i].size = 1;
    }

    int total_steps = n - 1;
    ClusterStep* history = (ClusterStep*)malloc(total_steps * sizeof(ClusterStep));
    int next_id = n;

    for (int step = 1; step <= total_steps; step++) {
        double min_dist = DBL_MAX;
        int idxA = 0, idxB = 1;

        for (int i = 0; i < active_count; i++) {
            for (int j = i + 1; j < active_count; j++) {
                double d = compute_cluster_distance(&active[i], &active[j], dist_matrix, method);
                if (d < min_dist) {
                    min_dist = d;
                    idxA = i;
                    idxB = j;
                }
            }
        }

        Cluster cA = active[idxA];
        Cluster cB = active[idxB];

        Cluster merged;
        merged.id = next_id++;
        snprintf(merged.label, sizeof(merged.label), "C%d", step);
        merged.size = cA.size + cB.size;
        merged.elements = (int*)malloc(merged.size * sizeof(int));
        memcpy(merged.elements, cA.elements, cA.size * sizeof(int));
        memcpy(merged.elements + cA.size, cB.elements, cB.size * sizeof(int));

        ClusterStep* h = &history[step - 1];
        h->step = step;
        h->clusterAId = cA.id;
        h->clusterBId = cB.id;
        strcpy(h->clusterALabel, cA.label);
        strcpy(h->clusterBLabel, cB.label);
        h->newClusterId = merged.id;
        strcpy(h->newClusterLabel, merged.label);
        h->distance = round6(min_dist);
        h->size = merged.size;

        free(cA.elements);
        free(cB.elements);

        // Compactar arreglo eliminando idxB y luego idxA
        for (int k = idxB; k < active_count - 1; k++) active[k] = active[k + 1];
        active_count--;
        for (int k = idxA; k < active_count - 1; k++) active[k] = active[k + 1];
        active_count--;

        active[active_count++] = merged;
    }

    // 3. Imprimir JSON (SingleClusteringResponse)
    printf("{\n");
    printf("  \"linkageMethod\": \"%s\",\n", method_to_string(method));
    printf("  \"result\": {\n");

    // distanceMatrix
    printf("    \"distanceMatrix\": [\n");
    for (int i = 0; i < n; i++) {
        printf("      [");
        for (int j = 0; j < n; j++) {
            printf("%.6f%s", round6(dist_matrix[i][j]), (j < n - 1) ? ", " : "");
        }
        printf("]%s\n", (i < n - 1) ? "," : "");
    }
    printf("    ],\n");

    // sortedRelations
    printf("    \"sortedRelations\": [\n");
    for (int i = 0; i < num_rel; i++) {
        printf("      {\"elementA\": %d, \"elementB\": %d, \"distance\": %.6f}%s\n",
               relations[i].elementA, relations[i].elementB, relations[i].distance,
               (i < num_rel - 1) ? "," : "");
    }
    printf("    ],\n");

    // clusteringHistory
    printf("    \"clusteringHistory\": [\n");
    for (int i = 0; i < total_steps; i++) {
        ClusterStep* h = &history[i];
        printf("      {\"step\": %d, \"clusterAId\": %d, \"clusterBId\": %d, "
               "\"clusterALabel\": \"%s\", \"clusterBLabel\": \"%s\", "
               "\"newClusterId\": %d, \"newClusterLabel\": \"%s\", "
               "\"distance\": %.6f, \"size\": %d}%s\n",
               h->step, h->clusterAId, h->clusterBId,
               h->clusterALabel, h->clusterBLabel,
               h->newClusterId, h->newClusterLabel,
               h->distance, h->size,
               (i < total_steps - 1) ? "," : "");
    }
    printf("    ]\n");

    printf("  }\n");
    printf("}\n");

    // Liberar memoria restante
    for (int i = 0; i < active_count; i++) free(active[i].elements);
    free(active);
    free(relations);
    free(history);
}