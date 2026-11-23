package org.uaemex.matrixservice.domain.formula;



import org.uaemex.matrixservice.dto.GowerResult;
import org.uaemex.matrixservice.model.Matrix;

import java.util.List;

public interface IFormula {

    List<GowerResult> doFormula(Matrix m);
}
