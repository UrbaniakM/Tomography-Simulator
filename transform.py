import numpy as np
from math import sin, cos, sqrt, pi

#theta: (0,180)
#rho: smallest distance to the origin of coordinate system

def rho(r, R):
    rho_min = - (R - 1) / 2 * sqrt(2)
    return rho_min + r / sqrt(2)


def to_radians(x):
    return x * pi / 180

def radon_mine(image, angles): #angles - list of theta
    rows,cols = image.shape
    x_min = -(rows - 1) / 2
    y_min = -(cols - 1) / 2
    R = max(rows, cols) 
    sinogram = np.zeros((R, angles.size))
    max_val = 1
    for t in range(angles.size):
        radians = to_radians(angles[t])
        cos_theta = cos(radians)
        sin_theta = sin(radians)
        rho_offset = x_min * cos_theta + y_min * sin_theta
        if sin_theta > 1 / sqrt(2):
            alpha = - cos_theta / sin_theta
            for r in range(R):
                beta = (rho(r,R) - rho_offset) / sin_theta
                sum = 0
                for row in range(rows):
                    col = int(round(alpha * row + beta))
                    if col >= 0 and col < cols:
                        sum += image[row][col]
                val = sum / abs(sin_theta) / sqrt(rows**2 + cols**2)
                if val > max_val:
                    max_val = val
                sinogram[r][t] = val
        else:
            alpha = - sin_theta / cos_theta
            for r in range(R):
                beta = (rho(r, R) - rho_offset) / cos_theta
                sum = 0
                for col in range(cols):
                    row = int(round(alpha * col + beta))
                    if row >= 0 and row < rows:
                        sum += image[row][col]
                val = sum / abs(cos_theta) / sqrt(rows ** 2 + cols ** 2)
                if val > max_val:
                    max_val = val
                sinogram[r][t] = val
    sinogram *= 255.0 / max_val
    return sinogram