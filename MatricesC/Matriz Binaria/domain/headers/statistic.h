#ifndef STATISTIC_H
#define STATISTIC_H

#include "reader.h"

typedef enum {
    DIST_JACCARD,
    DIST_SOKAL
} DistanceMethod;

typedef enum {
    LINK_VMC,
    LINK_VML,
    LINK_PROMEDIO
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

typedef struct {
    double **distMatrix;
    Relation *relations;
    int totalPairs;
    ClusterStep *history;
    int totalSteps;
} MethodResult;

MethodResult computeMethodResult(const BinaryMatrix *m, DistanceMethod distMethod, LinkageMethod linkMethod);
void freeMethodResult(MethodResult *res, int n);

void printDualResultJson(
    const char *linkageMethodName,
    const MethodResult *jaccard,
    const MethodResult *sokal,
    int n
);

#endif