package org.uaemex.gatewayservice.service;

import org.springframework.stereotype.Service;
import org.uaemex.gatewayservice.client.MatrixClient;
import org.uaemex.gatewayservice.dto.MatrixRequestDto;
import org.uaemex.gatewayservice.dto.MatrixRequestTypeDto;
import org.uaemex.gatewayservice.dto.MatrixResponseDto;

@Service
public class MatrixService {

    private final MatrixClient client;

    public MatrixService(MatrixClient client){
        this.client = client;
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

    private MatrixResponseDto jaccardAndSockal(MatrixRequestDto m){
        return new MatrixResponseDto(client.doJaccardAndSockal(m),null);
    }

    private MatrixResponseDto gowel(MatrixRequestDto request){
        return new MatrixResponseDto(null,client.doGowel(request));
    }

    private MatrixResponseDto euclidean(MatrixRequestDto request){
        return new MatrixResponseDto(null,client.doEuclidean(request));
    }

    private MatrixResponseDto mahalanobis(MatrixRequestDto request){
        return new MatrixResponseDto(null,client.doMahalanobis(request));
    }
}
