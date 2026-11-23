package org.uaemex.gatewayservice.validations.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.uaemex.gatewayservice.validations.validationtype.file.FileNotNullValidation;
import org.uaemex.gatewayservice.validations.validationtype.file.ValidationFileType;
import org.uaemex.gatewayservice.validations.validationtype.matrix.ValidationType;

import java.util.List;

@Component
public class FileValidationImp implements FileValidation{

    List<ValidationFileType> validations = List.of(
            new FileNotNullValidation()
    );

    @Override
    public void validate(MultipartFile file) {
        validations.forEach(v -> v.validate(file));
    }
}
