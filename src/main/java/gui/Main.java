package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        final LoadImage loadImage = new LoadImage(primaryStage);
        final TransformImage transformImage = new TransformImage();

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
