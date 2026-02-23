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

        int randomMapIndex = new Random().nextInt(allMaps.size());
        String[] mapLayout = allMaps.get(randomMapIndex);
        System.out.println("เริ่มเกมใหม่! สุ่มได้แผนที่: " + (randomMapIndex + 1));

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                if (mapLayout[y].charAt(x) == 'S') {
                    startTile = new SandTile("Start", x, y);
                    gridTiles[y][x] = startTile;
                    allTiles.add(startTile);
                }
            }
        }

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                char c = mapLayout[y].charAt(x);
                Tile newTile = null;

                if (c == 'O' || c == 'G') newTile = new SandTile((c=='G'?"Goal":"Sand"), x, y);
                else if (c == 'X') newTile = new CrabTile("Crab", x, y);
                else if (c == 'Y') newTile = new JellyfishTile("Jelly", x, y);
                else if (c == 'C') newTile = new CardTile("Card", x, y);
                else if (c == 'T') newTile = new TornadoTile("Tornado", x, y, startTile);

                if (newTile != null) {
                    gridTiles[y][x] = newTile;
                    allTiles.add(newTile);
                }
            }
        }

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