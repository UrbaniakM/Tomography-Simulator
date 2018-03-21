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
    sinogram = np.zeros([n_detectors,int(180/dalpha),])
    lines = []
    while alpha < 180:
        lines.append([])
        x, y = calculate_position_on_circle(circle_center_x, circle_center_y, R, alpha)
        for i in range(n_detectors):
            beta = alpha + 180 - structure_range/2 + i * (structure_range / (n_detectors - 1) )
            detector_x, detector_y = calculate_position_on_circle(circle_center_x, circle_center_y, R, beta)
            bresenham_line = line(x, y, detector_x, detector_y)
            lines[-1].append(bresenham_line)
            val = np.float(0)
            num = 0
            for x_iter,y_iter in bresenham_line:
                if x_iter < x_max and y_iter < y_max:
                    val += image[x_iter][y_iter]
                    num += 1
            if num > 0:
                val /= num
            sinogram[i,int(alpha/dalpha),] = val
        alpha += dalpha

    return sinogram, lines

def recreate_image(sinogram, lines, img_size_x, img_size_y):
    image = np.zeros([img_size_x, img_size_y])
    sinogram_size_x, sinogram_size_y = np.shape(sinogram)
    for x in range(sinogram_size_x):
        for y in range(sinogram_size_y):
            line = lines[y][x]
            for line_x, line_y in line:
                if line_x < img_size_x and line_y < img_size_y:
                    image[line_x][line_y] += sinogram[x][y]
    return image
