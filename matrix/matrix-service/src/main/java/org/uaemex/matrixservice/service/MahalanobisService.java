package org.uaemex.matrixservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.uaemex.matrixservice.dto.MatrixRequestDto;
import org.uaemex.matrixservice.dto.SingleClusteringResponse;
import org.uaemex.matrixservice.executor.CExecutor;
import org.uaemex.matrixservice.model.datatype.QuantitativeData;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Locale;

@Service
public class MahalanobisService {
    private final CExecutor cExecutor;
    private final ObjectMapper objectMapper;
    private final String mahalanobisPath;

    public MahalanobisService(
            CExecutor cExecutor,
            ObjectMapper objectMapper,
            @Value("${c.mahalanobis.path}") String mahalanobisPath) {
        this.cExecutor = cExecutor;
        this.objectMapper = objectMapper;
        this.mahalanobisPath = mahalanobisPath;
    }

    public SingleClusteringResponse doMahalanobis(MatrixRequestDto request) {
        List<QuantitativeData> quantCols = request.m().getQuantitativeData();
        int rows = request.m().getRowsNum();
        int cols = quantCols.size();

        if (cols == 0 || rows <= cols) {
            throw new IllegalArgumentException(
                    "Mahalanobis requiere variables cuantitativas y más filas que columnas (n > p).");
        }

        // 1. Armar el stdin: "<rows> <cols> <METHOD>\n" seguido de la matriz por filas
        StringBuilder sb = new StringBuilder();
        sb.append(rows).append(" ")
                .append(cols).append(" ")
                .append(request.method().name()).append("\n");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Double val = quantCols.get(j).getData().get(i);
                // Locale.US asegura punto decimal (.) y no coma (,)
                sb.append(String.format(Locale.US, "%.6f", val));
                if (j < cols - 1) sb.append(" ");
            }
            sb.append("\n");
        }

        // 2. Ejecutar binario en WSL (sin args extra, solo stdin)
        String jsonOutput = cExecutor.execute(mahalanobisPath, null, sb.toString());

        // 3. Deserializar al mismo DTO unificado
        try {
            return objectMapper.readValue(jsonOutput, SingleClusteringResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear la respuesta JSON de Mahalanobis: " + e.getMessage(), e);
        }
    }
}
