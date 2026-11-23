package org.uaemex.matrixservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.uaemex.matrixservice.dto.MatrixRequestDto;
import org.uaemex.matrixservice.dto.SingleClusteringResponse;

@Component
public class PythonClient {

    private final RestClient restClient;

    public PythonClient(RestClient.Builder builder,@Value("${python.euclidean.path}") String url){
        this.restClient = builder.baseUrl(url).build();
    }

    public SingleClusteringResponse consumeClient(MatrixRequestDto request){
        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SingleClusteringResponse.class);
    }
}
