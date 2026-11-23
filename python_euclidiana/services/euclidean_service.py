import numpy as np
from fastapi import HTTPException

from core.clustering import build_clustering_result
from core.distance import compute_euclidean_matrix
from schemas.dtos import MatrixRequestDto, SingleClusteringResponse


class EuclideanService:
    def solve(self, request: MatrixRequestDto) -> SingleClusteringResponse:
        quant_cols = request.m.quantitativeData
        if not quant_cols:
            raise HTTPException(
                status_code=400,
                detail="La matriz debe contener al menos una columna en quantitativeData.",
            )

        cols_matrix = np.array([col.data for col in quant_cols], dtype=float)
        rows_matrix = cols_matrix.T

        if rows_matrix.shape[0] != request.m.rowsNum:
            raise HTTPException(
                status_code=400,
                detail="rowsNum no coincide con la longitud de las columnas.",
            )

        dist_matrix = compute_euclidean_matrix(rows_matrix)
        result = build_clustering_result(dist_matrix, request.method)

        return SingleClusteringResponse(
            linkageMethod=request.method.value,
            result=result,
        )