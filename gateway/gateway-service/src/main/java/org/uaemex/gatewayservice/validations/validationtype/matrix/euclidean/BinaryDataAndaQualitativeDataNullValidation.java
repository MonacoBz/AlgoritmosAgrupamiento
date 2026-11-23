package org.uaemex.gatewayservice.validations.validationtype.matrix.euclidean;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class BinaryDataAndaQualitativeDataNullValidation implements ValidationType {
    @Override
    public void doValidation(Matrix m) {
        if(m.getBinaryData() == null || !m.getBinaryData().isEmpty())throw new ValidationException("Para poder usar euclidiana, solo debe tener datos cuantitativos");
        if(m.getQualitativeData() == null || !m.getQualitativeData().isEmpty())throw new ValidationException("Para poder suar euclidiana, solo debe tener datos cuantitativos");

    }
}
