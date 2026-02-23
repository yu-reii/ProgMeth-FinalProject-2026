package entity;

import entity.tile.Tile;
import javafx.scene.paint.Color;
import java.util.Stack;

public class Player {
    private String name;
    private Color color;
    private Tile currentTile;
    private Stack<Tile> history; // จำเส้นทางที่เดินผ่านมาเผื่อโดนสั่งถอยหลัง

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.history = new Stack<>();
    }

    public String getName() { return name; }
    public Color getColor() { return color; }
    public Tile getCurrentTile() { return currentTile; }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }

    // เดินหน้า (จดจำช่องที่เพิ่งจากมาลงใน Stack)
    public void moveForward(Tile nextTile) {
        if (this.currentTile != null) {
            history.push(this.currentTile);
        }
        this.currentTile = nextTile;
    }

    // ถอยหลัง (ดึงช่องล่าสุดที่เดินผ่านมากลับมาใช้งาน)
    public void moveBackward(int steps) {
        for (int i = 0; i < steps; i++) {
            if (!history.isEmpty()) {
                this.currentTile = history.pop();
            }
        }
    }

    // กลับจุดเริ่มต้น (โดนพายุ)
    public void returnToStart(Tile startTile) {
        this.currentTile = startTile;
        this.history.clear(); // ล้างประวัติการเดินทิ้ง
    }
    // เพิ่มเมธอดนี้ต่อท้ายใน Player.java
    public Stack<Tile> getHistory() {
        return history;
    }
}