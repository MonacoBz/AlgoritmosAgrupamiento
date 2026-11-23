package org.uaemex.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.uaemex.gatewayservice.domain.Matrix;
import org.uaemex.gatewayservice.dto.MatrixReport;
import org.uaemex.gatewayservice.service.CleanerService;

@RestController
@RequestMapping("/api/gateway")
@CrossOrigin("*")
public class CleanerController {

    private final CleanerService cleaner;

    public CleanerController(CleanerService cleaner){
        this.cleaner = cleaner;
    }

    @PostMapping("/prepare")
    public ResponseEntity<MatrixReport> prepareMatrix(@RequestParam("File") MultipartFile file){
        var body = cleaner.prepareCleanerClient(file);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/clean")
    public ResponseEntity<MatrixReport> cleanMatrix(@RequestBody Matrix matrix){
        var body = cleaner.cleanCleanerClient(matrix);
        return ResponseEntity.ok(body);
    }
}
