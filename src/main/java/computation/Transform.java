package computation;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;

public class Transform {
    private BufferedImage image = null;
    private BufferedImage sinogram = null;
    private BufferedImage reconstructed = null;

    //private BufferedImage

    private int numberOfDetectors = 30;
    private int structureRange = 90; // in degrees
    private int deltaAlpha = 2;

    private int size;
    private int radius;
    private int xMax;
    private int yMax;

    private int maskSize = 44;

    private ArrayList<Point>[][] lines;

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

    private Point calculatePositionOnCircle(double degree) {
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
        Raster inputImage = image.getRaster();
        Raster reconstructedImage = reconstructed.getRaster();
        for(int x = 0; x < xMax; x++){
            for(int y = 0; y < yMax; y++){
                meanSquareError += Math.pow(inputImage.getSample(x,y,0) - reconstructedImage.getSample(x,y,0),2);
            }
        }
        meanSquareError /=  255 * 255;
        meanSquareError /= xMax * yMax;
        meanSquareError = Math.round(meanSquareError * 10000.0) / 10000.0;
        return meanSquareError;
    }


    public BufferedImage generateSinogram(){
        int sinogramHeight = numberOfDetectors;
        int sinogramWidth = (int)Math.ceil(360 / deltaAlpha)+1;
        sinogram = new BufferedImage(sinogramHeight, sinogramWidth, BufferedImage.TYPE_BYTE_GRAY);
        Raster imageData = image.getData();
        lines = new ArrayList[sinogramHeight][sinogramWidth];
        int rayNum = 0;
        for(int alpha = 0; alpha <= 360; alpha += deltaAlpha){
            Point emiter = calculatePositionOnCircle(alpha);
            ArrayList<Double> valuesArray = new ArrayList<>();
            for(int detector = 0; detector < numberOfDetectors; detector++){
                double beta = alpha + 180 - structureRange/2 + (detector * structureRange / (numberOfDetectors - 1));
                Point detectorPoint = calculatePositionOnCircle(beta);
                ArrayList<Point> line = bresenhamLine(emiter, detectorPoint);
                lines[detector][rayNum] = new ArrayList<>();
                lines[detector][rayNum] = line;
                double val = 0;
                int num = 0;
                double weight = Math.max(Math.sin(Math.toRadians(beta%90)),Math.cos(Math.toRadians(beta%90)));
                for(Point currentPoint: line){
                    if(currentPoint.x < xMax && currentPoint.y < yMax){
                        double currentVal = imageData.getSample(currentPoint.x, currentPoint.y, 0);
                        currentVal *= 1/weight;
                        val += currentVal;
                        num++;
                    }
                }
                valuesArray.add(val);
                if(num > 0){
                    val /= num;
                }
                int[] valWrite = new int[1];
                valWrite[0] = Math.toIntExact(Math.round(val));
                sinogram.getRaster().setPixel(detector,rayNum,valWrite);
            }
            double maxValue = Collections.max(valuesArray);
            for(int detector = 0; detector < valuesArray.size(); detector++){
                int[] valWrite = new int[1];
                valWrite[0] = (int)(valuesArray.get(detector)/maxValue*255);
                sinogram.getRaster().setPixel(detector,rayNum,valWrite);
            }
            rayNum++;
        }
        double[] mask = getMask();
        for (int height = 0; height < sinogramHeight; height++) {
            for (int width = 0; width < sinogramWidth; width++) {
                double sinogramVal = sinogram.getData().getSample(height, width, 0);
                double[] newVal = new double[1];
                newVal[0] = sinogramVal * mask[0];
                for(int index = 1; index < maskSize; index++){
                    if(width + index < sinogramWidth) {
                        newVal[0] += sinogram.getData().getSample(height, width+index, 0) * mask[index];
                    }
                    if(width - index >= 0){
                        newVal[0] += sinogram.getData().getSample(height, width-index, 0) * mask[index];
                    }
                }
                if(newVal[0] < 0)
                {
                    newVal[0] = 0;
                }
                sinogram.getRaster().setPixel(height,width,newVal);
            }
        }
        return sinogram;
    }

    public double[] getMask() {
        double[] mask = new double[maskSize];
        mask[0] = 1.0;
        double constVal = -4 / (Math.PI * Math.PI);
        for (int i = 1; i < maskSize; i++) {
            if( i % 2 == 0){
                mask[i] = 0;
            } else {
                mask[i] = constVal / (double)(i*i);
            }
        }
        return mask;
    }


    public BufferedImage reconstructImage(boolean iter){
        reconstructed = new BufferedImage(xMax, yMax, BufferedImage.TYPE_BYTE_GRAY);
        int sinWidth = sinogram.getWidth();
        int sinHeight = sinogram.getHeight();
        double[][] values = new double[xMax][yMax];
        double[][] occurences = new double[xMax][yMax];
        for(int ray = 0; ray < sinHeight; ray++){
            for(int detector = 0; detector < sinWidth; detector++){
                int value = sinogram.getData().getSample(detector,ray,0);
                ArrayList<Point> line = lines[detector][ray];
                for(Point point: line){
                    if(point.x < xMax && point.y < yMax){
                        values[point.x][point.y] += value;
                        occurences[point.x][point.y]++;
                    }
                }
            }
        }
        for(int x = 0; x < xMax; x++){
            for(int y = 0; y < yMax; y++){
                values[x][y] /= occurences[x][y];
            }
        }

        double maxValue = 0;
        for(int x = 0; x < xMax; x++){
            for(int y = 0; y < yMax; y++){
                if(values[x][y] > maxValue) {
                    maxValue = values[x][y];
                }
            }
        }

        for (int x = 0; x < xMax; x++) {
            for (int y = 0; y < yMax; y++) {
                values[x][y] /= maxValue;
                double[] valWrite = new double[1];
                valWrite[0] = values[x][y] * 255;
                reconstructed.getRaster().setPixel(x,y,valWrite);
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
