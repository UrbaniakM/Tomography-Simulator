package gui;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoadImage extends VBox {
    final private ImageView imageView = new ImageView();
    final private FileChooser fileChooser = new FileChooser();
    final private Button loadFileButton = new Button("Select file");
    final private Label label = new Label("Input image");

    public LoadImage(Stage stage, Main main){
        super(8);
        fileChooser.setTitle("Select image to process");
        getChildren().addAll(label, imageView, loadFileButton);

        imageView.setFitHeight(250);
        imageView.setFitWidth(250);

        setAlignment(Pos.CENTER);

        loadFileButton.setOnMouseClicked(event -> {
            File file = fileChooser.showOpenDialog(stage);
            if(file != null){
                try {
                    BufferedImage bufferedImage = ImageIO.read(file);
                    main.setProcessedImage(bufferedImage);
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageView.setImage(image);
                } catch (IOException ex){
                    // TODO: wrong input file alert
                }
            }
        });
    }
}
