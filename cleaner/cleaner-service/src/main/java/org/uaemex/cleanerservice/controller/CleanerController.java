package org.uaemex.cleanerservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.uaemex.cleanerservice.models.Matrix;
import org.uaemex.cleanerservice.models.dto.MatrixReport;
import org.uaemex.cleanerservice.service.IService;

@RestController
@RequestMapping("/api/cleaner")
public class CleanerController {

    private final IService service;

    public CleanerController(IService service){
        this.service = service;
    }

    @PostMapping("/prepare")
    public ResponseEntity<MatrixReport> prepareMatrix(@RequestParam("File")MultipartFile file){
        var body = service.getMatrix(file);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/clean")
    public ResponseEntity<MatrixReport> cleanMatrix(@RequestBody Matrix m){
        var body = service.cleanMatrix(m);
        return ResponseEntity.ok(body);
    }
}
