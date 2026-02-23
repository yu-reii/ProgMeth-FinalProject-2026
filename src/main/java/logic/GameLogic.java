package logic;

import entity.Player;
import entity.tile.TornadoTile;
import entity.tile.Tile;
import javafx.scene.paint.Color; // Only used for player setup, which is fine, or you can move colors to UI.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {
    private MapManager mapManager;
    private GameUIListener uiListener;

    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Player enemyPlayer;

    private boolean isPlayer1Turn = true;
    private int remainingSteps = 0;
    private Map<Player, Tile> previousTiles = new HashMap<>();

    public GameLogic(GameUIListener uiListener) {
        this.uiListener = uiListener;
        this.mapManager = new MapManager();

        // Setup Players
        player1 = new Player("Player 1 (Blue)", Color.DODGERBLUE);
        player2 = new Player("Player 2 (Green)", Color.LIMEGREEN);
    }

    public void setupGame() {
        mapManager.generateRandomMap();
        Tile start = mapManager.getStartTile();

        player1.setCurrentTile(start);
        player2.setCurrentTile(start);

        previousTiles.put(player1, null);
        previousTiles.put(player2, null);

        isPlayer1Turn = true;
        uiListener.onBoardUpdate(null);
    }

    // Called by UI after the dice animation finishes
    public void executeTurn(int stepsRolled) {
        currentPlayer = getCurrentPlayer();
        enemyPlayer = isPlayer1Turn ? player2 : player1;

        remainingSteps = stepsRolled;
        processMovement();
    }

    private void processMovement() {
        if (remainingSteps <= 0) {
            Tile landedTile = currentPlayer.getCurrentTile();
            String effectMessage = landedTile.applyAction(currentPlayer, enemyPlayer);

            //if tornado tile, remove all previous history tiles (go back to start)
            if (landedTile instanceof TornadoTile || landedTile.getName().equals("Start")) {
                previousTiles.put(currentPlayer, null);
            }

            isPlayer1Turn = !isPlayer1Turn;
            uiListener.onTurnEnded(effectMessage);
            return;
        }

        Tile current = currentPlayer.getCurrentTile();
        Tile previous = previousTiles.get(currentPlayer);

        List<Tile> choices = new ArrayList<>(current.getNextTiles());

        if (choices.size() > 1 && previous != null) {
            choices.remove(previous);
        }

        if (choices.isEmpty()) {
            remainingSteps = 0;
            processMovement();
        } else if (choices.size() == 1) {
            Tile nextTile = choices.get(0);
            previousTiles.put(currentPlayer, current);
            currentPlayer.moveForward(nextTile);
            remainingSteps--;
            processMovement(); // Recursive call until steps are 0 or intersection hit
        } else {
            // show intersection
            uiListener.onIntersection(choices, remainingSteps);
        }
    }

    // Called by the UI when the player clicks a highlighted intersection tile
    public void resumeMovementWithChoice(Tile chosenTile) {
        previousTiles.put(currentPlayer, currentPlayer.getCurrentTile());
        currentPlayer.moveForward(chosenTile);
        remainingSteps--;
        processMovement(); // Resume the movement loop
    }

    // Getters for the UI to draw the board
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public Player getCurrentPlayer() { return isPlayer1Turn ? player1 : player2; }
    public MapManager getMapManager() { return mapManager; }
}