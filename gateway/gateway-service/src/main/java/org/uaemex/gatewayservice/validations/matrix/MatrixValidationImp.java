package org.uaemex.gatewayservice.validations.matrix;

import org.springframework.stereotype.Component;
import org.uaemex.gatewayservice.dto.MatrixRequestDto;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;
import org.uaemex.gatewayservice.validations.validationtype.matrix.binary.BinaryDataNotNullValidation;
import org.uaemex.gatewayservice.validations.validationtype.matrix.binary.OnlyOnesAndZeros;
import org.uaemex.gatewayservice.validations.validationtype.matrix.binary.QualitativeAndQuantitativeNullValidation;
import org.uaemex.gatewayservice.validations.validationtype.matrix.core_exceptions.ColumnLengthConsistencyValidation;
import org.uaemex.gatewayservice.validations.validationtype.matrix.core_exceptions.MinRowsValidation;
import org.uaemex.gatewayservice.validations.validationtype.matrix.core_exceptions.NotNullMatrixValidation;
import org.uaemex.gatewayservice.validations.validationtype.matrix.euclidean.BinaryDataAndaQualitativeDataNullValidation;
import org.uaemex.gatewayservice.validations.validationtype.matrix.euclidean.QuantitativaDataNotNull;
import org.uaemex.gatewayservice.validations.validationtype.matrix.gower.AnyRowHaveDataValidation;
import org.uaemex.gatewayservice.validations.validationtype.matrix.mahalanobis.MoreRowsThanColsValidation;
import org.uaemex.gatewayservice.validations.validationtype.matrix.mahalanobis.NonZeroVarianceValidation;

import java.util.List;

@Component
public class MatrixValidationImp implements MatrixValidation {

    private final List<ValidationType> binaryValidations = List.of(
            new NotNullMatrixValidation(),
            new MinRowsValidation(),
            new ColumnLengthConsistencyValidation(),
            new BinaryDataNotNullValidation(),
            new QualitativeAndQuantitativeNullValidation(),
            new OnlyOnesAndZeros()
    );

    private final List<ValidationType> gowerValidations = List.of(
            new NotNullMatrixValidation(),
            new MinRowsValidation(),
            new ColumnLengthConsistencyValidation(),
            new AnyRowHaveDataValidation()
    );

    private final List<ValidationType> euclideanValidations = List.of(
            new NotNullMatrixValidation(),
            new MinRowsValidation(),
            new ColumnLengthConsistencyValidation(),
            new BinaryDataAndaQualitativeDataNullValidation(),
            new QuantitativaDataNotNull()
    );
    private final List<ValidationType> mahalanobisValidations = List.of(
            new NotNullMatrixValidation(),
            new MinRowsValidation(),
            new ColumnLengthConsistencyValidation(),
            new BinaryDataAndaQualitativeDataNullValidation(),
            new QuantitativaDataNotNull(),
            new MoreRowsThanColsValidation(),
            new NonZeroVarianceValidation()
    );

    @Override
    public void validateBinary(MatrixRequestDto request) {
        binaryValidations.forEach(validation -> validation.doValidation(request.m()));
    }

    @Override
    public void validateGower(MatrixRequestDto request) {
        gowerValidations.forEach(validation -> validation.doValidation(request.m()));
    }

    @Override
    public void validateEuclidean(MatrixRequestDto request) {
        euclideanValidations.forEach(validation -> validation.doValidation(request.m()));
    }

    @Override
    public void validateMahalanobis(MatrixRequestDto request) {
        mahalanobisValidations.forEach(validation -> validation.doValidation(request.m()));
    }
}
