package org.uaemex.gatewayservice.validations.matrix;

import org.uaemex.gatewayservice.dto.MatrixRequestDto;

public interface MatrixValidation {

    void validateBinary(MatrixRequestDto request);

    void validateGower(MatrixRequestDto request);

    void validateEuclidean(MatrixRequestDto request);

    void validateMahalanobis(MatrixRequestDto request);
}
