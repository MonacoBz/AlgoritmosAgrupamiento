package org.uaemex.matrixservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uaemex.matrixservice.model.datatype.BinaryData;
import org.uaemex.matrixservice.model.datatype.QualitativeData;
import org.uaemex.matrixservice.model.datatype.QuantitativeData;

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
