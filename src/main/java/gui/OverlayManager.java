package gui;

import application.Main;
import application.SoundManager;
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

    public void showVictory(String winnerName, Image winnerAvatar, Color winnerColor, Runnable onReturnHome) {
        overlayLayer.getChildren().clear();

        Label winLabel = new Label("🎉 VICTORY! 🎉");
        winLabel.setFont(Main.getPixelFont(60));
        winLabel.setTextFill(Color.GOLD);

        ScaleTransition st = new ScaleTransition(Duration.millis(500), winLabel);
        st.setFromX(0.5); st.setFromY(0.5);
        st.setToX(1.1); st.setToY(1.1);
        st.setCycleCount(Timeline.INDEFINITE);
        st.setAutoReverse(true);
        st.play();

        ImageView avatarView = new ImageView(winnerAvatar);
        avatarView.setFitWidth(180); avatarView.setFitHeight(180);

        String hexColor = String.format("#%02X%02X%02X",
                (int)(winnerColor.getRed() * 255),
                (int)(winnerColor.getGreen() * 255),
                (int)(winnerColor.getBlue() * 255));
        avatarView.setStyle("-fx-effect: dropshadow(three-pass-box, " + hexColor + ", 30, 0.7, 0, 0);");

        Label nameLabel = new Label(winnerName + " IS THE CHAMPION!");
        nameLabel.setFont(Main.getPixelFont(30));
        nameLabel.setTextFill(Color.WHITE);

        Button backHomeBtn = new Button("BACK TO MAIN MENU");
        styleMenuButton(backHomeBtn, "#27AE60");

        backHomeBtn.setOnAction(e -> {
            SoundManager.playSFX("button.wav"); // 🎵 เสียงกดปุ่ม
            overlayLayer.setVisible(false);
            SoundManager.playMusic("start.wav");
            if (onReturnHome != null) onReturnHome.run();
        });

        VBox content = new VBox(30, winLabel, avatarView, nameLabel, backHomeBtn);
        content.setAlignment(Pos.CENTER);

        overlayLayer.getChildren().add(content);
        overlayLayer.setVisible(true);
    }

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

        // เล่นเสียงลูกเต๋า (ยังคงไว้ตามเดิม)
        SoundManager.playSFX("dice.mp3");

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

        List<Integer> cardTypes = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        Collections.shuffle(cardTypes);
        boolean[] hasChosen = {false};

        for (int i = 0; i < 3; i++) {
            final int effectType = cardTypes.get(i);
            ImageView cardView = new ImageView();
            cardView.setFitWidth(200); cardView.setFitHeight(200);
            try { cardView.setImage(new Image(getClass().getResource("/card/back.png").toExternalForm())); } catch (Exception e){}

            StackPane wrapper = new StackPane(cardView);
            wrapper.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

            wrapper.setOnMouseClicked(e -> {
                if (hasChosen[0]) return;

                SoundManager.playSFX("button.wav"); // 🎵 เสียงกดเลือกไพ่

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
                        SoundManager.playSFX("flip.mp3"); // 🎵 เสียงตอนไพ่พลิกหงายหน้าขึ้น

                        try { cView.setImage(new Image(getClass().getResource("/card/card" + cType + ".png").toExternalForm())); } catch(Exception ex){}
                        ScaleTransition flipIn = new ScaleTransition(Duration.millis(300), cView);
                        flipIn.setFromX(0); flipIn.setToX(1);
                        flipIn.play();

                        if (cIndex == 2) {
                            if (effectType == 1) effectDescText.setText("The tides are in your favour today!");
                            else if (effectType == 2) effectDescText.setText("You found a treasure map, Perhaps it will lead you somewhere?");
                            else if (effectType == 3) effectDescText.setText("Ouch! You enemy got sting by an urchin.");
                            else if (effectType == 4) effectDescText.setText(("Oh no! A thunderstorm hinders your enemy's way, or maybe that's a good thing?"));
                            else if (effectType == 5) effectDescText.setText("A portal opens up beneath your enemy's feet.");

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

        // 🎵 ตรวจสอบว่าเป็น Tile อะไร แล้วเล่นเสียงให้ตรงจุด
        if (tile instanceof CrabTile) {
            SoundManager.playSFX("crab.mp3"); // เสียงปู
            imageFileName = "crab.png"; eventName = "CRAB ATTACK!"; effectDesc = "Move backwards!";
        }
        else if (tile instanceof JellyfishTile) {
            SoundManager.playSFX("jelly.mp3"); // เสียงแมงกะพรุน
            imageFileName = "jellyfish.png"; eventName = "JELLYFISH STING!"; effectDesc = "Ouch! You got shocked!";
        }
        else if (tile instanceof TornadoTile) {
            SoundManager.playSFX("tonado.wav"); // เสียงพายุ
            imageFileName = "tornado.png"; eventName = "TORNADO!!"; effectDesc = "Blown back to start!";
        }
        else if (tile instanceof GoalTile) {
            imageFileName = "goal.png"; eventName = "GOAL REACHED!"; effectDesc = "You are the winner!";
        }

        overlayLayer.getChildren().clear();

        Label title = new Label(eventName);
        title.setFont(Main.getPixelFont(50));
        title.setTextFill(Color.GOLD);

        Label desc = new Label(effectDesc);
        desc.setFont(Main.getPixelFont(25));
        desc.setTextFill(Color.WHITE);

        ImageView img = new ImageView();
        img.setFitWidth(256); img.setFitHeight(256);
        try {
            String imagePath = getClass().getResource("/tile/" + imageFileName).toExternalForm();
            img.setImage(new Image(imagePath,256,256,false,false));
            img.setSmooth(false);
        } catch (Exception e) {}

        Button continueBtn = new Button("APPLY EFFECT");
        continueBtn.setFont(Main.getPixelFont(20));
        continueBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 10; -fx-cursor: hand;");
        continueBtn.setOnAction(e -> {
            SoundManager.playSFX("button.wav"); // 🎵 เสียงกดปุ่ม Apply Effect
            overlayLayer.setVisible(false);
            onContinue.run();
        });

        overlayLayer.getChildren().addAll(title, img, desc, continueBtn);
        overlayLayer.setVisible(true);
    }

    public void showPauseMenu(Runnable onResume, Runnable onChangeMap, Runnable onHome) {
        overlayLayer.getChildren().clear();

        Label title = new Label("GAME PAUSED");
        title.setFont(Main.getPixelFont(50));
        title.setTextFill(Color.GOLD);

        Button resumeBtn = new Button("RESUME");
        styleMenuButton(resumeBtn, "#2ECC71");
        resumeBtn.setOnAction(e -> {
            SoundManager.playSFX("button.wav"); // 🎵 เสียงกดปุ่ม Resume
            overlayLayer.setVisible(false);
            if (onResume != null) onResume.run();
        });

        Button changeMapBtn = new Button("CHANGE MAP");
        styleMenuButton(changeMapBtn, "#3498DB");
        changeMapBtn.setOnAction(e -> {
            SoundManager.playSFX("button.wav"); // 🎵 เสียงกดปุ่ม Change Map
            overlayLayer.setVisible(false);
            if (onChangeMap != null) onChangeMap.run();
        });

        Button homeBtn = new Button("MAIN MENU");
        styleMenuButton(homeBtn, "#E74C3C");
        homeBtn.setOnAction(e -> {
            SoundManager.playSFX("button.wav"); // 🎵 เสียงกดปุ่ม Main Menu
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
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 15 30; " +
                "-fx-background-radius: 10; -fx-cursor: hand; -fx-min-width: 300;");
    }
}