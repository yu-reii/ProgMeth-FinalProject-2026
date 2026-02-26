package application;

import entity.tile.Tile;
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
        StackPane mainRoot = new StackPane();

        BorderPane gameRoot = new BorderPane();
        gameRoot.setPadding(new Insets(10));

        // โหลดรูปพื้นหลังหน้า Game
        try {
            Image bgImage = new Image(getClass().getResource("/background/gamebc.jpg").toExternalForm());
            BackgroundImage bImg = new BackgroundImage(bgImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true));
            gameRoot.setBackground(new Background(bImg));
        } catch (Exception e) {
            gameRoot.setBackground(new Background(new BackgroundFill(Color.web("#F0F8FF"), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        // 🌟 สร้างแถบด้านบน (แยกโซนซ้าย/ขวาชัดเจน แก้ปัญหาข้อความทับปุ่ม)
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

        menuBtn.setOnAction(e -> {
            overlayManager.showPauseMenu(
                    () -> { /* Resume */ },
                    () -> {
                        /* Change Map / Restart */
                        gameLogic.setupGame();
                        statusLabel.setText("New Map Generated! Player 1's Turn.");
                        diceController.setRollDisabled(false);
                        p1DiceView.setVisible(false);
                        p2DiceView.setVisible(false);
                        updateActivePlayerUI();
                    },
                    () -> {
                        /* Main Menu */
                        new StartScreen(stage).show();
                    }
            );
        });

        gameRoot.setTop(topBar);

        boardRenderer = new BoardRenderer(gameLogic);
        gameRoot.setCenter(boardRenderer.getView());

        overlayManager = new OverlayManager();

        // 🌟 สร้างกรอบรูปตัวละคร พร้อมสีที่เลือกมา
        p1Node = createAvatarFrame(gameLogic.getPlayer1().getAvatar(), p1Color);
        p2Node = createAvatarFrame(gameLogic.getPlayer2().getAvatar(), p2Color);

        // 🌟 สร้างหน้าตาของลูกเต๋า P1 (ซ่อนไว้ก่อน)
        p1DiceView = new ImageView();
        p1DiceView.setFitWidth(60); p1DiceView.setFitHeight(60);
        p1DiceView.setVisible(false);
        StackPane p1DiceContainer = new StackPane(p1DiceView);
        p1DiceContainer.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 5);");

        // 🌟 สร้างหน้าตาของลูกเต๋า P2 (ซ่อนไว้ก่อน)
        p2DiceView = new ImageView();
        p2DiceView.setFitWidth(60); p2DiceView.setFitHeight(60);
        p2DiceView.setVisible(false);
        StackPane p2DiceContainer = new StackPane(p2DiceView);
        p2DiceContainer.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 5);");

        // 🌟 จัดกลุ่มให้ P1 (รูป P1 อยู่ซ้าย ลูกเต๋าอยู่ขวา)
        HBox p1Area = new HBox(20, p1Node, p1DiceContainer);
        p1Area.setAlignment(Pos.CENTER_LEFT);

        // 🌟 จัดกลุ่มให้ P2 (ลูกเต๋าอยู่ซ้าย รูป P2 อยู่ขวา)
        HBox p2Area = new HBox(20, p2DiceContainer, p2Node);
        p2Area.setAlignment(Pos.CENTER_RIGHT);

        BorderPane bottomBar = new BorderPane();
        bottomBar.setPadding(new Insets(10, 20, 10, 20));
        bottomBar.setLeft(p1Area);
        bottomBar.setRight(p2Area);

        diceController = new DiceController(gameLogic, overlayManager, statusLabel, p1DiceView, p2DiceView);

        StackPane diceMenu = diceController.getView();
        BorderPane.setAlignment(diceMenu, Pos.CENTER);
        bottomBar.setCenter(diceMenu);

        gameRoot.setBottom(bottomBar);

        mainRoot.getChildren().addAll(gameRoot, overlayManager.getView());

        gameLogic.setupGame();
        stage.setScene(new Scene(mainRoot, 950, 850));

        updateActivePlayerUI();
    }

    private StackPane createAvatarFrame(Image avatarImg, Color borderColor) {
        ImageView imgView = new ImageView();
        if (avatarImg != null) {
            imgView.setImage(avatarImg);
        }
        imgView.setFitWidth(80);
        imgView.setFitHeight(80);

        StackPane frame = new StackPane(imgView);
        frame.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 15;");
        frame.setBorder(new Border(new BorderStroke(borderColor, BorderStrokeStyle.SOLID, new CornerRadii(15), new BorderWidths(6))));
        frame.setPrefSize(95, 95);

        frame.setStyle(frame.getStyle() + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        return frame;
    }

    private void updateActivePlayerUI() {
        boolean isP1Turn = (gameLogic.getCurrentPlayer() == gameLogic.getPlayer1());

        ScaleTransition st1 = new ScaleTransition(Duration.millis(300), p1Node);
        st1.setToX(isP1Turn ? 1.2 : 0.8);
        st1.setToY(isP1Turn ? 1.2 : 0.8);
        st1.play();

        FadeTransition ft1 = new FadeTransition(Duration.millis(300), p1Node);
        ft1.setToValue(isP1Turn ? 1.0 : 0.4);
        ft1.play();

        ScaleTransition st2 = new ScaleTransition(Duration.millis(300), p2Node);
        st2.setToX(!isP1Turn ? 1.2 : 0.8);
        st2.setToY(!isP1Turn ? 1.2 : 0.8);
        st2.play();

        FadeTransition ft2 = new FadeTransition(Duration.millis(300), p2Node);
        ft2.setToValue(!isP1Turn ? 1.0 : 0.4);
        ft2.play();
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

        updateActivePlayerUI();
    }
}