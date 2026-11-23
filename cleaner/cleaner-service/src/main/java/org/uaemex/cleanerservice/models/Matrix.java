package org.uaemex.cleanerservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uaemex.cleanerservice.models.typedata.BinaryData;
import org.uaemex.cleanerservice.models.typedata.QualitativeData;
import org.uaemex.cleanerservice.models.typedata.QuantitativeData;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Matrix {

    private List<BinaryData> binaryData = new ArrayList<>();

    private List<QualitativeData> qualitativeData = new ArrayList<>();

    private List<QuantitativeData> quantitativeData = new ArrayList<>();

    private int rowsNum;

    private int totalVariables;

    private List<Double> repetitiveValues;
}
