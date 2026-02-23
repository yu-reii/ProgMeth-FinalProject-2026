package entity.tile;

import entity.Player;
import logic.GameLogic;

public class SandTile extends Tile{
    @Override
    public void onLanded(Player player, GameLogic gameLogic, GameUIHandler uiHandler){
        uiHandler.showMessage(player.getName() + " landed on a safe space.");
        gameLogic.endTurn();
    }
}
