package org.uaemex.matrixservice.service;

import org.springframework.stereotype.Service;
import org.uaemex.matrixservice.client.PythonClient;
import org.uaemex.matrixservice.dto.MatrixRequestDto;
import org.uaemex.matrixservice.dto.SingleClusteringResponse;

@Service
public class EuclideanService {

    private final PythonClient client;

    public EuclideanService(PythonClient client){
        this.client = client;
    }

    public SingleClusteringResponse doEuclidean(MatrixRequestDto request){
        return client.consumeClient(request);
    }
}
