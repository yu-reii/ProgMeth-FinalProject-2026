package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ProfileScreen {
    private Stage stage;
    private Image p1SelectedAvatar;
    private Image p2SelectedAvatar;
    private List<ImageView> p1AvatarViews = new ArrayList<>();
    private List<ImageView> p2AvatarViews = new ArrayList<>();

    public ProfileScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.web("#2C3E50"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label title = new Label("CHOOSE YOUR CHARACTER");
        title.setFont(Main.getPixelFont(32));
        title.setTextFill(Color.WHITE);

        Image img1 = loadImageSafely("/profile/pic1.png");
        Image img2 = loadImageSafely("/profile/pic2.png");
        Image img3 = loadImageSafely("/profile/pic3.png");
        Image[] images = {img1, img2, img3};

        p1SelectedAvatar = img1;
        p2SelectedAvatar = img2 != null ? img2 : img1;

        Label errorLabel = new Label("");
        errorLabel.setFont(Main.getPixelFont(20));
        errorLabel.setTextFill(Color.web("#E74C3C"));

        Label p1Label = new Label("Player 1 (Blue):");
        p1Label.setFont(Main.getPixelFont(20)); p1Label.setTextFill(Color.DODGERBLUE);
        HBox p1Box = createAvatarBox(images, true, errorLabel);

        Label p2Label = new Label("Player 2 (Green):");
        p2Label.setFont(Main.getPixelFont(20)); p2Label.setTextFill(Color.LIMEGREEN);
        HBox p2Box = createAvatarBox(images, false, errorLabel);

        Button playBtn = new Button("PLAY NOW!");
        playBtn.setFont(Main.getPixelFont(32));
        playBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-padding: 15 50; -fx-background-radius: 10; -fx-cursor: hand;");

        playBtn.setOnAction(e -> {
            if (p1SelectedAvatar == p2SelectedAvatar && p1SelectedAvatar != null) {
                errorLabel.setText("❌ Players cannot choose the same character!");
                return;
            }
            // ส่งรูปที่เลือก ไปให้ GameScreen
            new GameScreen(stage, p1SelectedAvatar, p2SelectedAvatar).show();
        });

        updateAvatarVisuals();

        root.getChildren().addAll(title, errorLabel, p1Label, p1Box, p2Label, p2Box, playBtn);
        stage.setScene(new Scene(root, 950, 850));
    }

    private HBox createAvatarBox(Image[] images, boolean isPlayer1, Label errorLabel) {
        HBox box = new HBox(30);
        box.setAlignment(Pos.CENTER);
        List<ImageView> currentList = isPlayer1 ? p1AvatarViews : p2AvatarViews;
        currentList.clear();

        for (Image img : images) {
            if (img == null) continue;
            ImageView iv = new ImageView(img);
            iv.setFitWidth(100); iv.setFitHeight(100);
            iv.setStyle("-fx-cursor: hand;");

            iv.setOnMouseClicked(e -> {
                if(isPlayer1) {
                    if (img == p2SelectedAvatar) {
                        errorLabel.setText("❌ Player 2 already selected this!");
                        return;
                    }
                    p1SelectedAvatar = img;
                } else {
                    if (img == p1SelectedAvatar) {
                        errorLabel.setText("❌ Player 1 already selected this!");
                        return;
                    }
                    p2SelectedAvatar = img;
                }
                errorLabel.setText("");
                updateAvatarVisuals();
            });
            currentList.add(iv);
            box.getChildren().add(iv);
        }
        return box;
    }

    private void updateAvatarVisuals() {
        for (ImageView v : p1AvatarViews) {
            if (v.getImage() == p1SelectedAvatar) {
                v.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, DODGERBLUE, 20, 0.8, 0, 0);");
                v.setOpacity(1.0);
            } else if (v.getImage() == p2SelectedAvatar) {
                v.setStyle("-fx-cursor: default;"); v.setOpacity(0.3);
            } else {
                v.setStyle("-fx-cursor: hand;"); v.setOpacity(1.0);
            }
        }
        for (ImageView v : p2AvatarViews) {
            if (v.getImage() == p2SelectedAvatar) {
                v.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, LIMEGREEN, 20, 0.8, 0, 0);");
                v.setOpacity(1.0);
            } else if (v.getImage() == p1SelectedAvatar) {
                v.setStyle("-fx-cursor: default;"); v.setOpacity(0.3);
            } else {
                v.setStyle("-fx-cursor: hand;"); v.setOpacity(1.0);
            }
        }
    }

    private Image loadImageSafely(String path) {
        try {
            var resource = getClass().getResource(path);
            if (resource != null) return new Image(resource.toExternalForm());
        } catch (Exception e) {}
        return null;
    }
}