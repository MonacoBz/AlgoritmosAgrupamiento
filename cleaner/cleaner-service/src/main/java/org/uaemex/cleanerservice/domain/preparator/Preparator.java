package org.uaemex.cleanerservice.domain.preparator;


import org.uaemex.cleanerservice.models.Matrix;

import java.util.List;

public interface Preparator {

    void prepareMatrix(Matrix m, List<String> report);
}
