package application;

import entity.Player;
import entity.tile.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.function.Consumer;

public class BoardRenderer {
    private GridPane boardView;
    private MapManager mapManager;
    private Player player1;
    private Player player2;
    private Consumer<Tile> onTileClicked;

    public BoardRenderer(GridPane boardView, MapManager mapManager, Player player1, Player player2, Consumer<Tile> onTileClicked) {
        this.boardView = boardView;
        this.mapManager = mapManager;
        this.player1 = player1;
        this.player2 = player2;
        this.onTileClicked = onTileClicked;
    }

    public void updateBoard(List<Tile> highlights) {
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
                        box.setOnMouseClicked(e -> {
                            if (onTileClicked != null) onTileClicked.accept(tile);
                        });
                    } else {
                        box.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.5))));
                    }

                    // 🟢 ระบบวาดผู้เล่น: ถ้ารูปไม่ null จะวาดรูปตัวเล็กๆ ถ้าโหลดรูปไม่ติดจะวาดจุดกลมๆ แทน
                    HBox tokens = new HBox(2);
                    tokens.setAlignment(Pos.CENTER);

                    if (player1.getCurrentTile() == tile) {
                        if (player1.getAvatar() != null) {
                            ImageView p1Avatar = new ImageView(player1.getAvatar());
                            p1Avatar.setFitWidth(20); p1Avatar.setFitHeight(20); // 🟢 ให้ตัวละครไซส์เล็กพอดีกับช่อง
                            tokens.getChildren().add(p1Avatar);
                        } else {
                            tokens.getChildren().add(new Circle(8, player1.getColor()));
                        }
                    }
                    if (player2.getCurrentTile() == tile) {
                        if (player2.getAvatar() != null) {
                            ImageView p2Avatar = new ImageView(player2.getAvatar());
                            p2Avatar.setFitWidth(20); p2Avatar.setFitHeight(20);
                            tokens.getChildren().add(p2Avatar);
                        } else {
                            tokens.getChildren().add(new Circle(8, player2.getColor()));
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