package application;

import entity.tile.*;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class ActionPopupManager {
    private StackPane overlay;
    private VBox contentBox;
    private ImageView imageView;
    private Label messageLabel;

    public ActionPopupManager() {
        // สร้างฉากหลังสีดำโปร่งแสง
        overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setVisible(false);

        contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);

        imageView = new ImageView();
        imageView.setFitWidth(150); // รูปใหญ่ขึ้นมากลางจอ
        imageView.setFitHeight(150);

        messageLabel = new Label();
        javafx.scene.text.Font pixelFont = javafx.scene.text.Font.loadFont(getClass().getResourceAsStream("/font/pixel.ttf"), 28);
        if (pixelFont == null) pixelFont = javafx.scene.text.Font.font("Courier New", 28); // ฟอนต์สำรองถ้าหาไฟล์ไม่เจอ

        messageLabel.setFont(pixelFont);
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0.8, 0, 0);"); // เอา font-weight ออกเพราะเราใช้ฟอนต์โหลดเองแล้ว

        contentBox.getChildren().addAll(imageView, messageLabel);
        overlay.getChildren().add(contentBox);
    }

    public StackPane getOverlay() {
        return overlay;
    }

    // ฟังก์ชันเรียกโชว์ป๊อปอัป
    public void showPopup(Tile landedTile, String effectMessage, Runnable onFinished) {
        // ถ้าเป็นช่องธรรมดา หรือช่อง Start ไม่ต้องโชว์ป๊อปอัป ให้ข้ามไปเลย
        if (landedTile.getName().equals("Normal") || landedTile.getName().equals("Start")) {
            onFinished.run();
            return;
        }

        // เลือกว่าจะโชว์รูปอะไร
        String imageFileName = "sand.png";
        if (landedTile instanceof CrabTile) imageFileName = "crab.png";
        else if (landedTile instanceof JellyfishTile) imageFileName = "jellyfish.png";
        else if (landedTile instanceof TornadoTile) imageFileName = "tornado.png";
        else if (landedTile instanceof CardTile) imageFileName = "card.png";

        try {
            String imagePath = getClass().getResource("/tile/" + imageFileName).toExternalForm();
            imageView.setImage(new Image(imagePath));
        } catch (Exception e) {
            imageView.setImage(null);
        }

        messageLabel.setText(effectMessage);

        overlay.setVisible(true);
        contentBox.setOpacity(1);

        // แอนิเมชันซูมเด้งขึ้นมา
        ScaleTransition scale = new ScaleTransition(Duration.millis(400), contentBox);
        scale.setFromX(0.2); scale.setFromY(0.2);
        scale.setToX(1.0); scale.setToY(1.0);
        scale.play();

        // ค้างไว้ 2 วินาทีให้ผู้เล่นอ่าน แล้วค่อยเฟดหายไป
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            FadeTransition fade = new FadeTransition(Duration.millis(300), overlay);
            fade.setToValue(0);
            fade.setOnFinished(ev -> {
                overlay.setVisible(false);
                overlay.setOpacity(1);
                onFinished.run(); // โชว์จบแล้ว ให้เกมทำงานต่อ (สลับเทิร์น)
            });
            fade.play();
        });
        pause.play();
    }
}