package org.uaemex.gatewayservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.uaemex.gatewayservice.dto.GowelResponseDto;
import org.uaemex.gatewayservice.dto.JaccardAndSockalResponse;
import org.uaemex.gatewayservice.dto.MatrixRequestDto;
import org.uaemex.gatewayservice.dto.SingleClusteringResponse;

@Component
public class MatrixClient {

    private final RestClient restClient;

    public MatrixClient(RestClient.Builder builder,@Value("${services.matrix.url}") String matrixUrl){
        this.restClient = builder.baseUrl(matrixUrl).build();
    }

    public JaccardAndSockalResponse doJaccardAndSockal(MatrixRequestDto request) {
        return restClient.post()
                .uri("/binary")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(JaccardAndSockalResponse.class);
    }

    public SingleClusteringResponse doGowel(MatrixRequestDto request){
        return restClient.post()
                .uri("/gowel")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SingleClusteringResponse.class);
    }

    public SingleClusteringResponse doEuclidean(MatrixRequestDto request){
        return restClient.post()
                .uri("/euclidean")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SingleClusteringResponse.class);
    }

    public SingleClusteringResponse doMahalanobis(MatrixRequestDto request){
        return restClient.post()
                .uri("/mahalanobis")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SingleClusteringResponse.class);
    }
}
