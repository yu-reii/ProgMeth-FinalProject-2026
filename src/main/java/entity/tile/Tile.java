package entity.tile;

import entity.Player;
import logic.GameLogic;

import java.util.ArrayList;
import java.util.List;

public abstract class Tile {
    private List<Tile> nextTiles = new ArrayList<>();

    public abstract void onLanded(Player player, GameLogic gameLogic, GameUIHandler uiHandler);
}
