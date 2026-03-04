package logic;

import entity.tile.Tile;
import java.util.List;
import java.util.function.Consumer;

public interface GameUIListener {
    void onBoardUpdate(List<Tile> highlights);
    void onTurnEnded(String effectMessage);
    void onIntersection(List<Tile> choices, int remainingSteps);

    void onSpecialTileEvent(Tile tile, Runnable onContinue);

    void onCardEvent(Consumer<Integer> onCardChosen);
}