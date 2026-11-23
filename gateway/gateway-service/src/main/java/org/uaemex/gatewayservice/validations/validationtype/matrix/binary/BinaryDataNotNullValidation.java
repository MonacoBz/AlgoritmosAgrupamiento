package org.uaemex.gatewayservice.validations.validationtype.matrix.binary;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class BinaryDataNotNullValidation implements ValidationType {
    @Override
    public void doValidation(Matrix m) {
        if (m.getBinaryData() == null || m.getBinaryData().isEmpty())throw new ValidationException("La matriz no cuenta con datos binarios");
    }
}
