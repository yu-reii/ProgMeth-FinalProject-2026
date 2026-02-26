package gui;

import application.Main;
import logic.GameLogic;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Random;

public class DiceController {
    private Button rollBtn;
    private StackPane view;
    private Random dice = new Random();

    private ImageView p1DiceView;
    private ImageView p2DiceView;

    public DiceController(GameLogic gameLogic, OverlayManager overlay, Label statusLabel, ImageView p1Dice, ImageView p2Dice) {
        this.p1DiceView = p1Dice;
        this.p2DiceView = p2Dice;

        rollBtn = new Button("Roll Dice");
        rollBtn.setFont(Main.getPixelFont(22));
        rollBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-padding: 15 40; -fx-background-radius: 10; -fx-cursor: hand;");

        view = new StackPane(rollBtn);
        // 🌟 ล็อกความกว้างไว้ไม่ให้กางไปทับรูปตัวละคร
        view.setMaxWidth(220);
        view.setPadding(new Insets(15));
        view.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 20;");

        rollBtn.setOnAction(e -> {
            rollBtn.setDisable(true);
            String pName = gameLogic.getCurrentPlayer().getName();
            boolean isP1Turn = (gameLogic.getCurrentPlayer() == gameLogic.getPlayer1());

            overlay.showDiceRoll(pName, dice, steps -> {
                if (isP1Turn) {
                    setDiceFace(p1DiceView, steps);
                    p1DiceView.setVisible(true);
                    p2DiceView.setVisible(false);
                } else {
                    setDiceFace(p2DiceView, steps);
                    p2DiceView.setVisible(true);
                    p1DiceView.setVisible(false);
                }

                statusLabel.setText(pName + " rolled " + steps + "! Moving...");
                gameLogic.executeTurn(steps);
            });
        });
    }

    public StackPane getView() { return view; }

    public void setRollDisabled(boolean disable) { rollBtn.setDisable(disable); }

    public static void setDiceFace(ImageView imgView, int face) {
        try {
            imgView.setImage(new Image(DiceController.class.getResource("/dice/dice" + face + ".png").toExternalForm()));
        } catch (Exception e) {}
    }
}