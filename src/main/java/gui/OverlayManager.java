package gui;

import application.Main;
import entity.tile.*;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class OverlayManager {
    private VBox overlayLayer;

    public OverlayManager() {
        overlayLayer = new VBox(20);
        overlayLayer.setAlignment(Pos.CENTER);
        overlayLayer.setBackground(new Background(new BackgroundFill(Color.web("#000000", 0.8), CornerRadii.EMPTY, Insets.EMPTY)));
        overlayLayer.setVisible(false);
    }

    public VBox getView() { return overlayLayer; }

    public void showDiceRoll(String playerName, Random dice, Consumer<Integer> onFinish) {
        overlayLayer.getChildren().clear();

        Label rollingText = new Label(playerName + " is rolling...");
        rollingText.setFont(Main.getPixelFont(35));
        rollingText.setTextFill(Color.WHITE);

        ImageView hugeDiceView = new ImageView();
        hugeDiceView.setFitWidth(150); hugeDiceView.setFitHeight(150);

        StackPane hugeDiceContainer = new StackPane(hugeDiceView);
        hugeDiceContainer.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 20, 0, 0, 10);");

        overlayLayer.getChildren().addAll(rollingText, hugeDiceContainer);
        overlayLayer.setVisible(true);

        RotateTransition rt = new RotateTransition(Duration.millis(800), hugeDiceContainer);
        rt.setByAngle(720);

        ScaleTransition st = new ScaleTransition(Duration.millis(400), hugeDiceContainer);
        st.setByX(0.5); st.setByY(0.5);
        st.setAutoReverse(true);
        st.setCycleCount(2);

        Timeline faceChange = new Timeline();
        for (int i = 0; i < 15; i++) {
            faceChange.getKeyFrames().add(new KeyFrame(Duration.millis(60 * i), e -> {
                DiceController.setDiceFace(hugeDiceView, dice.nextInt(6) + 1);
            }));
        }

        faceChange.setOnFinished(e -> {
            int steps = dice.nextInt(6) + 1;
            hugeDiceContainer.setRotate(0);
            hugeDiceContainer.setScaleX(1); hugeDiceContainer.setScaleY(1);
            DiceController.setDiceFace(hugeDiceView, steps);

            rollingText.setText("Got " + steps + "!");
            rollingText.setTextFill(Color.GOLD);

            Timeline hideDelay = new Timeline(new KeyFrame(Duration.millis(800), event -> {
                overlayLayer.setVisible(false);
                onFinish.accept(steps);
            }));
            hideDelay.play();
        });

        rt.play(); st.play(); faceChange.play();
    }

    public void showCardEvent(Consumer<Integer> onCardChosen) {
        overlayLayer.getChildren().clear();
        Label title = new Label("PICK A CARD!");
        title.setFont(Main.getPixelFont(50));
        title.setTextFill(Color.GOLD);

        HBox cardsBox = new HBox(30);
        cardsBox.setAlignment(Pos.CENTER);

        List<Integer> cardTypes = new ArrayList<>(List.of(1, 2, 3));
        Collections.shuffle(cardTypes);
        boolean[] hasChosen = {false};

        for (int i = 0; i < 3; i++) {
            final int effectType = cardTypes.get(i);
            ImageView cardView = new ImageView();
            cardView.setFitWidth(130); cardView.setFitHeight(190);
            try { cardView.setImage(new Image(getClass().getResource("/card/cardback.png").toExternalForm())); } catch (Exception e){}

            StackPane wrapper = new StackPane(cardView);
            wrapper.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

            wrapper.setOnMouseClicked(e -> {
                if (hasChosen[0]) return;
                hasChosen[0] = true;
                wrapper.setBorder(new Border(new BorderStroke(Color.GOLD, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(6))));

                Label effectDescText = new Label();
                effectDescText.setFont(Main.getPixelFont(30));
                effectDescText.setTextFill(Color.WHITE);

                for (int j = 0; j < 3; j++) {
                    final int cIndex = j;
                    final int cType = cardTypes.get(j);
                    ImageView cView = (ImageView) ((StackPane)cardsBox.getChildren().get(j)).getChildren().get(0);

                    ScaleTransition flipOut = new ScaleTransition(Duration.millis(300), cView);
                    flipOut.setFromX(1); flipOut.setToX(0);
                    flipOut.setOnFinished(ev -> {
                        try { cView.setImage(new Image(getClass().getResource("/card/card" + cType + ".png").toExternalForm())); } catch(Exception ex){}
                        ScaleTransition flipIn = new ScaleTransition(Duration.millis(300), cView);
                        flipIn.setFromX(0); flipIn.setToX(1);
                        flipIn.play();

                        if (cIndex == 2) {
                            if (effectType == 1) effectDescText.setText("Card 1: MOVE FORWARD 5 STEPS!");
                            else if (effectType == 2) effectDescText.setText("Card 2: MOVE BACKWARD 3 STEPS!");
                            else if (effectType == 3) effectDescText.setText("Card 3: ENEMY MOVES BACKWARD 5 STEPS!");

                            overlayLayer.getChildren().add(effectDescText);
                            Timeline delay = new Timeline(new KeyFrame(Duration.millis(2500), waitEvent -> {
                                overlayLayer.setVisible(false);
                                onCardChosen.accept(effectType);
                            }));
                            delay.play();
                        }
                    });
                    flipOut.play();
                }
            });
            cardsBox.getChildren().add(wrapper);
        }
        overlayLayer.getChildren().addAll(title, cardsBox);
        overlayLayer.setVisible(true);
    }

    public void showSpecialEvent(Tile tile, Runnable onContinue) {
        String imageFileName = "sand.png";
        String eventName = "Event!";
        String effectDesc = "Something happened!";

        if (tile instanceof CrabTile) { imageFileName = "crab.png"; eventName = "CRAB ATTACK!"; effectDesc = "Move backwards!"; }
        else if (tile instanceof JellyfishTile) { imageFileName = "jellyfish.png"; eventName = "JELLYFISH STING!"; effectDesc = "Ouch! You got shocked!"; }
        else if (tile instanceof TornadoTile) { imageFileName = "tornado.png"; eventName = "TORNADO!!"; effectDesc = "Blown back to start!"; }
        else if (tile instanceof GoalTile) { imageFileName = "goal.png"; eventName = "GOAL REACHED!"; effectDesc = "You are the winner!"; }

        overlayLayer.getChildren().clear();

        Label title = new Label(eventName);
        title.setFont(Main.getPixelFont(50));
        title.setTextFill(Color.GOLD);

        Label desc = new Label(effectDesc);
        desc.setFont(Main.getPixelFont(25));
        desc.setTextFill(Color.WHITE);

        ImageView img = new ImageView();
        img.setFitWidth(200); img.setFitHeight(200);
        try { img.setImage(new Image(getClass().getResource("/tile/" + imageFileName).toExternalForm())); } catch (Exception e) {}

        Button continueBtn = new Button("APPLY EFFECT");
        continueBtn.setFont(Main.getPixelFont(20));
        continueBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 10; -fx-cursor: hand;");
        continueBtn.setOnAction(e -> {
            overlayLayer.setVisible(false);
            onContinue.run();
        });

        overlayLayer.getChildren().addAll(title, img, desc, continueBtn);
        overlayLayer.setVisible(true);
    }

    // 🌟 ส่วนที่เพิ่มเข้ามา: หน้าต่างเมนู Pause
    public void showPauseMenu(Runnable onResume, Runnable onChangeMap, Runnable onHome) {
        overlayLayer.getChildren().clear();

        Label title = new Label("GAME PAUSED");
        title.setFont(Main.getPixelFont(50));
        title.setTextFill(Color.GOLD);

        Button resumeBtn = new Button("RESUME");
        styleMenuButton(resumeBtn, "#2ECC71");
        resumeBtn.setOnAction(e -> {
            overlayLayer.setVisible(false);
            if (onResume != null) onResume.run();
        });

        Button changeMapBtn = new Button("CHANGE MAP");
        styleMenuButton(changeMapBtn, "#3498DB");
        changeMapBtn.setOnAction(e -> {
            overlayLayer.setVisible(false);
            if (onChangeMap != null) onChangeMap.run();
        });

        Button homeBtn = new Button("MAIN MENU");
        styleMenuButton(homeBtn, "#E74C3C");
        homeBtn.setOnAction(e -> {
            overlayLayer.setVisible(false);
            if (onHome != null) onHome.run();
        });

        VBox menuBox = new VBox(20, title, resumeBtn, changeMapBtn, homeBtn);
        menuBox.setAlignment(Pos.CENTER);

        overlayLayer.getChildren().addAll(menuBox);
        overlayLayer.setVisible(true);
    }

    private void styleMenuButton(Button btn, String color) {
        btn.setFont(Main.getPixelFont(24));
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 10; -fx-cursor: hand; -fx-min-width: 300;");
    }
}