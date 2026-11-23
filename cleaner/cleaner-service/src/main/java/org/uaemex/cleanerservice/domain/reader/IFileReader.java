package org.uaemex.cleanerservice.domain.reader;


import org.springframework.web.multipart.MultipartFile;
import org.uaemex.cleanerservice.models.Matrix;
import org.uaemex.cleanerservice.models.dto.MatrixReport;

public interface IFileReader {

    MatrixReport getMatrix(MultipartFile file);
}
