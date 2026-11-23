package org.uaemex.cleanerservice.domain.preparator;


import org.springframework.stereotype.Component;
import org.uaemex.cleanerservice.domain.preparator.types.PrepareDataType;
import org.uaemex.cleanerservice.models.Matrix;
import org.uaemex.cleanerservice.models.typedata.BinaryData;
import org.uaemex.cleanerservice.models.typedata.QualitativeData;
import org.uaemex.cleanerservice.models.typedata.QuantitativeData;

import java.util.List;


@Component
public class MatrixPreparator implements Preparator{

    private final PrepareDataType<BinaryData> prepareBinary;
    private final PrepareDataType<QualitativeData> prepareQualitative;
    private final PrepareDataType<QuantitativeData> prepareQuantitative;


    public MatrixPreparator(
            PrepareDataType<BinaryData> prepareBinary,
            PrepareDataType<QualitativeData> prepareQualitative,
            PrepareDataType<QuantitativeData> prepareQuantitative
    ){
        this.prepareBinary = prepareBinary;
        this.prepareQualitative = prepareQualitative;
        this.prepareQuantitative = prepareQuantitative;
    }

    @Override
    public void prepareMatrix(Matrix m, List<String> report) {
            prepareBinary.prepareData(m.getBinaryData(), report);
            prepareQualitative.prepareData(m.getQualitativeData(), report);
            prepareQuantitative.prepareData(m.getQuantitativeData(), report);
    }


}
