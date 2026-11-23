package org.uaemex.gatewayservice.validations.validationtype.matrix.binary;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class QualitativeAndQuantitativeNullValidation implements ValidationType {


    @Override
    public void doValidation(Matrix m) {
        if (m.getQualitativeData() != null || !m.getQualitativeData().isEmpty())throw new ValidationException("La matriz cuenta con datos cualitativos, recuerda que para usar la solución binaria, solo necesitamos datos binarios");
        if (m.getQuantitativeData() != null || !m.getQuantitativeData().isEmpty())throw new ValidationException("La matriz cuenta con datos cuantitaviso, recuerda que para usar la solución binaria, solo necesitamos datos binarios");

    }
}
