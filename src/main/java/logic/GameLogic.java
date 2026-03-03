package logic;

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

    private Map<Player, Tile> previousTiles;
    // 🌟 ตัวแปรใหม่: จำประวัติการเดินทั้งหมดของแต่ละคน เพื่อใช้ตอนถอยหลัง
    private Map<Player, List<Tile>> playerPaths;

    public GameLogic(GameUIListener uiListener) {
        this.uiListener = uiListener;
        this.player1 = new Player("Player 1", Color.DODGERBLUE);
        this.player2 = new Player("Player 2", Color.LIMEGREEN);
        this.mapManager = new MapManager();
        this.previousTiles = new HashMap<>();
        this.playerPaths = new HashMap<>();
    }

    public void setupGame() {
        mapManager.generateRandomMap();
        Tile startTile = mapManager.getStartTile();

        player1.setCurrentTile(startTile);
        player2.setCurrentTile(startTile);

        isPlayer1Turn = true;
        remainingSteps = 0;
        previousTiles.clear();

        // เริ่มต้นประวัติการเดินที่จุด Start
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
        } else if (choices.size() == 1) {
            Tile nextTile = choices.get(0);
            previousTiles.put(currentPlayer, current);
            currentPlayer.moveForward(nextTile);

            // 🌟 บันทึกเส้นทางลงประวัติ
            playerPaths.get(currentPlayer).add(nextTile);

            remainingSteps--;
            uiListener.onBoardUpdate(null);

            Timeline delay = new Timeline(new KeyFrame(Duration.millis(300), e -> processMovement()));
            delay.play();
        } else {
            uiListener.onIntersection(choices, remainingSteps);
        }
    }

    public void resumeMovementWithChoice(Tile choice) {
        Player currentPlayer = getCurrentPlayer();
        previousTiles.put(currentPlayer, currentPlayer.getCurrentTile());
        currentPlayer.moveForward(choice);

        playerPaths.get(currentPlayer).add(choice); // บันทึกเส้นทาง

        remainingSteps--;
        uiListener.onBoardUpdate(null);

        Timeline delay = new Timeline(new KeyFrame(Duration.millis(300), e -> processMovement()));
        delay.play();
    }

    private void handleLanding() {
        Player currentPlayer = getCurrentPlayer();
        Tile landedTile = currentPlayer.getCurrentTile();

        // 🌟 แยกระบบช่องการ์ดออกมาต่างหาก
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

    // 🌟 ฟังก์ชันใหม่: ประมวลผลผลลัพธ์จากการ์ด
    private void executeCardEffect(int cardType) {
        if (cardType == 1) {
            // เดินหน้า 5 ช่อง
            this.remainingSteps = 5;
            processMovement();
        } else if (cardType == 2) {
            this.remainingSteps = 3;
        } else if (cardType == 3) {
            // เพื่อนถอยหลัง 3 ช่อง
            animateBackward(getEnemyPlayer(), 3, () -> {
                isPlayer1Turn = !isPlayer1Turn;
                uiListener.onTurnEnded("Enemy was pushed back 3 steps by the Card!");
            });
        } else if (cardType == 4) {
            // เพื่อนถอยหลัง 5 ช่อง
            animateBackward(getEnemyPlayer(), 5, () -> {
                isPlayer1Turn = !isPlayer1Turn;
                uiListener.onTurnEnded("Enemy was pushed back 5 steps by the Card!");
            });
        } else if (cardType == 5) {
            // return enemy to start
            getEnemyPlayer().returnToStart(getMapManager().getStartTile());
        }
    }

    // 🌟 ฟังก์ชันใหม่: ถอยหลังทีละก้าวแบบแอนิเมชันตามรอยเดิม
    private void animateBackward(Player p, int stepsLeft, Runnable onFinish) {
        List<Tile> path = playerPaths.get(p);

        if (stepsLeft <= 0 || path.size() <= 1) {
            onFinish.run(); // ถอยเสร็จแล้ว
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

        Timeline delay = new Timeline(new KeyFrame(Duration.millis(300), e -> animateBackward(p, stepsLeft - 1, onFinish)));
        delay.play();
    }

    private void applyTileEffect(Tile landedTile) {
        Player currentPlayer = getCurrentPlayer();
        Player enemyPlayer = getEnemyPlayer();

        String effectMessage = landedTile.applyAction(currentPlayer, enemyPlayer);

        if (landedTile instanceof TornadoTile || landedTile.getName().equals("Start")) {
            previousTiles.put(currentPlayer, null);
            playerPaths.get(currentPlayer).clear();
            playerPaths.get(currentPlayer).add(mapManager.getStartTile());
        }

        isPlayer1Turn = !isPlayer1Turn;
        uiListener.onTurnEnded(effectMessage);
    }
}