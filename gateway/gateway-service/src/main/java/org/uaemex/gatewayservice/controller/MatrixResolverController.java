package org.uaemex.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uaemex.gatewayservice.dto.MatrixRequestDto;
import org.uaemex.gatewayservice.dto.MatrixRequestTypeDto;
import org.uaemex.gatewayservice.dto.MatrixResponseDto;
import org.uaemex.gatewayservice.service.MatrixService;

@RestController
@RequestMapping("/api/gateway")
@CrossOrigin("*")
public class MatrixResolverController {

    private final MatrixService service;

    public MatrixResolverController(MatrixService service){
        this.service = service;
    }


    @PostMapping("/resolve")
    public ResponseEntity<MatrixResponseDto> resolveMatrix(@RequestBody MatrixRequestTypeDto m){
        return ResponseEntity.ok(service.resolveMatrix(m));
    }
}
