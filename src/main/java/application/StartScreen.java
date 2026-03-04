package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class StartScreen {
    private Stage stage;

    public StartScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);


        try {
            Image bgImage = new Image(getClass().getResource("/background/startbc.png").toExternalForm());
            BackgroundImage bImg = new BackgroundImage(bgImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(200, 200, true, true, false, true));
            root.setBackground(new Background(bImg));
        } catch (Exception e) {
            System.out.println("Start screen background loading failed. Using a flat color");
            root.setBackground(new Background(new BackgroundFill(Color.web("#1A252C"), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        Label title = new Label("PIXEL ADVENTURE");
        title.setFont(Main.getPixelFont(60));
        title.setTextFill(Color.GOLD);
        title.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 5);");

        Button startBtn = new Button("START GAME");
        startBtn.setFont(Main.getPixelFont(32));
        startBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 15 50; -fx-background-radius: 10; -fx-cursor: hand;");

        startBtn.setOnAction(e -> new ProfileScreen(stage).show());

        root.getChildren().addAll(title, startBtn);
        stage.setScene(new Scene(root, 950, 850));
    }
}