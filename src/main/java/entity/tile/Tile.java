package entity.tile;

import entity.Player;
import java.util.ArrayList;
import java.util.List;

public abstract class Tile implements Actionable {
    private TileType type;
    private String name;
    private int gridX;
    private int gridY;
    private List<Tile> nextTiles;

    public Tile(TileType type, String name, int gridX, int gridY) {
        this.type = type;
        this.name = name;
        this.gridX = gridX;
        this.gridY = gridY;
        this.nextTiles = new ArrayList<>();
    }

    public void addNextTile(Tile next) { this.nextTiles.add(next); }
    public List<Tile> getNextTiles() { return nextTiles; }
    public TileType getType() { return type; }
    public String getName() { return name; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
}