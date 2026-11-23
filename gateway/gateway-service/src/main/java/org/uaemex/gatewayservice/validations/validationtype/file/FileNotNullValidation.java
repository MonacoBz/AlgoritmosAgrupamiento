package org.uaemex.gatewayservice.validations.validationtype.file;

import org.springframework.web.multipart.MultipartFile;
import org.uaemex.gatewayservice.exceptions.ValidationException;

public class FileNotNullValidation implements ValidationFileType{

    @Override
    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty())throw new ValidationException("El archivo subido se encuentra vacío");
    }
}
