package entity;

import entity.tile.Tile;

public class Player {
    private String name;
    private Tile currentTile;

    public Player(String name, Tile startTile){
        this.name = name;
        this.currentTile = startTile;
    }

    public Tile getCurrentTile(){
        return this.currentTile;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }
}
