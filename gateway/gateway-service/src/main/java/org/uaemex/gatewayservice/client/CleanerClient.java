package org.uaemex.gatewayservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.dto.MatrixReport;

@Component
public class CleanerClient {

    private final RestClient restClient;

    public CleanerClient(RestClient.Builder builder, @Value("${services.cleaner.url}") String cleanerUrl) {
        this.restClient = builder.baseUrl(cleanerUrl).build();
    }

    public MatrixReport consumeClient(MultipartFile file){
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("File", file.getResource());

        return restClient.post()
                .uri("/prepare")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(MatrixReport.class);
    }

    public MatrixReport consumeClient(Matrix matrix){
        return restClient.post()
                .uri("/clean")
                .contentType(MediaType.APPLICATION_JSON)
                .body(matrix)
                .retrieve()
                .body(MatrixReport.class);
    }
}
