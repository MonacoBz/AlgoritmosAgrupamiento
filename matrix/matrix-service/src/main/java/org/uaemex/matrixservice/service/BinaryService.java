package org.uaemex.matrixservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.uaemex.matrixservice.dto.JaccardAndSockalResponse;
import org.uaemex.matrixservice.dto.MatrixRequestDto;
import org.uaemex.matrixservice.executor.CExecutor;
import org.uaemex.matrixservice.model.Matrix;
import org.uaemex.matrixservice.model.datatype.BinaryData;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class BinaryService {

    private final CExecutor cExecutor;
    private final ObjectMapper objectMapper;

    public BinaryService(
            CExecutor cExecutor,
            ObjectMapper objectMapper
    ){
        this.cExecutor = cExecutor;
        this.objectMapper = objectMapper;
    }

    @Value("${c.binary.path}")
    private String binaryPath;

    public JaccardAndSockalResponse doJaccardAndSockal(MatrixRequestDto request) {
        if (request == null || request.m() == null || request.method() == null) {
            throw new IllegalArgumentException("La matriz y el método de eslabonamiento son obligatorios.");
        }

        String stdinInput = formatBinaryMatrix(request.m());
        String linkageArg = request.method().name().toLowerCase();

        String jsonOutput = cExecutor.execute(binaryPath, List.of(linkageArg), stdinInput);

        try {
            return objectMapper.readValue(jsonOutput, JaccardAndSockalResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al deserializar la respuesta de C: " + e.getMessage(), e);
        }
    }

    private String formatBinaryMatrix(Matrix matrix) {
        List<BinaryData> binaryCols = matrix.getBinaryData();
        if (binaryCols == null || binaryCols.isEmpty()) {
            throw new IllegalArgumentException("La matriz no contiene columnas binarias.");
        }

        int rows = matrix.getRowsNum();
        int cols = binaryCols.size();

        StringBuilder sb = new StringBuilder();
        sb.append(rows).append(" ").append(cols).append("\n");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(binaryCols.get(j).getData().get(i));
                if (j < cols - 1) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
