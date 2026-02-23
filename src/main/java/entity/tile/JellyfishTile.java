package entity.tile;
import entity.Player;

public class JellyfishTile extends Tile {
    public JellyfishTile(String name, int gridX, int gridY) { super(TileType.MOB, name, gridX, gridY); }
    @Override public String applyAction(Player current, Player enemy) {
        current.moveBackward(5); // ถอยหลัง 5
        return "Jellyfish sting! " + current.getName() + " moves back 5 steps.";
    }
}