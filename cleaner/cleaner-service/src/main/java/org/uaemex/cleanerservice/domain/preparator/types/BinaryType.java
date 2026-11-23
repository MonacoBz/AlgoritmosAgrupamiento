package org.uaemex.cleanerservice.domain.preparator.types;

import org.springframework.stereotype.Component;
import org.uaemex.cleanerservice.models.typedata.BinaryData;

import java.util.List;
import java.util.function.Predicate;

@Component
public class BinaryType implements PrepareDataType<BinaryData> {


    @Override
    public void prepareData(List<BinaryData> data, List<String> report) {
        if (data == null || data.isEmpty()) return;

        int totalRows = data.get(0).getData().size();
        int umbral = (int) (totalRows * 0.9);

        for (int c = 0; c < data.size(); c++) {
            BinaryData bData = data.get(c);
            int noNullSize = noNullSize(bData);
            if (noNullSize >= umbral && noNullSize < totalRows) {
                overUmbral(bData, c, report);
            }
        }
    }

    private int noNullSize(BinaryData data) {
        int response = 0;
        var binaries = data.getData();
        for (Integer b : binaries) if (b != null) response++;
        return response;
    }

    private void overUmbral(BinaryData data, int colIndex, List<String> report){
        var binaries = data.getData();
        int zeros = count(data.getData(),b -> b != null && b == 0);
        int ones = count(data.getData(), b -> b != null && b == 1);
        int mode = (zeros > ones)? 0 : 1;
        for (int i = 0; i < binaries.size(); i++) {
            if (binaries.get(i) == null) {
                binaries.set(i, mode);
                report.add("Columna Binaria " + (colIndex + 1) + ", Fila " + (i + 1) + ": imputado con Moda (>=90%) -> " + mode);
            }
        }
    }

    private int count(List<Integer> data, Predicate<Integer> countZerosOrOnes){
        int response = 0;
        for (Integer b : data)if (countZerosOrOnes.test(b))response++;
        return response;
    }

}
