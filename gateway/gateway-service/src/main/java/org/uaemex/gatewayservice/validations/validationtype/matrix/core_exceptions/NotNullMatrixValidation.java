package org.uaemex.gatewayservice.validations.validationtype.matrix.core_exceptions;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class NotNullMatrixValidation implements ValidationType {
    @Override
    public void doValidation(Matrix m) {
        if (m == null)throw new ValidationException("La matriz esta vacía");
    }
}
