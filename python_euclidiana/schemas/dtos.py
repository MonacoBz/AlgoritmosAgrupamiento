from enum import Enum
from typing import List, Optional
from pydantic import BaseModel


class LinkageMethod(str, Enum):
    VMC = "VMC"
    VML = "VML"
    PROMEDIO = "PROMEDIO"


class QuantitativeData(BaseModel):
    data: List[float]


class MatrixDto(BaseModel):
    quantitativeData: List[QuantitativeData] = []
    rowsNum: int
    # Opcionales para no fallar si el Gateway manda el objeto Matrix completo
    binaryData: Optional[list] = []
    qualitativeData: Optional[list] = []
    totalVariables: Optional[int] = 0
    repetitiveValues: Optional[list] = []


class MatrixRequestDto(BaseModel):
    m: MatrixDto
    method: LinkageMethod


class RelationDto(BaseModel):
    elementA: int
    elementB: int
    distance: float


class ClusterStepDto(BaseModel):
    step: int
    clusterAId: int
    clusterBId: int
    clusterALabel: str
    clusterBLabel: str
    newClusterId: int
    newClusterLabel: str
    distance: float
    size: int


class ClusteringResultDto(BaseModel):
    distanceMatrix: List[List[float]]
    sortedRelations: List[RelationDto]
    clusteringHistory: List[ClusterStepDto]


class SingleClusteringResponse(BaseModel):
    linkageMethod: str
    result: ClusteringResultDto