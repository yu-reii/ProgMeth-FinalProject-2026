package entity.tile;
import entity.Player;

public class SandTile extends Tile {

    public SandTile(String name, int gridX, int gridY) {
        super(TileType.NORMAL, name, gridX, gridY);
    }

    @Override
    public String applyAction(Player current, Player enemy) {
        return "Safe on sand.";
    }
}