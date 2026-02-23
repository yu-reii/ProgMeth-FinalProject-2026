package entity.tile;
import entity.Player;

public class CardTile extends Tile {
    public CardTile(String name, int gridX, int gridY) {
        super(TileType.TREASURE, name, gridX, gridY);
    }
    @Override
    public String applyAction(Player current, Player enemy) {

        return "Drew a Card! (Action incoming)";
    }
}