def sign(x):
    if x >= 0:
        return +1
    return -1

def line(x0, y0, x1, y1):

    line_list = []
    dx = x1 - x0
    dy = y1 - y0
    inc_x = sign(dx)
    inc_y = sign(dy)
    dx = abs(dx)
    dy = abs(dy)

    if dx >= dy:

        d = 2*dy - dx
        delta_A = 2*dy
        delta_B = 2*dy - 2*dx
        x,y = (0, 0)

        for i in range(dx+1):
            line_list.append((x0+x, y0+y))
            if d > 0:
                d += delta_B
                x += inc_x
                y += inc_y
            else:
                d += delta_A
                x += inc_x
    else:

        d = 2*dx - dy
        delta_A = 2*dx
        delta_B = 2*dx - 2*dy
        x,y = (0,0)

        for i in range(dy+1):
            line_list.append((x0+x, y0+y))
            if d > 0:
                d += delta_B
                x += inc_x
                y += inc_y
            else:
                d += delta_A
                y += inc_y

    return line_list
