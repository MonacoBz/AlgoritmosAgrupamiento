package org.uaemex.cleanerservice.domain.preparator.types;

import java.util.List;

public interface PrepareDataType<T> {

    void prepareData(List<T> data, List<String> report);

}
