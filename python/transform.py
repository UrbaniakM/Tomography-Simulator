import numpy as np
from math import sin, cos, radians, floor
from bresenham import line

def calculate_position_on_circle(circle_center_x, circle_center_y, radius, alpha):
    x = round(circle_center_x + radius * cos(radians(alpha)))
    y = round(circle_center_y + radius * sin(radians(alpha)))
    return x,y

def radon_transform(image, n_detectors, structure_range, dalpha):
    circle_center_x, R = image.shape
    x_max = circle_center_x
    y_max = R
    R /= 2
    circle_center_y = R
    circle_center_x /= 2
    alpha = 0
    sinogram = []
    lines = []
    while alpha <= 360:
        sinogram.append([])
        lines.append([])
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
            lines[-1].append(bresenham_line)
            val = np.float(0)
            num = 0
            for x_iter,y_iter in bresenham_line:
                if x_iter < x_max and y_iter < y_max:
                    val += image[x_iter][y_iter]
                    num += 1
            if num > 0:
                val /= num
            sinogram[-1].append(val)
        alpha += dalpha

    return sinogram, lines

def recreateImage(sinogram, lines, img_size_x, img_size_y):
    image = np.zeros([img_size_x, img_size_y])
    sinogram_size_x, sinogram_size_y = np.shape(sinogram)
    for x in range(sinogram_size_x):
        for y in range(sinogram_size_y):
            line = lines[x][y]
            for line_x, line_y in line:
                if line_x < img_size_x and line_y < img_size_y:
                    image[line_x][line_y] += sinogram[x][y]
    return image