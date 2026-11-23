package org.uaemex.gatewayservice.service;

import org.springframework.stereotype.Service;
import org.uaemex.gatewayservice.client.MatrixClient;
import org.uaemex.gatewayservice.dto.MatrixRequestDto;
import org.uaemex.gatewayservice.dto.MatrixRequestTypeDto;
import org.uaemex.gatewayservice.dto.MatrixResponseDto;
import org.uaemex.gatewayservice.validations.matrix.MatrixValidation;

@Service
public class MatrixService {

    private final MatrixClient client;
    private final MatrixValidation validations;

    public MatrixService(
            MatrixClient client,
            MatrixValidation validations){
        this.client = client;
        this.validations = validations;
    }


    public MatrixResponseDto resolveMatrix(MatrixRequestTypeDto m){
        MatrixRequestDto request = new MatrixRequestDto(m.m(),m.method());
        return switch (m.resolver()){
            case JACCARD_SOCKAL -> jaccardAndSockal(request);
            case GOWEL -> gowel(request);
            case EUCLIDEAN -> euclidean(request);
            case MAHALANOBIS -> mahalanobis(request);
        };
    }

    private MatrixResponseDto jaccardAndSockal(MatrixRequestDto request){
        validations.validateBinary(request);
        return new MatrixResponseDto(client.doJaccardAndSockal(request),null);
    }

    private MatrixResponseDto gowel(MatrixRequestDto request){
        validations.validateGower(request);
        return new MatrixResponseDto(null,client.doGowel(request));
    }

    private MatrixResponseDto euclidean(MatrixRequestDto request){
        validations.validateEuclidean(request);
        return new MatrixResponseDto(null,client.doEuclidean(request));
    }

    private MatrixResponseDto mahalanobis(MatrixRequestDto request){
        validations.validateMahalanobis(request);
        return new MatrixResponseDto(null,client.doMahalanobis(request));
    }
}
