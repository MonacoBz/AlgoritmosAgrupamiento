package org.uaemex.gatewayservice.validations.validationtype.matrix.binary;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

public class OnlyOnesAndZeros implements ValidationType {
    @Override
    public void doValidation(Matrix m) {
        m.getBinaryData().forEach(bData ->{
            bData.getData().forEach(n -> {
                if (!(n >= 0 && n <= 1))throw new ValidationException("La matriz binaria debe contener solo 0's y 1's");
            });
        });
    }
}
