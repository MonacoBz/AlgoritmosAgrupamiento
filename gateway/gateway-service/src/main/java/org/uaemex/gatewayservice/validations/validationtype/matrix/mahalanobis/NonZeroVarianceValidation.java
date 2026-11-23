package org.uaemex.gatewayservice.validations.validationtype.matrix.mahalanobis;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.domain.datatype.QuantitativeData;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

import java.util.List;

public class NonZeroVarianceValidation implements ValidationType {

    private static final double EPSILON = 1e-12;

    @Override
    public void doValidation(Matrix m) {
        List<QuantitativeData> quantData = m.getQuantitativeData();

        for (int col = 0; col < quantData.size(); col++) {
            List<Double> values = quantData.get(col).getData();
            double first = values.get(0);

            boolean allEqual = values.stream()
                    .allMatch(v -> Math.abs(v - first) < EPSILON);

            if (allEqual) {
                throw new ValidationException(
                        "La variable cuantitativa en la columna " + (col + 1) +
                                " es constante (varianza cero); la matriz de covarianza no es invertible para Mahalanobis.");
            }
        }
    }
}
