package org.uaemex.cleanerservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.uaemex.cleanerservice.domain.knn.Knn;
import org.uaemex.cleanerservice.domain.preparator.Preparator;
import org.uaemex.cleanerservice.domain.reader.IFileReader;
import org.uaemex.cleanerservice.models.Matrix;
import org.uaemex.cleanerservice.models.dto.MatrixReport;

import java.util.ArrayList;
import java.util.List;

@Service
public class CleanerService implements IService {

    private final Preparator preparator;
    private final IFileReader reader;
    private final Knn knn;

    public CleanerService(
            Preparator preparator,
            IFileReader reader,
            Knn knn
    ){
        this.preparator = preparator;
        this.reader = reader;
        this.knn = knn;
    }

    @Override
    public MatrixReport getMatrix(MultipartFile file) {
        return reader.getMatrix(file);
    }

    @Override
    public MatrixReport cleanMatrix(Matrix m) {
        List<String> report = new ArrayList<>();
        preparator.prepareMatrix(m, report);
        knn.doKnn(m,report);
        return new MatrixReport(m, report);
    }


}
