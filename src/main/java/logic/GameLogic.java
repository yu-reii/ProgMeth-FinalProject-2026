package logic;

import application.SoundManager;
import entity.Player;
import entity.tile.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {
    private Player player1;
    private Player player2;
    private MapManager mapManager;
    private GameUIListener uiListener;

    private boolean isPlayer1Turn;
    private int remainingSteps;
    private boolean gameOver = false;
    private Player winner = null;

    private Map<Player, Tile> previousTiles;
    // 🌟 ตัวแปรใหม่: จำประวัติการเดินทั้งหมดของแต่ละคน เพื่อใช้ตอนถอยหลัง
    private Map<Player, List<Tile>> playerPaths;

    private int animationId = 0;

    public GameLogic(GameUIListener uiListener) {
        this.uiListener = uiListener;
        this.player1 = new Player("Player 1", Color.DODGERBLUE);
        this.player2 = new Player("Player 2", Color.LIMEGREEN);
        this.mapManager = new MapManager();
        this.previousTiles = new HashMap<>();
        this.playerPaths = new HashMap<>();
    }

    public void setupGame() {
        animationId++;
        gameOver = false;
        winner = null;

        mapManager.generateRandomMap();
        Tile startTile = mapManager.getStartTile();

        player1.setCurrentTile(startTile);
        player2.setCurrentTile(startTile);

        isPlayer1Turn = true;
        remainingSteps = 0;
        previousTiles.clear();

        playerPaths.clear();
        playerPaths.put(player1, new ArrayList<>(List.of(startTile)));
        playerPaths.put(player2, new ArrayList<>(List.of(startTile)));

        uiListener.onBoardUpdate(null);
    }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public MapManager getMapManager() { return mapManager; }

    public Player getCurrentPlayer() { return isPlayer1Turn ? player1 : player2; }
    public Player getEnemyPlayer() { return isPlayer1Turn ? player2 : player1; }

    public void executeTurn(int steps) {
        this.remainingSteps = steps;
        processMovement();
    }

    private void processMovement() {
        if (remainingSteps <= 0) {
            handleLanding();
            return;
        }

        Player currentPlayer = getCurrentPlayer();
        Tile current = currentPlayer.getCurrentTile();
        Tile previous = previousTiles.get(currentPlayer);

        List<Tile> choices = new ArrayList<>(current.getNextTiles());
        if (choices.size() > 1 && previous != null) {
            choices.remove(previous);
        }

        if (choices.isEmpty()) {
            remainingSteps = 0;
            // 🌟 แก้บั๊กตรงนี้! ถ้าถึงสุดทาง (เช่น GoalTile) ให้บังคับลงจอดทันที ไม่ต้องยืนค้าง
            handleLanding();
            return;
        } else if (choices.size() == 1) {
            Tile nextTile = choices.get(0);
            previousTiles.put(currentPlayer, current);
            currentPlayer.moveForward(nextTile);

            playerPaths.get(currentPlayer).add(nextTile);

            SoundManager.playSFX("walk.wav");

            remainingSteps--;
            uiListener.onBoardUpdate(null);

            int currentAnimId = animationId;
            Timeline delay = new Timeline(new KeyFrame(Duration.millis(300), e -> {
                if (currentAnimId == animationId) {
                    processMovement();
                }
            }));
            delay.play();
        } else {
            uiListener.onIntersection(choices, remainingSteps);
        }
    }

    public void resumeMovementWithChoice(Tile choice) {
        Player currentPlayer = getCurrentPlayer();
        previousTiles.put(currentPlayer, currentPlayer.getCurrentTile());
        currentPlayer.moveForward(choice);

        playerPaths.get(currentPlayer).add(choice);

        SoundManager.playSFX("walk.wav");

        remainingSteps--;
        uiListener.onBoardUpdate(null);

        int currentAnimId = animationId;
        Timeline delay = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            if (currentAnimId == animationId) {
                processMovement();
            }
        }));
        delay.play();
    }

    private void handleLanding() {
        Player currentPlayer = getCurrentPlayer();
        Tile landedTile = currentPlayer.getCurrentTile();

        if (landedTile instanceof CardTile) {
            uiListener.onCardEvent((cardType) -> executeCardEffect(cardType));
            return;
        }

        boolean isSpecial = (landedTile instanceof CrabTile || landedTile instanceof JellyfishTile ||
                landedTile instanceof TornadoTile || landedTile instanceof GoalTile);

        if (isSpecial) {
            uiListener.onSpecialTileEvent(landedTile, () -> applyTileEffect(landedTile));
        } else {
            applyTileEffect(landedTile);
        }
    }

    private void executeCardEffect(int cardType) {
        if (cardType == 1) {
            this.remainingSteps = 5;
            processMovement();
        } else if (cardType == 2) {
            this.remainingSteps = 3;
            processMovement();
        } else if (cardType == 3) {
            animateBackward(getEnemyPlayer(), 3, () -> {
                isPlayer1Turn = !isPlayer1Turn;
                uiListener.onTurnEnded("Enemy was pushed back 3 steps by the Card!");
            });
        } else if (cardType == 4) {
            animateBackward(getEnemyPlayer(), 5, () -> {
                isPlayer1Turn = !isPlayer1Turn;
                uiListener.onTurnEnded("Enemy was pushed back 5 steps by the Card!");
            });
        } else if (cardType == 5) {
            getEnemyPlayer().returnToStart(getMapManager().getStartTile());
            isPlayer1Turn = !isPlayer1Turn;
            uiListener.onTurnEnded("Enemy was sent to a portal back to Start!");
        }
    }

    private void animateBackward(Player p, int stepsLeft, Runnable onFinish) {
        List<Tile> path = playerPaths.get(p);

        if (stepsLeft <= 0 || path.size() <= 1) {
            onFinish.run();
            return;
        }

        // ลบช่องปัจจุบันออก แล้วดึงช่องก่อนหน้ามาเป็นปัจจุบัน
        path.remove(path.size() - 1);
        Tile prevTile = path.get(path.size() - 1);
        p.setCurrentTile(prevTile);

        if (path.size() > 1) {
            previousTiles.put(p, path.get(path.size() - 2));
        } else {
            previousTiles.put(p, null);
        }

        uiListener.onBoardUpdate(null);

        SoundManager.playSFX("crab.mp3");

        int currentAnimId = animationId;
        Timeline delay = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            if (currentAnimId == animationId) {
                animateBackward(p, stepsLeft - 1, onFinish);
            }
        }));
        delay.play();
    }

    private void applyTileEffect(Tile landedTile) {
        Player currentPlayer = getCurrentPlayer();
        String effectMessage = landedTile.applyAction(currentPlayer, getEnemyPlayer());

        if (landedTile instanceof TornadoTile || landedTile.getName().equals("Start")) {
            previousTiles.put(currentPlayer, null);
            playerPaths.get(currentPlayer).clear();
            playerPaths.get(currentPlayer).add(mapManager.getStartTile());
        }

        if (landedTile instanceof GoalTile) {
            checkVictory(currentPlayer);
        }

        isPlayer1Turn = !isPlayer1Turn;
        uiListener.onTurnEnded(effectMessage);
    }

    public boolean isGameOver() { return gameOver; }
    public Player getWinner() { return winner; }
    public void checkVictory(Player player) {
        this.gameOver = true;
        this.winner = player;
    }
}