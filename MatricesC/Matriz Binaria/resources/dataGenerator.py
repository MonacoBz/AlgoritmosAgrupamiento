import random

# Opciones que tu código en C debería procesar correctamente
validos = ['0', '1', '2', '5', '99', '42']
# Opciones de ruido que tu Cleaner debería convertir en 0
basura = ['basura', 'hola', 'X', 'Z', '0.5', '', ' ', 'error404', 'texto_largo']

with open("./test_100x100.xcsv", "w") as f:
    for _ in range(254):
        fila = []
        for _ in range(254):
            # 70% de probabilidad de poner un número válido, 30% de inyectar basura
            if random.random() < 0.7:
                fila.append(random.choice(validos))
            else:
                fila.append(random.choice(basura))
        
        f.write(",".join(fila) + "\n")

print("¡Matriz sucia de 100x100 generada en resources/test_100x100.xcsv!")