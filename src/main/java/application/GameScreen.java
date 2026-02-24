package application;

import entity.tile.Tile;
import gui.BoardRenderer;
import gui.DiceController;
import gui.OverlayManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.GameLogic;
import logic.GameUIListener;

import java.util.List;

public class GameScreen implements GameUIListener {

    private Stage stage;
    private GameLogic gameLogic;
    private Label statusLabel;

    private BoardRenderer boardRenderer;
    private OverlayManager overlayManager;
    private DiceController diceController;

    public GameScreen(Stage stage, Image p1Avatar, Image p2Avatar) {
        this.stage = stage;
        this.gameLogic = new GameLogic(this);
        this.gameLogic.getPlayer1().setAvatar(p1Avatar);
        this.gameLogic.getPlayer2().setAvatar(p2Avatar);
    }

    public void show() {
        StackPane mainRoot = new StackPane();

        BorderPane gameRoot = new BorderPane();
        gameRoot.setPadding(new Insets(10));

        // 🌟 โหลดรูปพื้นหลังหน้า Game
        try {
            Image bgImage = new Image(getClass().getResource("/background/gamebc.jpg").toExternalForm());
            BackgroundImage bImg = new BackgroundImage(bgImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true)); // ให้รูปขยายเต็มหน้าจอ
            gameRoot.setBackground(new Background(bImg));
        } catch (Exception e) {
            System.out.println("⚠️ โหลดรูปพื้นหลังหน้า Game ไม่สำเร็จ จะใช้สีทึบแทน");
            gameRoot.setBackground(new Background(new BackgroundFill(Color.web("#F0F8FF"), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        statusLabel = new Label("Game Start! Player 1's Turn.");
        statusLabel.setFont(Main.getPixelFont(22));
        // ใส่พื้นหลังให้ตัวอักษรนิดนึง จะได้อ่านง่ายขึ้นเวลาพื้นหลังลายตา
        statusLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-padding: 5 15; -fx-background-radius: 10;");

        HBox topBox = new HBox(statusLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        gameRoot.setTop(topBox);

        boardRenderer = new BoardRenderer(gameLogic);
        gameRoot.setCenter(boardRenderer.getView());

        overlayManager = new OverlayManager();

        diceController = new DiceController(gameLogic, overlayManager, statusLabel, () -> {
            gameLogic.setupGame();
            statusLabel.setText("New Game! Player 1's Turn.");
        });

        // แบคกราวน์โปร่งใสตรงเมนูด้านล่าง
        HBox bottomMenu = diceController.getView();
        bottomMenu.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 15;");
        gameRoot.setBottom(bottomMenu);

        mainRoot.getChildren().addAll(gameRoot, overlayManager.getView());

        gameLogic.setupGame();
        stage.setScene(new Scene(mainRoot, 950, 850));
    }

    @Override
    public void onBoardUpdate(List<Tile> highlights) {
        boardRenderer.updateBoard(highlights);
    }

    @Override
    public void onCardEvent(java.util.function.Consumer<Integer> onCardChosen) {
        overlayManager.showCardEvent(onCardChosen);
    }

    @Override
    public void onSpecialTileEvent(Tile tile, Runnable onContinue) {
        overlayManager.showSpecialEvent(tile, onContinue);
    }

    @Override
    public void onIntersection(List<Tile> choices, int remainingSteps) {
        statusLabel.setText("Intersection! Click on a YELLOW box to choose your path. (" + remainingSteps + " steps left)");
        onBoardUpdate(choices);
    }

    @Override
    public void onTurnEnded(String effectMessage) {
        String nextPlayer = gameLogic.getCurrentPlayer().getName();
        statusLabel.setText("Landed! " + effectMessage + " It's " + nextPlayer + "'s turn.");
        diceController.setRollDisabled(false);
        onBoardUpdate(null);
    }
}