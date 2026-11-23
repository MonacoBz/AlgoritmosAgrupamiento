import numpy as np


def compute_euclidean_matrix(matrix_rows: np.ndarray) -> np.ndarray:
    """
    Recibe una matriz de forma (n_elementos, n_variables)
    y devuelve la matriz simétrica de distancias euclidianas (n x n).
    """
    diff = matrix_rows[:, np.newaxis, :] - matrix_rows[np.newaxis, :, :]
    return np.sqrt(np.sum(diff ** 2, axis=-1))