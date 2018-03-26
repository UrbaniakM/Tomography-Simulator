package gui;

import computation.Transform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;

public class TransformImage extends HBox{
    final private VBox sinogramLayout = new VBox(8);
    final private Label sinogramLabel = new Label("Sinogram");
    final private ImageView sinogramView = new ImageView();
    final private Button sinogramButton = new Button("Generate sinogram");

    final private GridPane slidersPane = new GridPane();

    final private Label angleLabel = new Label("Dead angle:");
    final private Slider angleSlider = new Slider(5, 180, 90);
    final private Label angleValue = new Label("90");

    final private Label deltaAlphaLabel = new Label("Delta alpha:");
    final private Slider deltaAlphaSlider = new Slider(1, 45, 2);
    final private Label deltaAlphaValue = new Label("2");

    final private Label numberOfDetectorsLabel = new Label("No. of detectors:");
    final private Slider numberOfDetectorsSlider = new Slider(10, 250, 100);
    final private Label numberOfDetectorsValue = new Label("100");

    final private VBox recreatedLayout = new VBox(8);
    final private Label recreatedLabel = new Label("Recreated image");
    final private ImageView recreatedView = new ImageView();
    final private Button recreatedButton = new Button("Recreate image");
    final private ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList(
            "Final image", "Iteratively (animation)")
    );


    private BufferedImage bufferedImage = null;
    private BufferedImage sinogram = null;
    private BufferedImage reconstructedImage;
    private Transform transform = new Transform();

    public TransformImage(){
        super(5);
        getChildren().addAll(sinogramLayout, recreatedLayout);

        sinogramView.setFitHeight(250);
        sinogramView.setFitWidth(250);
        sinogramLayout.setAlignment(Pos.TOP_CENTER);

        recreatedView.setFitHeight(250);
        recreatedView.setFitWidth(250);
        recreatedLayout.setAlignment(Pos.TOP_CENTER);

        sinogramLayout.getChildren().addAll(sinogramLabel, sinogramView, sinogramButton, slidersPane);
        recreatedLayout.getChildren().addAll(recreatedLabel, recreatedView, recreatedButton, choiceBox);

        sinogramButton.setOnMouseClicked(event -> {
            generateSinogram();
        });

        angleSlider.valueProperty().addListener((obs, oldval, newVal) -> {
                Integer intValue = newVal.intValue();
                angleSlider.setValue(intValue);
                transform.setStructureRange(intValue);
                angleValue.setText(intValue.toString());
        });

        numberOfDetectorsSlider.valueProperty().addListener((obs, oldval, newVal) -> {
            Integer intValue = newVal.intValue();
            numberOfDetectorsSlider.setValue(intValue);
            transform.setNumberOfDetectors(intValue);
            numberOfDetectorsValue.setText(intValue.toString());
        });

        deltaAlphaSlider.valueProperty().addListener((obs, oldval, newVal) -> {
            Integer intValue = newVal.intValue();
            deltaAlphaSlider.setValue(intValue);
            transform.setDeltaAlpha(intValue);
            deltaAlphaValue.setText(intValue.toString());
        });

        slidersPane.add(angleLabel, 0, 0);
        slidersPane.add(angleSlider, 1, 0);
        slidersPane.add(angleValue, 2, 0);
        slidersPane.add(numberOfDetectorsLabel, 0, 1);
        slidersPane.add(numberOfDetectorsSlider, 1, 1);
        slidersPane.add(numberOfDetectorsValue, 2, 1);
        slidersPane.add(deltaAlphaLabel, 0, 2);
        slidersPane.add(deltaAlphaSlider, 1, 2);
        slidersPane.add(deltaAlphaValue, 2, 2);

        choiceBox.getSelectionModel().selectFirst();
        choiceBox.getSelectionModel().selectedIndexProperty().addListener( (observable, val, new_val) -> {
            if(new_val.intValue() == 0){
                // TODO: finalne
            } else {
                // TODO: iteracyjnie
            }
        });

        recreatedButton.setOnMouseClicked(event -> {
            recreateImage();
        });

    }

    public void setBufferedImage(BufferedImage bufferedImage){
        this.bufferedImage = bufferedImage;
        transform.setImage(bufferedImage);
    }

    private void generateSinogram(){
        if(bufferedImage == null){
            throw new NullPointerException("No image to process"); // TODO: alert
        }
        sinogram = transform.generateSinogram();
        Image image = SwingFXUtils.toFXImage(sinogram, null);
        sinogramView.setImage(image);
    }

    private void recreateImage(){
        if(sinogram == null){
            throw new NullPointerException("No sinogram to process"); // TODO: alert
        }
        reconstructedImage = transform.reconstructImage();
        Image image = SwingFXUtils.toFXImage(reconstructedImage, null);
        recreatedView.setImage(image);
    }
}
