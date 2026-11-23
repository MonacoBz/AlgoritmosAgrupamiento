package org.uaemex.gatewayservice.validations.validationtype.file;

import org.springframework.web.multipart.MultipartFile;

public interface ValidationFileType {

    void validate(MultipartFile file);
}
