package report;

import computation.Transform;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class ReportWriter {
    private FileWriter out;
    private String outFilePath;
    private Transform transform;

    public ReportWriter(String imagePath, String outFilePath){
        this.outFilePath = outFilePath;
        try {
            out = new FileWriter(outFilePath);
            File file = new File(imagePath);
            BufferedImage inputImage = ImageIO.read(file);
            BufferedImage bufferedImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            bufferedImage.getGraphics().drawImage(inputImage, 0, 0, null);
            transform = new Transform();
            transform.setImage(bufferedImage);
        } catch (Exception ex){
            System.err.println(ex);
        }

    }

    public void generateResults() {
        final ArrayList<Result> resultsDeadAngle = new ArrayList<>();
        final ArrayList<Result> resultsNumberOfDetectors = new ArrayList<>();
        final ArrayList<Result> resultsDeltaAlpha = new ArrayList<>();

        try {
            transform.setNumberOfDetectors(100);
            transform.setDeltaAlpha(2);
            for(int deadAngle = 60; deadAngle <= 360; deadAngle += 20){
                transform.setStructureRange(deadAngle);
                transform.generateSinogram();
                transform.reconstructImage();
                double val = transform.meanSquareError();
                resultsDeadAngle.add(new Result(deadAngle, val));
            }


            for(int numberOfDetectors = 10; numberOfDetectors <= 250; numberOfDetectors += 10){
                transform.setNumberOfDetectors(numberOfDetectors);
                transform.generateSinogram();
                transform.reconstructImage();
                double val = transform.meanSquareError();
                resultsNumberOfDetectors.add(new Result(numberOfDetectors, val));
            }

            transform.setNumberOfDetectors(100);
            for(int deltaAlpha = 2; deltaAlpha <= 45; deltaAlpha += 2){
                transform.setDeltaAlpha(deltaAlpha);
                transform.generateSinogram();
                transform.reconstructImage();
                double val = transform.meanSquareError();
                resultsDeltaAlpha.add(new Result(deltaAlpha, val));
            }

            out.append("Dead angle");
            out.append(';');
            out.append("MSE");
            out.append('\n');

            System.out.println("Dead angle");

            for(Result result : resultsDeadAngle){
                out.append(Integer.toString(result.key));
                out.append(';');
                out.append(Double.toString(result.value));
                out.append('\n');
                System.out.println(Integer.toString(result.key) + ": " + Double.toString(result.value));
            }

            out.append('\n');
            out.append("Number of detectors");
            out.append(';');
            out.append("MSE");
            out.append('\n');

            System.out.println("\nNumber of detectors");

            for(Result result : resultsNumberOfDetectors){
                out.append(Integer.toString(result.key));
                out.append(';');
                out.append(Double.toString(result.value));
                out.append('\n');
                System.out.println(Integer.toString(result.key) + ": " + Double.toString(result.value));
            }

            out.append("\n");
            out.append("Delta alpha");
            out.append(';');
            out.append("MSE");
            out.append('\n');

            System.out.println("\nDelta alpha");

            for(Result result : resultsDeltaAlpha){
                out.append(Integer.toString(result.key));
                out.append(';');
                out.append(Double.toString(result.value));
                out.append('\n');
                System.out.println(Integer.toString(result.key) + ": " + Double.toString(result.value));
            }

            out.flush();
            out.close();
        } catch (Exception ex){
            System.out.println("Error with generating results");
            System.out.println("------------------------");
            System.err.println(ex);
        }
        System.out.println("Results generated properly, output file: " + outFilePath);
        System.out.println("------------------------");
    }

    private class Result{
        private int key;
        private double value;

        public Result(int key, double value){
            this.key = key;
            this.value = value;
        }
    }
}
