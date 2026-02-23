package entity.tile;
import entity.Player;

public class TornadoTile extends Tile {
    private Tile startTile;
    public TornadoTile(String name, int gridX, int gridY, Tile startTile) {
        super(TileType.TORNADO, name, gridX, gridY);
        this.startTile = startTile;
    }
    @Override public String applyAction(Player current, Player enemy) {
        current.returnToStart(startTile); // กลับจุดเริ่มต้น
        return "TORNADO! " + current.getName() + " is blown back to Start!";
    }
}