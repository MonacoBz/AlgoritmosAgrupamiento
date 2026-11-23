package org.uaemex.gatewayservice.validations.validationtype.matrix.gower;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class AnyRowHaveDataValidation implements ValidationType {

    @Override
    public void doValidation(Matrix m) {
        int count = 3;
        if (m.getQualitativeData() == null || m.getQualitativeData().isEmpty())count--;
        if (m.getQuantitativeData() == null || m.getQuantitativeData().isEmpty())count--;
        if (m.getBinaryData() == null || m.getBinaryData().isEmpty())count--;

        if (count == 0)throw new ValidationException("Se necesita que almenos una columa tenga datos para poder usar gower");
    }
}
