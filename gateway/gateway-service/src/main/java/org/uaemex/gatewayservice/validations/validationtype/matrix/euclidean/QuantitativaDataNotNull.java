package org.uaemex.gatewayservice.validations.validationtype.matrix.euclidean;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class QuantitativaDataNotNull implements ValidationType {
    @Override
    public void doValidation(Matrix m) {
        if (m.getQuantitativeData() == null || m.getQuantitativeData().isEmpty())throw new ValidationException("Para poder usar euclidiana, la matriz debe contener datos cuantitativos");
    }
}
