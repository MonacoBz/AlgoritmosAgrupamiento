package org.uaemex.cleanerservice.service;

import org.springframework.web.multipart.MultipartFile;
import org.uaemex.cleanerservice.models.Matrix;
import org.uaemex.cleanerservice.models.dto.MatrixReport;

public interface IService {
    MatrixReport getMatrix(MultipartFile file);

    MatrixReport cleanMatrix(Matrix m);

}
