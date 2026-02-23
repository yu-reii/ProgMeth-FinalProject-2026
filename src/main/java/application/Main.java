package application;

import entity.Player;
import entity.tile.*;
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
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    private GridPane boardView;
    private MapManager mapManager;

    private Player player1;
    private Player player2;
    private boolean isPlayer1Turn = true;
    private Label statusLabel;
    private Random dice = new Random();
    private Button rollBtn;

    // 🟢 ตัวแปรใหม่สำหรับคุมการเดินทีละก้าว
    private int remainingSteps = 0;
    private Player currentPlayer;
    private Player enemyPlayer;
    private boolean isMoving = false;

    @Override
    public void start(Stage primaryStage) {
        player1 = new Player("Player 1 (Blue)", Color.DODGERBLUE);
        player2 = new Player("Player 2 (Green)", Color.LIMEGREEN);

        mapManager = new MapManager();
        setupGame();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setBackground(new Background(new BackgroundFill(Color.web("#F0F8FF"), CornerRadii.EMPTY, Insets.EMPTY)));

        statusLabel = new Label("Game Start! Player 1's Turn.");
        statusLabel.setFont(Font.font("Arial", 22));
        HBox topBox = new HBox(statusLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        root.setTop(topBox);

        boardView = new GridPane();
        boardView.setHgap(0);
        boardView.setVgap(0);
        boardView.setAlignment(Pos.CENTER);
        root.setCenter(boardView);

        rollBtn = new Button("Roll Dice 🎲");
        rollBtn.setFont(Font.font("Arial", 18));
        rollBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;");
        rollBtn.setOnAction(e -> {
            if (!isMoving) startTurn(); // 🟢 กดได้เฉพาะตอนที่ไม่ได้เดินอยู่
        });

        Button restartBtn = new Button("Restart Game 🔄");
        restartBtn.setFont(Font.font("Arial", 18));
        restartBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;");
        restartBtn.setOnAction(e -> {
            setupGame();
            isPlayer1Turn = true;
            isMoving = false;
            rollBtn.setDisable(false);
            statusLabel.setText("New Game! Player 1's Turn.");
            updateBoard(null);
        });

        HBox bottomBox = new HBox(20, rollBtn, restartBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15));
        root.setBottom(bottomBox);

        updateBoard(null);

        Scene scene = new Scene(root, 950, 850);
        primaryStage.setTitle("20x20 Epic Board Game (Interactive Map)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupGame() {
        mapManager.generateRandomMap();
        Tile start = mapManager.getStartTile();

        player1.setCurrentTile(start);
        player2.setCurrentTile(start);
        player1.getHistory().clear();
        player2.getHistory().clear();
    }

    // 🟢 เริ่มต้นทอยลูกเต๋า
    private void startTurn() {
        currentPlayer = isPlayer1Turn ? player1 : player2;
        enemyPlayer = isPlayer1Turn ? player2 : player1;

        remainingSteps = dice.nextInt(6) + 1;
        isMoving = true;
        rollBtn.setDisable(true); // ปิดปุ่มทอยลูกเต๋าชั่วคราว

        statusLabel.setText(currentPlayer.getName() + " rolled " + remainingSteps + "! Moving...");
        processMovement();
    }

    // 🟢 ระบบประมวลผลการเดินทีละก้าว
    private void processMovement() {
        if (remainingSteps <= 0) {
            // เดินครบแล้ว ทำงานเอฟเฟกต์ของช่องนั้น
            Tile landedTile = currentPlayer.getCurrentTile();
            String effectMessage = landedTile.applyAction(currentPlayer, enemyPlayer);

            // 🛠️ แก้ปัญหาข้อ 2: ล้างประวัติการเดินเมื่อเจอกลับจุดเริ่มต้น (Tornado)
            if (landedTile instanceof TornadoTile || landedTile.getName().equals("Start")) {
                currentPlayer.getHistory().clear();
            }

            statusLabel.setText(currentPlayer.getName() + " landed! " + effectMessage);
            isPlayer1Turn = !isPlayer1Turn;
            isMoving = false;
            rollBtn.setDisable(false); // เปิดปุ่มทอยให้คนต่อไป
            updateBoard(null);
            return;
        }

        Tile current = currentPlayer.getCurrentTile();
        Tile previous = currentPlayer.getHistory().isEmpty() ? null : currentPlayer.getHistory().peek();

        List<Tile> choices = new ArrayList<>(current.getNextTiles());
        choices.remove(previous);

        if (choices.isEmpty()) {
            // ทางตัน
            remainingSteps = 0;
            processMovement();
        } else if (choices.size() == 1) {
            // 🚶 มีทางเดียว เดินหน้าอัตโนมัติ
            currentPlayer.moveForward(choices.get(0));
            remainingSteps--;
            processMovement();
        } else {
            // 🛣️ แก้ปัญหาข้อ 1: เจอทางแยก! หยุดรอให้ผู้เล่นคลิกเลือก
            statusLabel.setText("Intersection! Click on a YELLOW box to choose your path. (" + remainingSteps + " steps left)");
            updateBoard(choices); // วาดกระดานและส่งช่องทางแยกไปไฮไลท์
        }
    }

    // 🟢 อัปเดตการวาดกระดานให้รองรับการคลิกไฮไลท์
    private void updateBoard(List<Tile> highlights) {
        boardView.getChildren().clear();
        Tile[][] gridTiles = mapManager.getGridTiles();

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                Tile tile = gridTiles[y][x];
                StackPane box = new StackPane();
                box.setPrefSize(35, 35);

                if (tile != null) {
                    String imageFileName = "sand.png";
                    if (tile instanceof CrabTile) imageFileName = "crab.png";
                    else if (tile instanceof JellyfishTile) imageFileName = "jellyfish.png";
                    else if (tile instanceof TornadoTile) imageFileName = "tornado.png";
                    else if (tile instanceof CardTile) imageFileName = "card.png";

                    try {
                        String imagePath = getClass().getResource("/tile/" + imageFileName).toExternalForm();
                        Image img = new Image(imagePath);
                        ImageView imageView = new ImageView(img);
                        imageView.setFitWidth(35);
                        imageView.setFitHeight(35);
                        box.getChildren().add(imageView);
                    } catch (Exception e) {
                        box.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                    }

                    // 🌟 ถ้าระบบส่งไฮไลท์มา และช่องนี้เป็นหนึ่งในทางแยกที่ไปได้
                    if (highlights != null && highlights.contains(tile)) {
                        box.setBorder(new Border(new BorderStroke(Color.GOLD, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
                        box.setStyle("-fx-cursor: hand;"); // เปลี่ยนเมาส์เป็นรูปนิ้ว
                        box.setOnMouseClicked(e -> {
                            currentPlayer.moveForward(tile);
                            remainingSteps--;
                            processMovement(); // เมื่อคลิกแล้ว ค่อยสั่งให้เดินก้าวต่อไป
                        });
                    } else {
                        // กรอบปกติ
                        box.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.5))));
                    }

                    HBox tokens = new HBox(2);
                    tokens.setAlignment(Pos.CENTER);
                    if (player1.getCurrentTile() == tile) tokens.getChildren().add(new Circle(8, player1.getColor()));
                    if (player2.getCurrentTile() == tile) tokens.getChildren().add(new Circle(8, player2.getColor()));
                    box.getChildren().add(tokens);
                } else {
                    box.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                }

                boardView.add(box, x, y);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}