package entity.tile;
import entity.Player;

public class CrabTile extends Tile {
    public CrabTile(String name, int gridX, int gridY) {
        super(TileType.MOB, name, gridX, gridY);
    }
    @Override
    public String applyAction(Player current, Player enemy) {
        current.moveBackward(3);
        return "Crab attack! " + current.getName() + " moves back 3 steps.";
    }
}