from fastapi import FastAPI

from schemas.dtos import MatrixRequestDto, SingleClusteringResponse
from services.euclidean_service import EuclideanService

app = FastAPI(title="Euclidean Clustering Service")
euclidean_service = EuclideanService()


@app.post("/api/matrix/euclidean", response_model=SingleClusteringResponse)
def calculate_euclidean(request: MatrixRequestDto):
    return euclidean_service.solve(request)