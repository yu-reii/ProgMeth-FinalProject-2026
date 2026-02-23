package application;

import entity.Player;
import entity.tile.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import logic.GameLogic;
import logic.GameUIListener;

import java.util.List;
import java.util.Random;

public class Main extends Application implements GameUIListener {

    private GameLogic gameLogic;

    private GridPane boardView;
    private Label statusLabel;
    private Label diceLabel;
    private Button rollBtn;
    private final String[] diceFaces = {"⚀", "⚁", "⚂", "⚃", "⚄", "⚅"};
    private Random dice = new Random();
    private boolean isAnimating = false;

    @Override
    public void start(Stage primaryStage) {
        // Initialize logic and pass 'this' as the UI Listener
        gameLogic = new GameLogic(this);

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

        diceLabel = new Label("🎲");
        diceLabel.setFont(Font.font("Arial", 60));
        diceLabel.setTextFill(Color.DARKORANGE);

        rollBtn = new Button("Roll Dice");
        rollBtn.setFont(Font.font("Arial", 18));
        rollBtn.setStyle("-fx-background-color: #FF5733; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;");
        rollBtn.setOnAction(e -> {
            if (!isAnimating) startDiceRollAnimation();
        });

        Button restartBtn = new Button("Restart Game 🔄");
        restartBtn.setFont(Font.font("Arial", 18));
        restartBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;");
        restartBtn.setOnAction(e -> {
            gameLogic.setupGame();
            isAnimating = false;
            rollBtn.setDisable(false);
            diceLabel.setText("🎲");
            statusLabel.setText("New Game! Player 1's Turn.");
        });

        HBox bottomBox = new HBox(30, rollBtn, diceLabel, restartBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15));
        root.setBottom(bottomBox);

        gameLogic.setupGame(); // Start the game logic

        Scene scene = new Scene(root, 950, 850);
        primaryStage.setTitle("20x20 Epic Board Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startDiceRollAnimation() {
        isAnimating = true;
        rollBtn.setDisable(true);
        Player current = gameLogic.getCurrentPlayer();
        statusLabel.setText(current.getName() + " is rolling...");

        Timeline rollAnimation = new Timeline();
        for (int i = 0; i < 10; i++) {
            rollAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(50 * i), e -> {
                diceLabel.setText(diceFaces[dice.nextInt(6)]);
            }));
        }

        rollAnimation.setOnFinished(e -> {
            int steps = dice.nextInt(6) + 1;
            diceLabel.setText(diceFaces[steps - 1]);
            statusLabel.setText(current.getName() + " rolled " + steps + "! Moving...");

            // Hand control over to the Logic class
            gameLogic.executeTurn(steps);
        });

        rollAnimation.play();
    }

    // --- Implementing the GameUIListener Interface ---

    @Override
    public void onBoardUpdate(List<Tile> highlights) {
        boardView.getChildren().clear();
        Tile[][] gridTiles = gameLogic.getMapManager().getGridTiles();

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                Tile tile = gridTiles[y][x];
                StackPane box = new StackPane();
                box.setPrefSize(35, 35);

                if (tile != null) {
                    // ... (Keep your exact same Image/ImageView loading code here) ...
                    String imageFileName = "sand.png";
                    if (tile instanceof CrabTile) imageFileName = "crab.png";
                    else if (tile instanceof JellyfishTile) imageFileName = "jellyfish.png";
                    else if (tile instanceof TornadoTile) imageFileName = "tornado.png";
                    else if (tile instanceof CardTile) imageFileName = "card.png";
                    else if (tile instanceof GoalTile) imageFileName = "goal.png";

                    try {
                        String imagePath = getClass().getResource("/tile/" + imageFileName).toExternalForm();
                        ImageView imageView = new ImageView(new Image(imagePath));
                        imageView.setFitWidth(35);
                        imageView.setFitHeight(35);
                        box.getChildren().add(imageView);
                    } catch (Exception e) {
                        box.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                    }

                    // Handle Highlights for Intersections
                    if (highlights != null && highlights.contains(tile)) {
                        box.setBorder(new Border(new BorderStroke(Color.GOLD, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
                        box.setStyle("-fx-cursor: hand;");
                        box.setOnMouseClicked(e -> {
                            // Tell the logic which path we chose!
                            gameLogic.resumeMovementWithChoice(tile);
                        });
                    } else {
                        box.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.5))));
                    }

                    // Draw Player Tokens
                    HBox tokens = new HBox(2);
                    tokens.setAlignment(Pos.CENTER);
                    if (gameLogic.getPlayer1().getCurrentTile() == tile) tokens.getChildren().add(new Circle(8, gameLogic.getPlayer1().getColor()));
                    if (gameLogic.getPlayer2().getCurrentTile() == tile) tokens.getChildren().add(new Circle(8, gameLogic.getPlayer2().getColor()));
                    box.getChildren().add(tokens);
                } else {
                    box.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                }

                boardView.add(box, x, y);
            }
        }
    }

    @Override
    public void onIntersection(List<Tile> choices, int remainingSteps) {
        statusLabel.setText("Intersection! Click on a YELLOW box to choose your path. (" + remainingSteps + " steps left)");
        onBoardUpdate(choices); // Highlight the choices
    }

    @Override
    public void onTurnEnded(String effectMessage) {
        Player previousPlayer = gameLogic.getCurrentPlayer() == gameLogic.getPlayer1() ? gameLogic.getPlayer2() : gameLogic.getPlayer1();
        statusLabel.setText(previousPlayer.getName() + " landed! " + effectMessage + " Next player's turn.");

        isAnimating = false;
        rollBtn.setDisable(false);
        onBoardUpdate(null); // Clear highlights and update final positions
    }

    public static void main(String[] args) {
        launch(args);
    }
}