package org.uaemex.gatewayservice.validations.validationtype.matrix.mahalanobis;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class MoreRowsThanColsValidation implements ValidationType {
    @Override
    public void doValidation(Matrix m) {
        if (m.getRowsNum() <= m.getQuantitativeData().size())throw new ValidationException("El número de filas debe ser mayor que el de columnas (n > p); de lo contrario, la matriz de covarianza es singular (determinante 0) y Gauss-Jordan no puede invertirla.");

    }
}
