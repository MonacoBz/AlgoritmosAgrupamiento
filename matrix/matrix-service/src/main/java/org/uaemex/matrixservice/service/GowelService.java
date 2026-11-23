package org.uaemex.matrixservice.service;

import org.springframework.stereotype.Service;
import org.uaemex.matrixservice.domain.HierarchicalClusteringEngine;
import org.uaemex.matrixservice.domain.formula.IFormula;
import org.uaemex.matrixservice.dto.ClusteringResultDto;
import org.uaemex.matrixservice.dto.GowerResult;
import org.uaemex.matrixservice.dto.MatrixRequestDto;
import org.uaemex.matrixservice.dto.SingleClusteringResponse;
import org.uaemex.matrixservice.model.Matrix;

import java.util.List;

@Service
public class GowelService {

    private final IFormula formula;
    private final HierarchicalClusteringEngine clustering;

    public GowelService(
            IFormula formula,
            HierarchicalClusteringEngine clustering){
        this.formula = formula;
        this.clustering = clustering;
    }


    public SingleClusteringResponse doGowel(MatrixRequestDto request){
        Matrix matrix = request.m();
        int n = matrix.getRowsNum();
        List<GowerResult> pairs = formula.doFormula(matrix);

        double[][] distMatrix = new double[n][n];
        for (GowerResult r : pairs) {
            int row = r.rowIndexJ() - 1;
            int col = r.rowIndexI() - 1;
            double dist = r.distance();

            distMatrix[row][col] = dist;
            distMatrix[col][row] = dist;
        }

        ClusteringResultDto result = clustering.buildResult(distMatrix, request.method());
        return new SingleClusteringResponse(request.method().name(), result, pairs);
    }


}
