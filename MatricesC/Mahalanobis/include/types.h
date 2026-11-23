#ifndef TYPES_H
#define TYPES_H

typedef enum {
    VMC,
    VML,
    PROMEDIO
} LinkageMethod;

typedef struct {
    int elementA;
    int elementB;
    double distance;
} Relation;

typedef struct {
    int step;
    int clusterAId;
    int clusterBId;
    char clusterALabel[16];
    char clusterBLabel[16];
    int newClusterId;
    char newClusterLabel[16];
    double distance;
    int size;
} ClusterStep;

#endif