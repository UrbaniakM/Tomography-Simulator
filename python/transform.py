import numpy as np
from math import sin, cos, sqrt, radians, floor
from bresenham import line

#theta: (0,180)
#rho: smallest distance to the origin of coordinate system

def rho(r, R):
    rho_min = - (R - 1) / 2 * sqrt(2)
    return rho_min + r / sqrt(2)


def radon_mine(image, angles): #angles - list of theta
    rows,cols = image.shape
    x_min = -(rows - 1) / 2
    y_min = -(cols - 1) / 2
    R = max(rows, cols)
    sinogram = np.zeros((R, angles.size))
    max_val = 1
    for t in range(angles.size):
        radians = radians(angles[t])
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




def calculate_position_on_circle(circle_center_x, circle_center_y, radius, alpha):
    x = round(circle_center_x + radius * cos(radians(alpha)))
    y = round(circle_center_y + radius * sin(radians(alpha)))
    return x,y

def radon_transform(image, n_detectors, structure_range, dalpha):
    circle_center_x, R = image.shape
    R /= 2
    circle_center_y = R
    circle_center_x /= 2
    alpha = 0
    while alpha < 360:
        x, y = calculate_position_on_circle(circle_center_x, circle_center_y, R, alpha)
        detectors = []
        if n_detectors%2 != 0:
            beta = alpha + 180.
            detector = calculate_position_on_circle(circle_center_x, circle_center_y, R, beta)
            detectors.append(detector)
        i_range = floor(n_detectors / 2)
        for i in range(-i_range, i_range+1):
            if i == 0:
                continue;
            beta = alpha + 180. + structure_range / i
            detector = calculate_position_on_circle(circle_center_x, circle_center_y, R, beta)
            detectors.append(detector)

        for i in range(len(detectors)):
            bresenham_line = line(x, y, detectors[i][0], detectors[i][1])

        alpha += dalpha