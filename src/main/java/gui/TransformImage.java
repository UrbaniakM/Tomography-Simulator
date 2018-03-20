package gui;

import computation.Transform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;

public class TransformImage extends HBox{
    final private VBox sinogramLayout = new VBox(8);
    final private Label sinogramLabel = new Label("Sinogram");
    final private ImageView sinogramView = new ImageView();
    final private Button sinogramButton = new Button("Generate sinogram");

    final private VBox recreatedLayout = new VBox(8);
    final private Label recreatedLabel = new Label("Recreated image");
    final private ImageView recreatedView = new ImageView();
    final private Button recreatedButton = new Button("Recreate image");


    public static BufferedImage bufferedImage = null;
    private BufferedImage sinogram = null;
    private BufferedImage reconstructedImage;
    private Transform transform = new Transform();

    public TransformImage(){
        super(5);
        getChildren().addAll(sinogramLayout, recreatedLayout);

        sinogramView.setFitHeight(250);
        sinogramView.setFitWidth(250);
        sinogramLayout.setAlignment(Pos.CENTER);

        recreatedView.setFitHeight(250);
        recreatedView.setFitWidth(250);
        recreatedLayout.setAlignment(Pos.CENTER);

        sinogramLayout.getChildren().addAll(sinogramLabel, sinogramView, sinogramButton);
        recreatedLayout.getChildren().addAll(recreatedLabel, recreatedView, recreatedButton);

        sinogramButton.setOnMouseClicked(event -> {
            generateSinogram();
        });

    }

    public void setBufferedImage(BufferedImage bufferedImage){
        this.bufferedImage = bufferedImage;
        transform.setImage(bufferedImage);
    }

    public void generateSinogram(){
        if(bufferedImage == null){
            throw new NullPointerException("No image to process");
        }
        sinogram = transform.generateSinogram();
        Image image = SwingFXUtils.toFXImage(sinogram, null);
        sinogramView.setImage(image);
    }
}
