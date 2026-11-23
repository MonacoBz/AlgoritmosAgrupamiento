package org.uaemex.cleanerservice.domain.knn;



import org.uaemex.cleanerservice.models.Matrix;

import java.util.List;

public interface Knn {

    void doKnn(Matrix m, List<String> report);
}
