package org.uaemex.gatewayservice.validations.validationtype.matrix.core_exceptions;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class MinRowsValidation implements ValidationType {
    @Override
    public void doValidation(Matrix m) {
        if (m.getRowsNum() < 2)throw new ValidationException("La matriz tiene muy pocas filas para realizar calculos");
    }
}
