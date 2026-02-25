package gui;

import entity.tile.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import logic.GameLogic;

import java.util.List;

public class BoardRenderer {
    private GridPane boardView;
    private GameLogic gameLogic;

    public BoardRenderer(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        boardView = new GridPane();
        boardView.setHgap(0);
        boardView.setVgap(0);
        boardView.setAlignment(Pos.CENTER);
    }

    public GridPane getView() { return boardView; }

    public void updateBoard(List<Tile> highlights) {
        boardView.getChildren().clear();
        Tile[][] gridTiles = gameLogic.getMapManager().getGridTiles();

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

                    if (highlights != null && highlights.contains(tile)) {
                        box.setBorder(new Border(new BorderStroke(Color.GOLD, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
                        box.setStyle("-fx-cursor: hand;");
                        box.setOnMouseClicked(e -> gameLogic.resumeMovementWithChoice(tile));
                    } else {
                        box.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.5))));
                    }

                    HBox tokens = new HBox(2);
                    tokens.setAlignment(Pos.CENTER);

                    if (gameLogic.getPlayer1().getCurrentTile() == tile) {
                        if (gameLogic.getPlayer1().getAvatar() != null) {
                            ImageView p1Avatar = new ImageView(gameLogic.getPlayer1().getAvatar());
                            p1Avatar.setFitWidth(20); p1Avatar.setFitHeight(20);
                            tokens.getChildren().add(p1Avatar);
                        } else {
                            tokens.getChildren().add(new Circle(8, gameLogic.getPlayer1().getColor()));
                        }
                    }
                    if (gameLogic.getPlayer2().getCurrentTile() == tile) {
                        if (gameLogic.getPlayer2().getAvatar() != null) {
                            ImageView p2Avatar = new ImageView(gameLogic.getPlayer2().getAvatar());
                            p2Avatar.setFitWidth(20); p2Avatar.setFitHeight(20);
                            tokens.getChildren().add(p2Avatar);
                        } else {
                            tokens.getChildren().add(new Circle(8, gameLogic.getPlayer2().getColor()));
                        }
                    }
                    box.getChildren().add(tokens);
                } else {
                    box.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                boardView.add(box, x, y);
            }
        }
    }
}