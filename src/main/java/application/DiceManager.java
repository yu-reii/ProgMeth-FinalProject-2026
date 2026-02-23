package application;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Random;
import java.util.function.Consumer;

public class DiceManager {
    private StackPane diceOverlay;
    private StackPane diceContainer;
    private Random dice = new Random();

    public DiceManager() {
        // สร้างแผ่นฟิล์มดำใสๆ เพื่อหรี่แสงจอ
        diceOverlay = new StackPane();
        diceOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        diceOverlay.setVisible(false);

        diceContainer = new StackPane();
        diceOverlay.getChildren().add(diceContainer);
    }

    // ส่ง Overlay ไปแปะในหน้า Main
    public StackPane getDiceOverlay() {
        return diceOverlay;
    }

    // ฟังก์ชันทอยลูกเต๋า (เมื่อทอยเสร็จจะส่งแต้มที่ได้กลับไปผ่าน Consumer)
    public void rollDice(Consumer<Integer> onRollFinished) {
        diceOverlay.setVisible(true);
        diceContainer.setOpacity(1);

        ScaleTransition popUp = new ScaleTransition(Duration.millis(300), diceContainer);
        popUp.setFromX(0.2); popUp.setFromY(0.2);
        popUp.setToX(1.0); popUp.setToY(1.0);
        popUp.play();

        RotateTransition spin = new RotateTransition(Duration.millis(800), diceContainer);
        spin.setByAngle(720);
        spin.play();

        Timeline rollAnimation = new Timeline();
        for (int i = 0; i < 15; i++) {
            rollAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(50 * i), e -> {
                int randomFace = dice.nextInt(6) + 1;
                diceContainer.getChildren().setAll(createRealisticDice(randomFace));
            }));
        }

        rollAnimation.setOnFinished(e -> {
            int finalSteps = dice.nextInt(6) + 1;
            diceContainer.getChildren().setAll(createRealisticDice(finalSteps));

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(ev -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), diceOverlay);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(fadeEv -> {
                    diceOverlay.setVisible(false);
                    diceOverlay.setOpacity(1);
                    onRollFinished.accept(finalSteps); // 🟢 ส่งแต้มกลับไปให้ Main เดินต่อ!
                });
                fadeOut.play();
            });
            pause.play();
        });

        rollAnimation.play();
    }

    // วาดลูกเต๋า
    private StackPane createRealisticDice(int face) {
        StackPane dicePane = new StackPane();
        dicePane.setPrefSize(150, 150);
        dicePane.setMaxSize(150, 150);
        dicePane.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #dcdcdc); " +
                "-fx-background-radius: 25; " +
                "-fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-radius: 25; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 5, 5);");

        GridPane dots = new GridPane();
        dots.setAlignment(Pos.CENTER);
        dots.setHgap(20);
        dots.setVgap(20);

        Circle c1 = new Circle(12, Color.web("#222222"));
        Circle c2 = new Circle(12, Color.web("#222222"));
        Circle c3 = new Circle(12, Color.web("#222222"));
        Circle c4 = new Circle(12, Color.web("#222222"));
        Circle c5 = new Circle(12, Color.web("#222222"));
        Circle c6 = new Circle(12, Color.web("#222222"));
        Circle centerDot = new Circle(15, Color.web("#E74C3C"));

        switch (face) {
            case 1: dots.add(centerDot, 1, 1); break;
            case 2: dots.add(c1, 0, 0); dots.add(c2, 2, 2); break;
            case 3: dots.add(c1, 0, 0); dots.add(centerDot, 1, 1); dots.add(c2, 2, 2); break;
            case 4: dots.add(c1, 0, 0); dots.add(c2, 2, 0); dots.add(c3, 0, 2); dots.add(c4, 2, 2); break;
            case 5: dots.add(c1, 0, 0); dots.add(c2, 2, 0); dots.add(centerDot, 1, 1); dots.add(c3, 0, 2); dots.add(c4, 2, 2); break;
            case 6: dots.add(c1, 0, 0); dots.add(c2, 0, 1); dots.add(c3, 0, 2); dots.add(c4, 2, 0); dots.add(c5, 2, 1); dots.add(c6, 2, 2); break;
        }

        dicePane.getChildren().add(dots);
        return dicePane;
    }
}