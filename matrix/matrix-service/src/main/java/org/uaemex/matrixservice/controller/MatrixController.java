package org.uaemex.matrixservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uaemex.matrixservice.dto.JaccardAndSockalResponse;
import org.uaemex.matrixservice.dto.MatrixRequestDto;
import org.uaemex.matrixservice.dto.SingleClusteringResponse;
import org.uaemex.matrixservice.service.BinaryService;
import org.uaemex.matrixservice.service.EuclideanService;
import org.uaemex.matrixservice.service.GowelService;
import org.uaemex.matrixservice.service.MahalanobisService;

@RestController
@RequestMapping("/api/matrix")
public class MatrixController {

    private final BinaryService binaryService;
    private final GowelService gService;
    private final EuclideanService eService;
    private final MahalanobisService mService;

    public MatrixController(
            BinaryService binaryService,
            GowelService gService,
            EuclideanService eService,
            MahalanobisService mService){
        this.binaryService = binaryService;
        this.gService = gService;
        this.eService = eService;
        this.mService = mService;
    }

    @PostMapping("/binary")
    public ResponseEntity<JaccardAndSockalResponse> resolveBinaryMatrix(@RequestBody MatrixRequestDto request){
        var body = binaryService.doJaccardAndSockal(request);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/gowel")
    public ResponseEntity<SingleClusteringResponse> resolveMixMatrix(@RequestBody MatrixRequestDto request){
        var body = gService.doGowel(request);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/euclidean")
    public ResponseEntity<SingleClusteringResponse> resolveQuantitativeMatrix(@RequestBody MatrixRequestDto request){
        var body = eService.doEuclidean(request);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/mahalanobis")
    public ResponseEntity<SingleClusteringResponse> resolveQuantitativeMatrixM(@RequestBody MatrixRequestDto request){
        var body = mService.doMahalanobis(request);
        return ResponseEntity.ok(body);
    }
}
