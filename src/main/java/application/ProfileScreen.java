package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
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

    private Color p1SelectedColor = Color.DODGERBLUE;
    private Color p2SelectedColor = Color.LIMEGREEN;

    private List<ImageView> p1AvatarViews = new ArrayList<>();
    private List<ImageView> p2AvatarViews = new ArrayList<>();

    public ProfileScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);

        try {
            Image bgImage = new Image(getClass().getResource("/background/setbc.png").toExternalForm());
            BackgroundImage bImg = new BackgroundImage(bgImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true));
            root.setBackground(new Background(bImg));
        } catch (Exception e) {
            System.out.println("Background image not found, using default color.");
            root.setBackground(new Background(new BackgroundFill(Color.web("#2C3E50"), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        Label title = new Label("CHOOSE YOUR CHARACTER");
        title.setFont(Main.getPixelFont(32));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 3);");

        Image img1 = loadImageSafely("/profile/blue.png");
        Image img2 = loadImageSafely("/profile/green.png");
        Image img3 = loadImageSafely("/profile/purple.png");
        Image img4 = loadImageSafely("/profile/red.png");
        Image[] images = {img1, img2, img3, img4};

        p1SelectedAvatar = img1;
        p2SelectedAvatar = img2 != null ? img2 : img1;

        Label errorLabel = new Label("");
        errorLabel.setFont(Main.getPixelFont(20));
        errorLabel.setTextFill(Color.web("#FF6B6B"));
        errorLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 2);");

        Label p1Label = new Label("Player 1:");
        p1Label.setFont(Main.getPixelFont(20));
        p1Label.setTextFill(Color.WHITE);
        p1Label.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 2);");

        ColorPicker p1ColorPicker = new ColorPicker(p1SelectedColor);
        p1ColorPicker.setOnAction(e -> {
            p1SelectedColor = p1ColorPicker.getValue();
            updateAvatarVisuals();
        });
        HBox p1Header = new HBox(15, p1Label, p1ColorPicker);
        p1Header.setAlignment(Pos.CENTER);
        HBox p1Box = createAvatarBox(images, true, errorLabel);

        Label p2Label = new Label("Player 2:");
        p2Label.setFont(Main.getPixelFont(20));
        p2Label.setTextFill(Color.WHITE);
        p2Label.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 2);");

        ColorPicker p2ColorPicker = new ColorPicker(p2SelectedColor);
        p2ColorPicker.setOnAction(e -> {
            p2SelectedColor = p2ColorPicker.getValue();
            updateAvatarVisuals();
        });
        HBox p2Header = new HBox(15, p2Label, p2ColorPicker);
        p2Header.setAlignment(Pos.CENTER);
        HBox p2Box = createAvatarBox(images, false, errorLabel);

        Button playBtn = new Button("PLAY NOW!");
        playBtn.setFont(Main.getPixelFont(32));
        playBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-padding: 15 50; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        playBtn.setOnAction(e -> {
            if (p1SelectedAvatar == p2SelectedAvatar && p1SelectedAvatar != null) {
                errorLabel.setText("❌ Players cannot choose the same character!");
                return;
            }
            new GameScreen(stage, p1SelectedAvatar, p2SelectedAvatar, p1SelectedColor, p2SelectedColor).show();
        });

        updateAvatarVisuals();

        root.getChildren().addAll(title, errorLabel, p1Header, p1Box, p2Header, p2Box, playBtn);
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
        String p1HexColor = toHexString(p1SelectedColor);
        String p2HexColor = toHexString(p2SelectedColor);

        for (ImageView v : p1AvatarViews) {
            if (v.getImage() == p1SelectedAvatar) {
                v.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, " + p1HexColor + ", 20, 0.8, 0, 0);");
                v.setOpacity(1.0);
            } else if (v.getImage() == p2SelectedAvatar) {
                v.setStyle("-fx-cursor: default;"); v.setOpacity(0.3);
            } else {
                v.setStyle("-fx-cursor: hand;"); v.setOpacity(1.0);
            }
        }
        for (ImageView v : p2AvatarViews) {
            if (v.getImage() == p2SelectedAvatar) {
                v.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, " + p2HexColor + ", 20, 0.8, 0, 0);");
                v.setOpacity(1.0);
            } else if (v.getImage() == p1SelectedAvatar) {
                v.setStyle("-fx-cursor: default;"); v.setOpacity(0.3);
            } else {
                v.setStyle("-fx-cursor: hand;"); v.setOpacity(1.0);
            }
        }
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private Image loadImageSafely(String path) {
        try {
            var resource = getClass().getResource(path);
            if (resource != null) return new Image(resource.toExternalForm());
        } catch (Exception e) {}
        return null;
    }
}