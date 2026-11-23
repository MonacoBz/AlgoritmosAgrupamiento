package org.uaemex.cleanerservice.domain.knn;


import org.springframework.stereotype.Component;
import org.uaemex.cleanerservice.models.Matrix;
import org.uaemex.cleanerservice.models.dto.MatrixReport;
import org.uaemex.cleanerservice.models.typedata.QuantitativeData;


import java.util.*;
import java.util.stream.Collectors;

@Component
public class KnnInputer implements Knn {

    private static final int K = 3; // El número de vecinos más cercanos a buscar

    @Override
    public void doKnn(Matrix m, List<String> report) {
        int totalRows = m.getRowsNum();
        int minUmbral = (int) (totalRows * 0.60); // Mínimo 60% de datos POR COLUMNA

        // 1. Pre-calcular rangos (Max - Min) de las cuantitativas para poder medir distancias
        List<Double> ranges = calculateRanges(m.getQuantitativeData());

        // 2. Procesar Cuantitativas
        for (int c = 0; c < m.getQuantitativeData().size(); c++) {
            List<Double> column = m.getQuantitativeData().get(c).getData();
            if (needsKnn(column, minUmbral, totalRows)) {
                imputeQuantitative(m, column, ranges, c, report);
            }
        }

        // 3. Procesar Binarias
        for (int c = 0; c < m.getBinaryData().size(); c++) {
            List<Integer> column = m.getBinaryData().get(c).getData();
            if (needsKnn(column, minUmbral, totalRows)) {
                imputeBinary(m, column, ranges ,c, report);
            }
        }

        // 4. Procesar Cualitativas
        for (int c = 0; c < m.getQualitativeData().size(); c++) {
            List<Double> column = m.getQualitativeData().get(c).getData();
            if (needsKnn(column, minUmbral, totalRows)) {
                imputeQualitative(m, column, ranges, c, report);
            }
        }
    }

    // --- EVALUACIÓN DE COLUMNA ---
    private boolean needsKnn(List<?> column, int minUmbral, int totalRows) {
        int validCount = 0;
        for (Object val : column) {
            if (val != null) validCount++;
        }
        // Aplica KNN si cumple el >= 60%, pero asegúrate de que haya al menos un null que reparar (< totalRows)
        return validCount >= minUmbral && validCount < totalRows;
    }

    // --- LOGICA DE IMPUTACIÓN ---
    private void imputeQuantitative(Matrix m, List<Double> column, List<Double> ranges, int colIndex, List<String> report) {
        for (int i = 0; i < column.size(); i++) {
            if (column.get(i) == null) {
                List<Neighbor> neighbors = findNearestNeighbors(m, ranges, column, i);

                // Sacar el promedio de los vecinos
                double sum = 0;
                for (Neighbor n : neighbors) {
                    sum += column.get(n.rowIndex);
                }
                double avg = sum / neighbors.size();
                column.set(i, avg);
                report.add("Columna Cuantitativa " + (colIndex + 1) + ", Fila " + (i + 1) + ": imputado con KNN -> " + avg);
            }
        }
    }

    private void imputeBinary(Matrix m, List<Integer> column, List<Double> ranges, int colIndex, List<String> report) {
        for (int i = 0; i < column.size(); i++) {
            if (column.get(i) == null) {
                List<Neighbor> neighbors = findNearestNeighbors(m, ranges, column, i);

                // Sacar la moda (votación) de los vecinos
                int ones = 0, zeros = 0;
                for (Neighbor n : neighbors) {
                    if (column.get(n.rowIndex) == 1) ones++;
                    else zeros++;
                }
                int imputed = ones > zeros ? 1 : 0;
                column.set(i, imputed);
                report.add("Columna Binaria " + (colIndex + 1) + ", Fila " + (i + 1) + ": imputado con KNN -> " + imputed);
            }
        }
    }

    private void imputeQualitative(Matrix m, List<Double> column, List<Double> ranges, int colIndex, List<String> report) {
        for (int i = 0; i < column.size(); i++) {
            if (column.get(i) == null) {
                List<Neighbor> neighbors = findNearestNeighbors(m, ranges, column, i);

                // Sacar la moda de los vecinos
                Map<Double, Integer> freq = new HashMap<>();
                for (Neighbor n : neighbors) {
                    Double val = column.get(n.rowIndex);
                    freq.put(val, freq.getOrDefault(val, 0) + 1);
                }
                Double mode = freq.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .get().getKey();
                column.set(i, mode);
                report.add("Columna Cualitativa " + (colIndex + 1) + ", Fila " + (i + 1) + ": imputado con KNN -> " + mode);
            }
        }
    }

    // --- ALGORITMO BÚSQUEDA DE VECINOS ---
    private List<Neighbor> findNearestNeighbors(Matrix m, List<Double> ranges, List<?> currentColumn, int targetRowIndex) {
        List<Neighbor> candidates = new ArrayList<>();

        for (int j = 0; j < m.getRowsNum(); j++) {
            // Solo considerar como vecino a las filas que SÍ tienen dato en esta columna
            if (j != targetRowIndex && currentColumn.get(j) != null) {
                double similarity = calculateRowSimilarity(m, ranges, targetRowIndex, j);
                candidates.add(new Neighbor(j, similarity));
            }
        }

        // Ordenar de mayor similitud a menor, y tomar los K primeros
        candidates.sort((n1, n2) -> Double.compare(n2.similarity, n1.similarity));
        return candidates.stream().limit(K).collect(Collectors.toList());
    }

    // Calcula una versión simplificada de Gower solo para buscar vecinos
    private double calculateRowSimilarity(Matrix m, List<Double> ranges, int rowA, int rowB) {
        double similarity = 0;
        double weight = 0;

        // Cuantitativas
        for (int c = 0; c < m.getQuantitativeData().size(); c++) {
            Double v1 = m.getQuantitativeData().get(c).getData().get(rowA);
            Double v2 = m.getQuantitativeData().get(c).getData().get(rowB);
            if (v1 != null && v2 != null) {
                weight++;
                similarity += 1 - (Math.abs(v1 - v2) / ranges.get(c));
            }
        }

        // Binarias
        for (int c = 0; c < m.getBinaryData().size(); c++) {
            Integer v1 = m.getBinaryData().get(c).getData().get(rowA);
            Integer v2 = m.getBinaryData().get(c).getData().get(rowB);
            if (v1 != null && v2 != null) {
                weight++;
                if (v1.equals(v2)) similarity++;
            }
        }

        // Cualitativas
        for (int c = 0; c < m.getQualitativeData().size(); c++) {
            Double v1 = m.getQualitativeData().get(c).getData().get(rowA);
            Double v2 = m.getQualitativeData().get(c).getData().get(rowB);
            if (v1 != null && v2 != null) {
                weight++;
                if (v1.equals(v2)) similarity++;
            }
        }

        return weight == 0 ? 0 : similarity / weight;
    }

    private List<Double> calculateRanges(List<QuantitativeData> qData) {
        return qData.stream().map(q -> {
            double max = q.getData().stream().filter(Objects::nonNull).mapToDouble(d -> d).max().orElse(1.0);
            double min = q.getData().stream().filter(Objects::nonNull).mapToDouble(d -> d).min().orElse(0.0);
            return (max - min) == 0 ? 1.0 : (max - min);
        }).collect(Collectors.toList());
    }

    // Clase auxiliar para guardar y ordenar distancias
    private static class Neighbor {
        int rowIndex;
        double similarity;

        Neighbor(int rowIndex, double similarity) {
            this.rowIndex = rowIndex;
            this.similarity = similarity;
        }
    }
}
