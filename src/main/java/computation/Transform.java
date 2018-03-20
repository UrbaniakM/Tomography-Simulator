package computation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Transform {
    private BufferedImage image = null;
    private BufferedImage sinogram = null;

    private int numberOfDetectors = 30;
    private int structureRange = 90; // in degrees
    private int deltaAlpha = 3;

    private int size;
    private int radius;
    private int xMax;
    private int yMax;

    public void setImage(BufferedImage image){
        this.image = image;
        size = image.getHeight();
        radius = size/2;
        xMax = size;
        yMax = size;
    }

    public void setNumberOfDetectors(int numberOfDetectors){
        this.numberOfDetectors = numberOfDetectors;
    }

    public void setDeltaAlpha(int deltaAlpha){
        this.deltaAlpha = deltaAlpha;
    }

    public void setStructureRange(int structureRange){
        this.structureRange = structureRange;
    }

    private Point calculatePositionOnCircle(int degree) {
        int x = Math.toIntExact(Math.round(size/2 + radius * Math.cos(Math.toRadians(degree))));
        int y = Math.toIntExact(Math.round(size/2 + radius * Math.sin(Math.toRadians(degree))));
        return new Point(x,y);
    }

    private int sign(int value){
        return (value >= 0) ? 1 : -1;
    }

    private ArrayList<Point> bresenhamLine(Point p0, Point p1){
        ArrayList<Point> line = new ArrayList<>();
        int dx = p1.x - p0.x;
        int dy = p1.y - p0.y;
        int incX = sign(dx);
        int incY = sign(dy);
        dx = Math.abs(dx);
        dy = Math.abs(dy);
        if(dx >= dy) {
            int d = 2 * dy - dx;
            int deltaA = 2 * dy;
            int deltaB = 2 * dy - 2 * dx;
            int x = 0;
            int y = 0;

            for(int i = 0; i <= dx; i++) {
                Point currentPoint = new Point(p0.x + x, p0.y + y);
                line.add(currentPoint);
                if(d > 0) {
                    d += deltaB;
                    x += incX;
                    y += incY;
                }
                else {
                    d += deltaA;
                    x += incX;
                }
            }
        }
        else {
            int d = 2 * dx - dy;
            int deltaA = 2 * dx;
            int deltaB = 2 * dx - 2 * dy;
            int x = 0;
            int y = 0;

            for(int i = 0; i <= dy; i++) {
                Point currentPoint = new Point(p0.x + x, p0.y + y);
                line.add(currentPoint);
                if(d > 0) {
                    d += deltaB;
                    x += incX;
                    y += incY;
                }
                else {
                    d += deltaA;
                    y += incY;
                }
            }
        }

        return line;
    }

    public BufferedImage generateSinogram(){ // TODO
        int sinogramHeight = numberOfDetectors;
        int sinogramWidth = 180 / deltaAlpha;
        sinogram = new BufferedImage(sinogramHeight, sinogramWidth, BufferedImage.TYPE_BYTE_GRAY);
        int maxVal = 1024;
        for(int alpha = 0; alpha < 180; alpha += deltaAlpha){
            Point emiter = calculatePositionOnCircle(alpha);
            for(int iter = 0; iter < numberOfDetectors; iter++){
                int beta = alpha + 180 - structureRange/2 + iter * (structureRange / (numberOfDetectors - 1));
                Point detector = calculatePositionOnCircle(beta);
                ArrayList<Point> line = bresenhamLine(emiter, detector);
                int val = 0;
                int num = 0;
                for(Point currentPoint: line){
                    if(currentPoint.x < xMax && currentPoint.y < yMax){
                        Color color = new Color(image.getRGB(currentPoint.x, currentPoint.y));
                        val += (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                        num++;
                    }
                }
                //if(num > 0){
                //    val /= num;
                //}
                if(val < maxVal)
                    maxVal = val;
                sinogram.setRGB(iter,alpha/deltaAlpha, val);
            }
        }
        return sinogram;
    }

    private class Point{
        private int x;
        private int y;

        private Point(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
}
