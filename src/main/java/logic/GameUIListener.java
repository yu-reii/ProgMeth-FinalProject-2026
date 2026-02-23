package logic;

import entity.tile.Tile;
import java.util.List;

public interface GameUIListener {

    // Called when the board needs to visually refresh
    void onBoardUpdate(List<Tile> highlights);

    // Called when a player lands and the turn ends
    void onTurnEnded(String effectMessage);

    // Called when the logic pauses at an intersection
    void onIntersection(List<Tile> choices, int remainingSteps);

}