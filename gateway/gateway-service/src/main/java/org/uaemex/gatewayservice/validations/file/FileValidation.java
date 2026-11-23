package org.uaemex.gatewayservice.validations.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidation {

    void validate(MultipartFile file);
}
