package computation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Transform {
    private BufferedImage image = null;
    private BufferedImage sinogram = null;
    private BufferedImage reconstructed = null;

    private int numberOfDetectors = 30;
    private int structureRange = 90; // in degrees
    private int deltaAlpha = 2;

    private int size;
    private int radius;
    private int xMax;
    private int yMax;

    private ArrayList<ArrayList<Point>> lines;

    private double meanSquareError;

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

    private Point calculatePositionOnCircle(float degree) {
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

    public double meanSquareError(){
        meanSquareError = 0;
        // TODO
        return meanSquareError;
    }

    public BufferedImage generateSinogram(){ // TODO
        int sinogramHeight = numberOfDetectors;
        int sinogramWidth = (int)Math.ceil(360 / deltaAlpha)+1;
        sinogram = new BufferedImage(sinogramHeight, sinogramWidth, BufferedImage.TYPE_BYTE_GRAY);
        lines = new ArrayList<>();
        int rayNum = 0;
        for(int alpha = 0; alpha <= 360; alpha += deltaAlpha){
            Point emiter = calculatePositionOnCircle(alpha);
            for(int detector = 0; detector < numberOfDetectors; detector++){
                float beta = alpha + 180 - structureRange/2 + detector * (structureRange / (numberOfDetectors - 1));
                Point detectorPoint = calculatePositionOnCircle(beta);
                ArrayList<Point> line = bresenhamLine(emiter, detectorPoint);
                lines.add(line);
                double val = 0;
                int num = 0;
                double weight = Math.max(Math.sin(Math.toRadians(alpha%90)),Math.cos(Math.toRadians(alpha%90)));
                for(Point currentPoint: line){
                    if(currentPoint.x < xMax && currentPoint.y < yMax){
                        //Color color = new Color(image.getRGB(currentPoint.x, currentPoint.y));
                        //val += (color.getRed() + color.getGreen() + color.getBlue());// / 3;
                        double currentVal = image.getRGB(currentPoint.x, currentPoint.y)& 0xFF;
                        currentVal *= 1/weight;
                        val += currentVal;
                        num++;
                    }
                }
                if(num > 0){
                    //val /= num;
                }
                sinogram.setRGB(detector,rayNum, Math.toIntExact(Math.round(val)));
            }
            rayNum++;
        }
        return sinogram;
    }

    public BufferedImage reconstructImage(){
        reconstructed = new BufferedImage(xMax, yMax, BufferedImage.TYPE_BYTE_GRAY);
        int sinWidth = sinogram.getWidth();
        int sinHeight = sinogram.getHeight();
        for(int x = 0; x < sinWidth; x++){
            for(int y = 0; y < sinHeight; y++){
                ArrayList<Point> line = lines.get(x + y);
                for(Point point: line){
                    if(point.x < xMax && point.y < yMax){
                        //Color color = new Color(reconstructed.getRGB(point.x, point.y));
                        //int val = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                        //Color colorSinog = new Color(sinogram.getRGB(x,y));
                        //val += (colorSinog.getRed() + colorSinog.getGreen() + colorSinog.getBlue()) / 3;
                        int val = reconstructed.getRGB(point.x,point.y) + sinogram.getRGB(x,y);
                        reconstructed.setRGB(x, y, val);
                    }
                }
            }
        }

        return reconstructed;
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
