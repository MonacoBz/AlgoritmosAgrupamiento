package org.uaemex.gatewayservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.uaemex.gatewayservice.client.CleanerClient;
import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.dto.MatrixReport;
import org.uaemex.gatewayservice.validations.file.FileValidation;

@Service
public class CleanerService {

    private final CleanerClient client;
    private final FileValidation validations;

    public CleanerService(
            CleanerClient client,
            FileValidation validations
    ){
        this.client = client;
        this.validations = validations;
    }

    public MatrixReport prepareCleanerClient(MultipartFile file){
        validations.validate(file);
        return client.consumeClient(file);
    }

    public MatrixReport cleanCleanerClient(Matrix m){
        return client.consumeClient(m);
    }
}
