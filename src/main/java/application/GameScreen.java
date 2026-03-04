package application;

import entity.tile.Tile;
import entity.Player;
import gui.BoardRenderer;
import gui.DiceController;
import gui.OverlayManager;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
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

    private StackPane p1Node;
    private StackPane p2Node;

    private ImageView p1DiceView;
    private ImageView p2DiceView;

    private Color p1Color;
    private Color p2Color;

    public GameScreen(Stage stage, Image p1Avatar, Image p2Avatar, Color p1Color, Color p2Color) {
        this.stage = stage;
        this.gameLogic = new GameLogic(this);
        this.gameLogic.getPlayer1().setAvatar(p1Avatar);
        this.gameLogic.getPlayer2().setAvatar(p2Avatar);
        this.p1Color = p1Color;
        this.p2Color = p2Color;
    }

    public void show() {
        SoundManager.playMusic("game.wav");
        StackPane mainRoot = new StackPane();

        BorderPane gameRoot = new BorderPane();
        gameRoot.setPadding(new Insets(10));

        // โหลดรูปพื้นหลังหน้า Game
        try {
            Image bgImage = new Image(getClass().getResource("/background/game_bg.png").toExternalForm());
            BackgroundImage bImg = new BackgroundImage(bgImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true));
            gameRoot.setBackground(new Background(bImg));
        } catch (Exception e) {
            System.out.println("⚠️ โหลดรูปพื้นหลังไม่สำเร็จ ใช้สีทึบแทน");
            gameRoot.setBackground(new Background(new BackgroundFill(Color.web("#F0F8FF"), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        // 🌟 แถบด้านบน
        BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(10, 20, 10, 20));

        statusLabel = new Label("Game Start! Player 1's Turn.");
        statusLabel.setFont(Main.getPixelFont(22));
        statusLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-padding: 5 15; -fx-background-radius: 10;");

        HBox statusBox = new HBox(statusLabel);
        statusBox.setAlignment(Pos.CENTER);
        topBar.setCenter(statusBox);

        Button menuBtn = new Button("⚙ MENU");
        menuBtn.setFont(Main.getPixelFont(16));
        menuBtn.setStyle("-fx-background-color: #2C3E50; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");

        topBar.setRight(menuBtn);
        BorderPane.setAlignment(menuBtn, Pos.CENTER_RIGHT);

        // 🌟 รวม Event ของปุ่ม Menu ให้ทำงานสมบูรณ์และใส่เสียง
        menuBtn.setOnAction(e -> {
            SoundManager.playSFX("button.wav"); // 🎵 เพิ่มเสียงกดปุ่ม
            SoundManager.playMusic("rest.wav"); // 🎵 สลับเป็นเพลงพักผ่อนตอนเปิดเมนู Pause

            overlayManager.showPauseMenu(
                    () -> {
                        // Resume
                        SoundManager.playMusic("game.wav"); // 🎵 กลับเข้าเกม -> กลับไปเล่นเพลงเกม
                    },
                    () -> {
                        // Change Map
                        gameLogic.setupGame();
                        statusLabel.setText("New Map Generated! Player 1's Turn.");
                        diceController.setRollDisabled(false);
                        p1DiceView.setVisible(false);
                        p2DiceView.setVisible(false);
                        updateActivePlayerUI();
                        SoundManager.playMusic("game.wav"); // 🎵 เล่นเพลงเกมต่อ
                    },
                    () -> {
                        // Main Menu
                        new StartScreen(stage).show();
                        // (เสียง start.wav จะถูกเรียกอัตโนมัติใน StartScreen.show())
                    }
            );
        });

        gameRoot.setTop(topBar);

        boardRenderer = new BoardRenderer(gameLogic);
        gameRoot.setCenter(boardRenderer.getView());

        overlayManager = new OverlayManager();

        // 🌟 กรอบรูปตัวละคร
        p1Node = createAvatarFrame(gameLogic.getPlayer1().getAvatar(), p1Color);
        p2Node = createAvatarFrame(gameLogic.getPlayer2().getAvatar(), p2Color);

        p1DiceView = new ImageView();
        p1DiceView.setFitWidth(60); p1DiceView.setFitHeight(60);
        p1DiceView.setVisible(false);
        StackPane p1DiceContainer = new StackPane(p1DiceView);

        p2DiceView = new ImageView();
        p2DiceView.setFitWidth(60); p2DiceView.setFitHeight(60);
        p2DiceView.setVisible(false);
        StackPane p2DiceContainer = new StackPane(p2DiceView);

        HBox p1Area = new HBox(20, p1Node, p1DiceContainer);
        p1Area.setAlignment(Pos.CENTER_LEFT);

        HBox p2Area = new HBox(20, p2DiceContainer, p2Node);
        p2Area.setAlignment(Pos.CENTER_RIGHT);

        BorderPane bottomBar = new BorderPane();
        bottomBar.setPadding(new Insets(10, 20, 10, 20));
        bottomBar.setLeft(p1Area);
        bottomBar.setRight(p2Area);

        diceController = new DiceController(gameLogic, overlayManager, statusLabel, p1DiceView, p2DiceView);
        bottomBar.setCenter(diceController.getView());

        gameRoot.setBottom(bottomBar);

        mainRoot.getChildren().addAll(gameRoot, overlayManager.getView());

        gameLogic.setupGame();
        stage.setScene(new Scene(mainRoot, 950, 850));

        updateActivePlayerUI();
    }

    private StackPane createAvatarFrame(Image avatarImg, Color borderColor) {
        ImageView imgView = new ImageView(avatarImg);
        imgView.setFitWidth(80); imgView.setFitHeight(80);
        StackPane frame = new StackPane(imgView);
        frame.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 15;");
        frame.setBorder(new Border(new BorderStroke(borderColor, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(6))));
        frame.setPrefSize(95, 95);
        frame.setStyle(frame.getStyle() + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        return frame;
    }

    private void updateActivePlayerUI() {
        boolean isP1Turn = (gameLogic.getCurrentPlayer() == gameLogic.getPlayer1());
        applyAnim(p1Node, isP1Turn);
        applyAnim(p2Node, !isP1Turn);
    }

    private void applyAnim(StackPane node, boolean isActive) {
        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        st.setToX(isActive ? 1.2 : 0.8); st.setToY(isActive ? 1.2 : 0.8); st.play();
        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        ft.setToValue(isActive ? 1.0 : 0.4); ft.play();
    }

    private void handleGameOver(Player winner) {
        Color winColor = (winner == gameLogic.getPlayer1()) ? p1Color : p2Color;
        overlayManager.showVictory(
                winner.getName(),
                winner.getAvatar(),
                winColor,
                () -> new StartScreen(stage).show() // กดย้อนกลับไปหน้าแรก
        );
    }

    @Override
    public void onTurnEnded(String effectMessage) {
        if (gameLogic.isGameOver()) {
            handleGameOver(gameLogic.getWinner());
            return;
        }

        String nextPlayer = gameLogic.getCurrentPlayer().getName();
        statusLabel.setText("Landed! " + effectMessage + " It's " + nextPlayer + "'s turn.");
        diceController.setRollDisabled(false);
        onBoardUpdate(null);
        updateActivePlayerUI();
    }

    @Override public void onBoardUpdate(List<Tile> h) { boardRenderer.updateBoard(h); }
    @Override public void onCardEvent(java.util.function.Consumer<Integer> c) { overlayManager.showCardEvent(c); }
    @Override public void onSpecialTileEvent(Tile t, Runnable r) { overlayManager.showSpecialEvent(t, r); }
    @Override public void onIntersection(List<Tile> c, int s) { statusLabel.setText("Intersection! Choose your path (" + s + " steps left)"); onBoardUpdate(c); }
}