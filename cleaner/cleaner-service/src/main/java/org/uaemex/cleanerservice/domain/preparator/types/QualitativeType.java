package org.uaemex.cleanerservice.domain.preparator.types;

import org.springframework.stereotype.Component;
import org.uaemex.cleanerservice.models.typedata.QualitativeData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QualitativeType implements PrepareDataType<QualitativeData>{
    @Override
    public void prepareData(List<QualitativeData> data, List<String> report) {
        if (data == null || data.isEmpty()) return;

        int totalRows = data.get(0).getData().size();
        int umbral = (int) (totalRows * 0.9);

        for (int c = 0; c < data.size(); c++) {
            QualitativeData qData = data.get(c);
            int noNullSize = noNullSize(qData);

            if (noNullSize >= umbral && noNullSize < totalRows) {
                overUmbral(qData, c, report);
            }
        }
    }

    private int noNullSize(QualitativeData data) {
        int response = 0;
        var binaries = data.getData();
        for (Double b : binaries) if (b != null) response++;
        return response;
    }

    private void overUmbral(QualitativeData data, int colIndex, List<String> report){
        var qData = data.getData();
        double mode = getMode(qData);
        for (int i = 0; i < qData.size() ; i++) {
            if (qData.get(i) == null) {
                qData.set(i, mode);
                report.add("Columna Cualitativa " + (colIndex + 1) + ", Fila " + (i + 1) + ": imputado con Moda (>=90%) -> " + mode);
            }
        }
    }

    private double getMode(List<Double> data){
        Map<Double, Integer> frequencies = new HashMap<>();

        for (Double q : data) {
            if (q != null) {
                frequencies.put(q, frequencies.getOrDefault(q, 0) + 1);
            }
        }

        double mode = 0;
        int maxFreq = 0;
        for (Map.Entry<Double, Integer> entry : frequencies.entrySet()) {
            if (entry.getValue() > maxFreq) {
                maxFreq = entry.getValue();
                mode = entry.getKey();
            }
        }
        return mode;
    }

}
