package org.uaemex.gatewayservice.validations.validationtype.matrix.core_exceptions;

import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.exceptions.ValidationException;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

import java.util.List;
import java.util.Objects;

public class ColumnLengthConsistencyValidation implements ValidationType {

    @Override
    public void doValidation(Matrix m) {
        if (m.getBinaryData() != null) {
            m.getBinaryData().forEach(d -> validateColumn(d.getData(), m.getRowsNum(), "binarias"));
        }
        if (m.getQuantitativeData() != null) {
            m.getQuantitativeData().forEach(d -> validateColumn(d.getData(), m.getRowsNum(), "cuantitativas"));
        }
        if (m.getQualitativeData() != null) {
            m.getQualitativeData().forEach(d -> validateColumn(d.getData(), m.getRowsNum(), "cualitativas"));
        }
    }

    private void validateColumn(List<?> nums, int expectedRows, String type) {
        if (nums == null || nums.size() != expectedRows) {
            throw new ValidationException(
                    "Las filas de las variables " + type + " no coinciden con las filas detectadas originalmente.");
        }
        if (nums.stream().anyMatch(Objects::isNull)) {
            throw new ValidationException("Se encontró un valor nulo en las variables " + type + ".");
        }
    }
}
