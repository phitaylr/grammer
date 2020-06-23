package org.trojancs.grammer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.awt.Color;
import java.io.File;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.WindowEvent;
import org.trojancs.grammer.Post;

public class PrimaryController {


    @FXML
    private BorderPane borderPane;

    Post post = null;
    int panelNumber = 0;

    @FXML
    private ChoiceBox imageNchoiceBox;
    @FXML
    private ChoiceBox borderSizeChoiceBox;
    @FXML
    private ChoiceBox aspectRatioChoiceBox;

    @FXML
    private ColorPicker bgColorChooser;

    @FXML
    private Label leftButton, rightButton;

    @FXML
    private void initialize() {
        // number of panels
        imageNchoiceBox.getItems().add("1");
        imageNchoiceBox.getItems().add("2");
        imageNchoiceBox.getItems().add("3");
        imageNchoiceBox.setValue("1");
        EventHandler<ActionEvent> imageNevent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                updateConfiguration();
            }
        };
        imageNchoiceBox.setOnAction(imageNevent);

        // color chooser
        bgColorChooser.setValue(javafx.scene.paint.Color.rgb(255, 255, 255));
        EventHandler<ActionEvent> bgcolorevent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                updateConfiguration();
            }
        };
        bgColorChooser.setOnAction(bgcolorevent);

        // border size
        borderSizeChoiceBox.getItems().add("None");
        borderSizeChoiceBox.getItems().add("Small");
        borderSizeChoiceBox.getItems().add("Medium");
        borderSizeChoiceBox.getItems().add("Large");
        borderSizeChoiceBox.setValue("None");
        EventHandler<ActionEvent> borderSizeCBevent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                updateConfiguration();
            }
        };
        borderSizeChoiceBox.setOnAction(borderSizeCBevent);



        //aspect Ratio
        aspectRatioChoiceBox.getItems().add("Square");
        aspectRatioChoiceBox.getItems().add("Portrait");
        aspectRatioChoiceBox.getItems().add("Landscape");
        aspectRatioChoiceBox.setValue("Square");
        EventHandler<ActionEvent> aspectRatioCBevent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                updateConfiguration();
            }
        };
        aspectRatioChoiceBox.setOnAction(aspectRatioCBevent);

        // init arrow labels to invisible
        leftButton.setVisible(false);
        rightButton.setVisible(false);

        EventHandler<MouseEvent> turnLeftEvent = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                turnPage(-1);
            }
        };
        leftButton.setOnMouseClicked(turnLeftEvent);

        EventHandler<MouseEvent> turnRightEvent = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                turnPage(1);
            }
        };
        rightButton.setOnMouseClicked(turnRightEvent);

    }

    @FXML
    void handleKeyPress(KeyEvent e) {


            if (e.getCode() == KeyCode.LEFT ) {
                turnPage(-1);
            }
            else if (e.getCode() == KeyCode.RIGHT ) {
                turnPage(1);
            }


    }

    void turnPage(int dir){
        if (post != null) {
            if (dir < 0 && panelNumber > 0) {
                panelNumber--;
            } else if (dir > 0 && panelNumber < post.getPanelsN() - 1) {
                panelNumber++;
            }


            updatePanel(false);
        }
        updateArrows();
    }

    void updateArrows(){
        if(post.getPanelsN()>1) {
            if (panelNumber > 0)
                leftButton.setVisible(true);
            else
                leftButton.setVisible(false);
            if (panelNumber < post.getPanelsN() - 1)
                rightButton.setVisible(true);
            else
                rightButton.setVisible(false);

        }else{
            leftButton.setVisible(false);
            rightButton.setVisible(false);
        }
    }

    // MENU
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem closeMenuItem;

    @FXML
    public void handleClose(ActionEvent event) {
        menuBar.getScene().getWindow().fireEvent(
                new WindowEvent(
                        menuBar.getScene().getWindow(),
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    @FXML
    private MenuItem openMenuItem;
    @FXML
    private Button loadImageButton;

    @FXML
    public void loadFile(ActionEvent event) {

        Stage stage = (Stage) menuBar.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            post = new Post(selectedFile, 1, new Color(255, 255, 255), .05, Post.CENTER);
            updateConfiguration();
        }
    }

    // Main Panel
    @FXML
    private FlowPane mainPane;

    private void updateConfiguration() {
        System.out.println("action");
        if (post != null) {
            panelNumber = 0;

            post.setBgColor(new Color((float) bgColorChooser.getValue().getRed(),
                    (float) bgColorChooser.getValue().getGreen(), (float) bgColorChooser.getValue().getBlue(),
                    (float) bgColorChooser.getValue().getOpacity()));

            post.setPanelsN(Integer.parseInt(imageNchoiceBox.getValue().toString()));

            if (borderSizeChoiceBox.getValue().toString().equals("Small")) {
                post.setPercentBorder(.05);
            } else if (borderSizeChoiceBox.getValue().toString().equals("Medium")) {
                post.setPercentBorder(.1);
            } else if (borderSizeChoiceBox.getValue().toString().equals("Large")) {
                post.setPercentBorder(.2);
            } else {
                post.setPercentBorder(0);
            }

            if (aspectRatioChoiceBox.getValue().toString().equals("Square")) {
                post.setAspectRatio(Post.RATIO_SQUARE);

            } else if (aspectRatioChoiceBox.getValue().toString().equals("Portrait")) {
                post.setAspectRatio(Post.RATIO_PORTRAIT);

            } else if (aspectRatioChoiceBox.getValue().toString().equals("Landscape")) {
                post.setAspectRatio(Post.RATIO_LANDSCAPE);
            }


            updatePanel();
        }
       updateArrows();

    }

    @FXML
    public void saveImages(ActionEvent e) {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        post.generateFullResolution();

        FileChooser fileChooser = new FileChooser();

        // Set extension filter for text files

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);


        // Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            Post.savePanel(post.getPanel(0), file);
        }
        for(int i = 1; i < post.getPanelsN(); i++) {
            Post.savePanel(post.getPanel(i), new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().length()-4 )+"-"+(i+1)+".png"));
        }
    }

    private void updatePanel() {
        updatePanel(true);
    }

    private void updatePanel(boolean regen) {
        if (regen)
            post.generatePanels(Post.PREVIEW_DIM);
        Image image = SwingFXUtils.toFXImage(post.getPanel(panelNumber), null);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(mainPane.getHeight());

        mainPane.getChildren().clear();
        mainPane.getChildren().addAll(imageView);
    }
}
