package gui; // 🌟 เปลี่ยน package เป็น gui แล้ว

import application.Main;
import logic.GameLogic;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Random;

public class DiceController {
    private ImageView diceView;
    private Button rollBtn;
    private Button restartBtn;
    private HBox view;
    private Random dice = new Random();

    public DiceController(GameLogic gameLogic, OverlayManager overlay, Label statusLabel, Runnable onRestart) {
        diceView = new ImageView();
        diceView.setFitWidth(60);
        diceView.setFitHeight(60);
        setDiceFace(diceView, 6);

        StackPane diceContainer = new StackPane(diceView);
        diceContainer.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 5); -fx-background-radius: 10;");

        rollBtn = new Button("Roll Dice");
        rollBtn.setFont(Main.getPixelFont(18));
        rollBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");

        rollBtn.setOnAction(e -> {
            rollBtn.setDisable(true);
            String pName = gameLogic.getCurrentPlayer().getName();
            // เรียกแอนิเมชันลูกเต๋าใหญ่ใน Overlay
            overlay.showDiceRoll(pName, dice, steps -> {
                setDiceFace(diceView, steps);
                statusLabel.setText(pName + " rolled " + steps + "! Moving...");
                gameLogic.executeTurn(steps);
            });
        });

        restartBtn = new Button("Restart Game 🔄");
        restartBtn.setFont(Main.getPixelFont(18));
        restartBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        restartBtn.setOnAction(e -> {
            onRestart.run();
            rollBtn.setDisable(false);
            setDiceFace(diceView, 6);
        });

        view = new HBox(30, rollBtn, diceContainer, restartBtn);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(15));
    }

    public HBox getView() { return view; }

    public void setRollDisabled(boolean disable) { rollBtn.setDisable(disable); }

    public static void setDiceFace(ImageView imgView, int face) {
        try {
            imgView.setImage(new Image(DiceController.class.getResource("/dice/dice" + face + ".png").toExternalForm()));
        } catch (Exception e) {
            // ถ้าไม่มีรูป ให้ปล่อยผ่านไปก่อน
        }
    }
}