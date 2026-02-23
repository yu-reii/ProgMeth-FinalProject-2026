package entity.tile;

import entity.Player;

public class GoalTile extends Tile {
    public GoalTile(String name, int gridX, int gridY){
        super(TileType.GOAL,name,gridX,gridY);
    }

    @Override
    public String applyAction(Player current, Player enemy){
        return current.getName() + " wins!";
    }
}
