package application;

import entity.Player;
import entity.tile.*;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    private Stage window;

    private GridPane boardView;
    private MapManager mapManager;
    private DiceManager diceManager;
    private BoardRenderer boardRenderer;
    private ActionPopupManager actionPopupManager;

    private Player player1;
    private Player player2;
    private boolean isPlayer1Turn = true;

    private Label turnLabel;
    private Label statusLabel;
    private Button rollBtn;

    private int remainingSteps = 0;
    private Player currentPlayer;
    private Player enemyPlayer;
    private boolean isMoving = false;
    private Map<Player, Tile> previousTiles = new HashMap<>();

    private Font pixelFontLarge;
    private Font pixelFontMedium;
    private Font pixelFontHuge;

    // ตัวแปรจำรูปที่ผู้เล่นเลือก และเก็บลิสต์ภาพเพื่อใช้อัปเดตแสงไฮไลท์
    private Image p1SelectedAvatar;
    private Image p2SelectedAvatar;
    private List<ImageView> p1AvatarViews = new ArrayList<>();
    private List<ImageView> p2AvatarViews = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        this.window = primaryStage;

        // 🟢 ระบบโหลดฟอนต์ (เพิ่มการ Print แจ้งเตือนถ้าหาไฟล์ไม่เจอ)
        try {
            var fontStream = getClass().getResourceAsStream("/font/pixel.ttf");
            if (fontStream == null) {
                System.out.println("❌ หาไฟล์ฟอนต์ไม่เจอ! เช็คว่าโฟลเดอร์ font อยู่ใน src/main/resources หรือไม่");
            } else {
                pixelFontHuge = Font.loadFont(getClass().getResourceAsStream("/font/pixel.ttf"), 60);
                pixelFontLarge = Font.loadFont(getClass().getResourceAsStream("/font/pixel.ttf"), 32);
                pixelFontMedium = Font.loadFont(getClass().getResourceAsStream("/font/pixel.ttf"), 20);
            }
            if (pixelFontLarge == null) throw new Exception("Font not found");
        } catch (Exception e) {
            System.out.println("⚠️ โหลดฟอนต์ Pixel ไม่สำเร็จ จะใช้ฟอนต์ตัวหนาธรรมดาแทน");
            pixelFontHuge = Font.font("Courier New", FontWeight.BOLD, 50);
            pixelFontLarge = Font.font("Courier New", FontWeight.BOLD, 28);
            pixelFontMedium = Font.font("Courier New", FontWeight.BOLD, 18);
        }

        player1 = new Player("Player 1 (Blue)", Color.DODGERBLUE);
        player2 = new Player("Player 2 (Green)", Color.LIMEGREEN);

        showStartScreen();

        window.setTitle("20x20 Epic Board Game");
        window.show();
    }

    private void showStartScreen() {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.web("#1A252C"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label title = new Label("PIXEL ADVENTURE");
        title.setFont(pixelFontHuge);
        title.setTextFill(Color.GOLD);
        title.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 5);");

        Button startBtn = new Button("START GAME");
        startBtn.setFont(pixelFontLarge);
        startBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 15 50; -fx-background-radius: 10; -fx-cursor: hand;");
        startBtn.setOnAction(e -> showProfileSelectionScreen());

        root.getChildren().addAll(title, startBtn);
        window.setScene(new Scene(root, 950, 850));
    }

    private void showProfileSelectionScreen() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.web("#2C3E50"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label title = new Label("CHOOSE YOUR CHARACTER");
        title.setFont(pixelFontLarge);
        title.setTextFill(Color.WHITE);

        Image img1 = loadImageSafely("/profile/pic1.png");
        Image img2 = loadImageSafely("/profile/pic2.png");
        Image img3 = loadImageSafely("/profile/pic3.png");
        Image[] images = {img1, img2, img3};

        // ตั้งค่ารูปเริ่มต้นให้ไม่ซ้ำกัน
        p1SelectedAvatar = img1;
        p2SelectedAvatar = img2 != null ? img2 : img1;

        Label errorLabel = new Label(""); // 🟢 ป้ายแจ้งเตือนสีแดงเวลาเลือกซ้ำ
        errorLabel.setFont(pixelFontMedium);
        errorLabel.setTextFill(Color.web("#E74C3C"));

        Label p1Label = new Label("Player 1:");
        p1Label.setFont(pixelFontMedium); p1Label.setTextFill(Color.DODGERBLUE);
        HBox p1Box = createAvatarBox(images, true, errorLabel);

        Label p2Label = new Label("Player 2:");
        p2Label.setFont(pixelFontMedium); p2Label.setTextFill(Color.LIMEGREEN);
        HBox p2Box = createAvatarBox(images, false, errorLabel);

        Button playBtn = new Button("PLAY NOW!");
        playBtn.setFont(pixelFontLarge);
        playBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-padding: 15 50; -fx-background-radius: 10; -fx-cursor: hand;");
        playBtn.setOnAction(e -> {
            if (p1SelectedAvatar == p2SelectedAvatar && p1SelectedAvatar != null) {
                errorLabel.setText("❌ Players cannot choose the same character!");
                return;
            }
            player1.setAvatar(p1SelectedAvatar);
            player2.setAvatar(p2SelectedAvatar);
            showGameScreen();
        });

        // อัปเดตแสงไฮไลท์ครั้งแรก
        updateAvatarVisuals();

        root.getChildren().addAll(title, errorLabel, p1Label, p1Box, p2Label, p2Box, playBtn);
        window.setScene(new Scene(root, 950, 850));
    }

    // 🟢 ระบบสร้างกล่องเลือกรูป พร้อมป้องกันการเลือกซ้ำ
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
                errorLabel.setText(""); // ล้างข้อความเตือนเมื่อเลือกถูก
                updateAvatarVisuals();
            });
            currentList.add(iv);
            box.getChildren().add(iv);
        }
        return box;
    }

    // 🟢 ฟังก์ชันอัปเดตแสงไฮไลท์ และทำรูปที่โดนเลือกไปแล้วให้จางลง (Opacity)
    private void updateAvatarVisuals() {
        for (ImageView v : p1AvatarViews) {
            if (v.getImage() == p1SelectedAvatar) {
                v.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, DODGERBLUE, 20, 0.8, 0, 0);");
                v.setOpacity(1.0);
            } else if (v.getImage() == p2SelectedAvatar) {
                v.setStyle("-fx-cursor: default;");
                v.setOpacity(0.3); // รูปที่ P2 เลือกไปแล้วจะจางลง
            } else {
                v.setStyle("-fx-cursor: hand;");
                v.setOpacity(1.0);
            }
        }

        for (ImageView v : p2AvatarViews) {
            if (v.getImage() == p2SelectedAvatar) {
                v.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, LIMEGREEN, 20, 0.8, 0, 0);");
                v.setOpacity(1.0);
            } else if (v.getImage() == p1SelectedAvatar) {
                v.setStyle("-fx-cursor: default;");
                v.setOpacity(0.3); // รูปที่ P1 เลือกไปแล้วจะจางลง
            } else {
                v.setStyle("-fx-cursor: hand;");
                v.setOpacity(1.0);
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

    // --- ส่วนของ Game Screen ด้านล่างคงเดิม ---
    private void showGameScreen() {
        mapManager = new MapManager();
        diceManager = new DiceManager();
        actionPopupManager = new ActionPopupManager();

        setupGame();

        StackPane mainRoot = new StackPane();
        BorderPane gameLayout = new BorderPane();
        gameLayout.setPadding(new Insets(10));
        gameLayout.setBackground(new Background(new BackgroundFill(Color.web("#F0F8FF"), CornerRadii.EMPTY, Insets.EMPTY)));

        turnLabel = new Label();
        turnLabel.setFont(pixelFontLarge);
        turnLabel.setPadding(new Insets(10, 30, 10, 30));
        updateTurnIndicator();

        statusLabel = new Label("Game Start! Please roll the dice.");
        statusLabel.setFont(pixelFontMedium);
        statusLabel.setTextFill(Color.DARKGRAY);

        VBox topBox = new VBox(10, turnLabel, statusLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        gameLayout.setTop(topBox);

        boardView = new GridPane();
        boardView.setHgap(0);
        boardView.setVgap(0);
        boardView.setAlignment(Pos.CENTER);
        gameLayout.setCenter(boardView);

        boardRenderer = new BoardRenderer(boardView, mapManager, player1, player2, selectedTile -> {
            previousTiles.put(currentPlayer, currentPlayer.getCurrentTile());
            currentPlayer.moveForward(selectedTile);
            remainingSteps--;
            boardRenderer.updateBoard(null);

            PauseTransition delay = new PauseTransition(Duration.millis(300));
            delay.setOnFinished(ev -> processMovement());
            delay.play();
        });

        rollBtn = new Button("Roll Dice 🎲");
        rollBtn.setFont(pixelFontMedium);
        rollBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
        rollBtn.setOnAction(e -> {
            if (!isMoving) startTurn();
        });

        Button restartBtn = new Button("Restart Game 🔄");
        restartBtn.setFont(pixelFontMedium);
        restartBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-padding: 10 30; -fx-background-radius: 8; -fx-cursor: hand;");
        restartBtn.setOnAction(e -> {
            setupGame();
            isPlayer1Turn = true;
            isMoving = false;
            rollBtn.setDisable(false);
            updateTurnIndicator();
            statusLabel.setText("New Game! Please roll the dice.");
            boardRenderer.updateBoard(null);
        });

        HBox bottomBox = new HBox(40, rollBtn, restartBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15));
        gameLayout.setBottom(bottomBox);

        mainRoot.getChildren().addAll(gameLayout, diceManager.getDiceOverlay(), actionPopupManager.getOverlay());
        boardRenderer.updateBoard(null);

        window.setScene(new Scene(mainRoot, 950, 850));
    }

    private void setupGame() {
        mapManager.generateRandomMap();
        Tile start = mapManager.getStartTile();
        player1.setCurrentTile(start);
        player2.setCurrentTile(start);
        previousTiles.put(player1, null);
        previousTiles.put(player2, null);
    }

    private void updateTurnIndicator() {
        if (isPlayer1Turn) {
            turnLabel.setText("🎯 " + player1.getName() + "'s Turn");
            turnLabel.setStyle("-fx-background-color: DODGERBLUE; -fx-text-fill: white; -fx-background-radius: 20;");
        } else {
            turnLabel.setText("🎯 " + player2.getName() + "'s Turn");
            turnLabel.setStyle("-fx-background-color: LIMEGREEN; -fx-text-fill: white; -fx-background-radius: 20;");
        }
    }

    private void startTurn() {
        currentPlayer = isPlayer1Turn ? player1 : player2;
        enemyPlayer = isPlayer1Turn ? player2 : player1;
        isMoving = true;
        rollBtn.setDisable(true);
        statusLabel.setText("Rolling...");

        diceManager.rollDice(steps -> {
            remainingSteps = steps;
            statusLabel.setText("Rolled " + remainingSteps + "! Moving...");
            processMovement();
        });
    }

    private void processMovement() {
        if (remainingSteps <= 0) {
            Tile landedTile = currentPlayer.getCurrentTile();
            String effectMessage = landedTile.applyAction(currentPlayer, enemyPlayer);

            if (landedTile instanceof TornadoTile || landedTile.getName().equals("Start")) {
                previousTiles.put(currentPlayer, null);
            }

            actionPopupManager.showPopup(landedTile, effectMessage, () -> {
                statusLabel.setText("Landed on " + landedTile.getName() + "! " + effectMessage);
                isPlayer1Turn = !isPlayer1Turn;
                updateTurnIndicator();
                isMoving = false;
                rollBtn.setDisable(false);
                boardRenderer.updateBoard(null);
            });
            return;
        }

        Tile current = currentPlayer.getCurrentTile();
        Tile previous = previousTiles.get(currentPlayer);
        List<Tile> choices = new ArrayList<>(current.getNextTiles());

        if (choices.size() > 1 && previous != null) {
            choices.remove(previous);
        }

        if (choices.isEmpty()) {
            remainingSteps = 0;
            processMovement();
        } else if (choices.size() == 1) {
            Tile nextTile = choices.get(0);
            previousTiles.put(currentPlayer, current);
            currentPlayer.moveForward(nextTile);
            remainingSteps--;
            boardRenderer.updateBoard(null);

            PauseTransition delay = new PauseTransition(Duration.millis(300));
            delay.setOnFinished(e -> processMovement());
            delay.play();
        } else {
            statusLabel.setText("Intersection! Click on a YELLOW box to choose your path. (" + remainingSteps + " steps left)");
            boardRenderer.updateBoard(choices);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}