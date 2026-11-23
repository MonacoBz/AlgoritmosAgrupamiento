package org.uaemex.gatewayservice.validations.validationtype.matrix.mahalanobis;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class MoreRowsThanColsValidation implements ValidationType {
    @Override
    public void doValidation(Matrix m) {
        if (m.getRowsNum() > m.getQuantitativeData().size())throw new ValidationException("Si $n \\le p$ o hay una columna constante, la matriz de covarianza es singular (determinante 0) y Gauss-Jordan no puede invertirla.");

    }
}
