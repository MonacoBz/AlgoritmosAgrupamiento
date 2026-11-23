package org.uaemex.gatewayservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.uaemex.gatewayservice.domain.datatype.BinaryData;
import org.uaemex.gatewayservice.domain.datatype.QualitativeData;
import org.uaemex.gatewayservice.domain.datatype.QuantitativeData;

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
