package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;

public class Main extends Application {
    final TransformImage transformImage = new TransformImage();

    public static void main(String[] args) {
        launch(args);
    }

    private BufferedImage processedImage = null;

    public void setProcessedImage(BufferedImage bufferedImage){
        processedImage = bufferedImage;
        transformImage.setBufferedImage(bufferedImage);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        final LoadImage loadImage = new LoadImage(primaryStage, this);

        FlowPane scenePane = new FlowPane(Orientation.VERTICAL);
        scenePane.setPadding(new Insets(5, 5, 5, 5));
        scenePane.setVgap(4);
        scenePane.setHgap(4);
        scenePane.getChildren().addAll(loadImage, transformImage);

        Scene scene = new Scene(scenePane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
