package application;

import entity.tile.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {
    private Tile[][] gridTiles;
    private List<Tile> allTiles;
    private Tile startTile;

    public void generateRandomMap() {
        allTiles = new ArrayList<>();
        gridTiles = new Tile[20][20];
        Random random = new Random();

        List<String[]> allMaps = new ArrayList<>();

        // 🗺️ แมพที่ 1 (เมืองตาราง)
        allMaps.add(new String[]{
                "SOOOXOOOOOOOOCOOOOOO",
                "O   O   O    O     O",
                "O   OOOOXOOOOO     O",
                "C   O   O    O     T",
                "OOOOO   OOOOOOOO   O",
                "O   Y   O    Y O   O",
                "OOOOOOOOO    OOOOOOO",
                "O       O    O     O",
                "OOOOOOOXOOOOOOOOOOOO",
                "O   O        O     O",
                "O   OOOOOOCOOOOO   O",
                "O   O      O   O   O",
                "OOOOOOOOOOOO   OOOOO",
                "C   O      O   X   O",
                "O   X   OOOO   O   O",
                "O   O   T  O   OOOOO",
                "OOOOOOOOO  OOOOO   O",
                "O   O      O       O",
                "OOOOOOOOOYOOOOOOOOOO",
                "                   G"
        });

        // 🗺️ แมพที่ 2 (ก้นหอยเขาวงกต)
        allMaps.add(new String[]{
                "SOOOOOOXOOOOOOOOOOOO",
                "O                  O",
                "O OOOOOOCOOOOOOOOO O",
                "O O              O O",
                "O O OOOOOOYOOOOO O O",
                "O O O          O O O",
                "O O O OOOXOOOO O O O",
                "O O O O      O O O O",
                "O O O O OTOO O O O O",
                "O O O O O  O O O O O",
                "O O O O O  O O O O O",
                "O O O O OOOO O O O O",
                "O O O O      O O O O",
                "O O O OOOOOOOO O O O",
                "O O O      O   O O O",
                "O O OOOOOXOOOOOO O O",
                "O O        O     O O",
                "O OOOOOOOOOOOOOOOO O",
                "O                  O",
                "OOOOOOOOOYOOOOOOOOOG"
        });

        // 🗺️ แมพที่ 3 (ทางเดินซิกแซก)
        allMaps.add(new String[]{
                "SOOOOOXOOOOOOOCOOOOO",
                "O     O       O    O",
                "OOOOOOOOOOOOOOOOOO O",
                "O     O       O    O",
                "O OOOOOOYOOOOOOOOOOO",
                "O O   O       O    O",
                "OOOOOOOOOXOOOOOOOO O",
                "O     O       O    O",
                "O OOOOOOOOOCOOOOOOOO",
                "O O   O       O    O",
                "OOOOOOOOOOOOOOOOOO O",
                "O     O       O    O",
                "O OOOOOOOXOOOOOOOOOO",
                "O O   O       O    O",
                "OOOOOOOOOOYOOOOOOO O",
                "O     O       O    O",
                "O OOOOOOOOOOOOOOOO O",
                "O                  O",
                "OOOOOOOOOOOOOOOOOO O",
                "                   G"
        });

        // 🗺️ แมพที่ 4 (สมมาตร)
        allMaps.add(new String[]{
                "SOOOOOOOOOXOOOOOOOOO",
                "O         O        O",
                "O OOOOOOOOOOOOOOOO O",
                "O O       O      O O",
                "O O OOOOOOOOOOOO O O",
                "O O O     O    O O O",
                "O O O OOOOOOOO O O O",
                "O O O O      O O O O",
                "O O O O OOOO O O O O",
                "X O O O O  O O O O C",
                "O O O O O  O O O O O",
                "O O O O OOOO O O O O",
                "O O O O      O O O O",
                "O O O OOOOOOOO O O O",
                "O O O     O    O O O",
                "O O OOOOOOOOOOOO O O",
                "O O       O      O O",
                "O OOOOOOOOOOOOOOOO O",
                "O         O        O",
                "OOOOOOOOOOYOOOOOOOOG"
        });

        int randomMapIndex = random.nextInt(allMaps.size());
        String[] mapLayout = allMaps.get(randomMapIndex);
        System.out.println("เริ่มเกมใหม่! สุ่มได้แผนที่: " + (randomMapIndex + 1));

        // 1. สร้าง StartTile ก่อนเพื่อเตรียมไว้ใช้กับ Tornado
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                if (mapLayout[y].charAt(x) == 'S') {
                    startTile = new SandTile("Start", x, y);
                    gridTiles[y][x] = startTile;
                    allTiles.add(startTile);
                }
            }
        }

        // 2. สร้างช่องอื่นๆ และสุ่มแรนด้อม Action
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                char c = mapLayout[y].charAt(x);

                if (c == 'S' || c == ' ') {
                    continue; // ข้ามช่อง Start (ทำไปแล้ว) และช่องว่าง
                }

                Tile newTile = null;

                if (c == 'G') {
                    newTile = new SandTile("Goal", x, y);
                } else {
                    // 🟢 สุ่มช่องทางเดินปกติทั้งหมดให้เป็น Action สุดเดือด!
                    int chance = random.nextInt(100);

                    if (chance < 40) {
                        newTile = new SandTile("Sand", x, y);
                    } else if (chance < 60) {
                        newTile = new CrabTile("Crab", x, y);
                    } else if (chance < 75) {
                        newTile = new JellyfishTile("Jellyfish", x, y);
                    } else if (chance < 90) {
                        newTile = new TornadoTile("Tornado", x, y, startTile);
                    } else {
                        newTile = new CardTile("Card", x, y);
                    }
                }

                if (newTile != null) {
                    gridTiles[y][x] = newTile;
                    allTiles.add(newTile);
                }
            }
        }

        // 3. เชื่อมต่อเส้นทางให้เดินได้
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                Tile t = gridTiles[y][x];
                if (t != null) {
                    if (x + 1 < 20 && gridTiles[y][x + 1] != null) t.addNextTile(gridTiles[y][x + 1]);
                    if (y + 1 < 20 && gridTiles[y + 1][x] != null) t.addNextTile(gridTiles[y + 1][x]);
                    if (x - 1 >= 0 && gridTiles[y][x - 1] != null) t.addNextTile(gridTiles[y][x - 1]);
                    if (y - 1 >= 0 && gridTiles[y - 1][x] != null) t.addNextTile(gridTiles[y - 1][x]);
                }
            }
        }
    }

    public Tile[][] getGridTiles() { return gridTiles; }
    public Tile getStartTile() { return startTile; }
}