package org.uaemex.gatewayservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.uaemex.gatewayservice.client.CleanerClient;
import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.dto.MatrixReport;

@Service
public class CleanerService {

    private final CleanerClient client;

    public CleanerService(
            CleanerClient client
    ){
        this.client = client;
    }

    public MatrixReport prepareCleanerClient(MultipartFile file){
        return client.consumeClient(file);
    }

    public MatrixReport cleanCleanerClient(Matrix m){
        return client.consumeClient(m);
    }
}
