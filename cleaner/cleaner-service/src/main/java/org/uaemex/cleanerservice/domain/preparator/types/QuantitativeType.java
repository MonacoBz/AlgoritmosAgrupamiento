package org.uaemex.cleanerservice.domain.preparator.types;

import org.springframework.stereotype.Component;
import org.uaemex.cleanerservice.models.typedata.QuantitativeData;

import java.util.List;

@Component
public class QuantitativeType implements PrepareDataType<QuantitativeData>{

    @Override
    public void prepareData(List<QuantitativeData> data, List<String> report) {
        if (data == null || data.isEmpty())return;
        int totalRows = data.get(0).getData().size();
        int umbral = (int) (totalRows * 0.9);

        for (int c = 0; c < data.size(); c++) {
            QuantitativeData qData = data.get(c);
            int noNullSize = noNullSize(qData);
            if (noNullSize >= umbral && noNullSize < totalRows)overUmbral(qData, c, report);

        }
    }

    private int noNullSize(QuantitativeData data) {
        int response = 0;
        var binaries = data.getData();
        for (Double b : binaries) if (b != null) response++;
        return response;
    }

    private void overUmbral(QuantitativeData data, int colIndex, List<String> report){
        var qData = data.getData();
        double average = getAverage(qData);
        for (int i = 0; i < qData.size() ; i++)if (qData.get(i) == null) {
            qData.set(i,average);
            report.add("Columna Cuantitativa: " + (colIndex + 1) + ", Fila " + (i + 1) + ": imputado con promedio (>=90%) -> " + average);
        }

    }

    private double getAverage(List<Double> data){
        double result = 0;
        int validCount = 0;
        for (Double q : data) {
            if (q != null) {
                result += q;
                validCount++;
            }
        }
        return result / validCount;
    }
}
