package org.uaemex.matrixservice.domain.formula;


import org.springframework.stereotype.Component;
import org.uaemex.matrixservice.dto.GowerResult;
import org.uaemex.matrixservice.model.Matrix;
import org.uaemex.matrixservice.model.datatype.BinaryData;
import org.uaemex.matrixservice.model.datatype.QualitativeData;
import org.uaemex.matrixservice.model.datatype.QuantitativeData;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

@Component
public class Gower implements IFormula {


    @Override
    public List<GowerResult> doFormula(Matrix m) {
        List<Double> gValues = new ArrayList<>();
        calculateGsValues(m.getQuantitativeData(),gValues);
        return calculateGower(m,gValues);
    }

    private void calculateGsValues(List<QuantitativeData> qValue, List<Double> gValues) {
        qValue.forEach(quantitative -> {
            Double max = quantitative.getData().stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .max().orElse(1.0);

            Double min = quantitative.getData().stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .min().orElse(0.0);

            double g = max - min;

            gValues.add(g == 0 ? 1.0 : g);
        });
    }

    private List<GowerResult> calculateGower(Matrix m, List<Double> gValues) {
        List<QuantitativeData> quantitativeData = m.getQuantitativeData();
        List<QualitativeData> qualitativeData = m.getQualitativeData();
        List<BinaryData> binaryData = m.getBinaryData();
        List<GowerResult> response = new ArrayList<>();

        for (int i = 0; i < m.getRowsNum(); i++) {
            for (int j = i + 1; j < m.getRowsNum(); j++) {

                double[] quantResult = weightedSum(quantitativeData, i, j, gValues);
                double wSum = quantResult[0];
                double p1 = quantResult[1];

                double[] qualResult = calculateAlpha(qualitativeData, i, j);
                double alpha = qualResult[0];
                double p3 = qualResult[1];

                double[] binResult = calculateBinaryData(binaryData, i, j);
                double a = binResult[0];
                double d = binResult[1];
                double p2 = binResult[2];

                double denominador = p1 + (p2 - d) + p3;

                double pSimilarity = (denominador == 0) ? 0 : (wSum + a + alpha) / denominador;
                double distance = Math.sqrt(Math.max(0.0, 1.0 - pSimilarity));

                response.add(new GowerResult(j + 1, i + 1, pSimilarity, distance));
            }
        }
        return response;
    }

    private double[] calculateBinaryData(List<BinaryData> binaries, int i, int j) {
        double a = 0, d = 0, weight = 0;
        for (BinaryData b : binaries) {
            Integer v1 = b.getData().get(i);
            Integer v2 = b.getData().get(j);
            if (v1 != null && v2 != null) {
                weight++;
                if (v1.equals(1) && v2.equals(1)) a++;
                if (v1.equals(0) && v2.equals(0)) d++;
            }
        }
        return new double[]{a, d, weight};
    }

    private double[] calculateAlpha(List<QualitativeData> qualitativeData, int i, int j) {
        double similitud = 0;
        double weight = 0;
        for (QualitativeData q : qualitativeData) {
            Double v1 = q.getData().get(i);
            Double v2 = q.getData().get(j);
            if (v1 != null && v2 != null) {
                weight++;
                if (v1.equals(v2)) similitud++;
            }
        }
        return new double[]{similitud, weight};
    }

    private double[] weightedSum(List<QuantitativeData> quantitativeData, int i, int j, List<Double> gValues) {
        double similitud = 0.0;
        double weight = 0.0;
        int index = 0;
        for (QuantitativeData numsL : quantitativeData) {
            Double v1 = numsL.getData().get(i);
            Double v2 = numsL.getData().get(j);
            if (v1 != null && v2 != null) {
                weight++;
                similitud += 1 - (Math.abs(v1 - v2) / gValues.get(index));
            }
            index++;
        }
        return new double[]{similitud, weight};
    }
}