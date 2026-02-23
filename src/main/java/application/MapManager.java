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

        // 🗺️ แมพที่ 1
        allMaps.add(new String[]{
                "SOOOXOOOOOOOOCOOOOOO",
                "   O      O        O",
                "   O   OOOOXOOOO   O",
                "   C   O       O   T",
                "   O   O  OOO  O   O",
                "   O   Y  O O  Y   O",
                "   OOOOO  O O  OOOOO",
                "          O O       ",
                "OOOOOOOXOOO OOOOOOOO",
                "O                  O",
                "O  OOOOOOCOOOOOOO  O",
                "O  O            O  O",
                "O  O  OOOOOOOO  O  O",
                "C  O  O      O  X  O",
                "O  X  O  OO  O  O  O",
                "O  O  T  O   O  O  O",
                "O  OOOOOOO   OOOO  O",
                "O                  O",
                "OOOOOOOOOYOOOOOOOOOO",
                "                   G"
        });

        // 🗺️ แมพที่ 2
        allMaps.add(new String[]{
                "SOOOOOOOXOOOOCOOOOOO",
                "                   O",
                "OOOOOOCOOOOOOOOO   O",
                "O                  O",
                "O  OOOOOOOOXOOOOOOOO",
                "O  O                ",
                "O  OOOOYOOOOOOOOOO C",
                "O                  O",
                "XOOOOOOOOOCOOOOOOO O",
                "                   O",
                "OOOOOOOTOOOOOOOOOO O",
                "O                  O",
                "O OOOOOOOXOOOOOOOOOO",
                "O O                 ",
                "O OOOOOOOOOOOOOYOOOO",
                "O                  O",
                "COOOOOXOOOOOOOOOOO O",
                "                   O",
                "OOOOOOOOOOOOOOOOOO O",
                "                   G"
        });

        // 🗺️ แมพที่ 3
        allMaps.add(new String[]{
                "SOOOOOOOOOOOOOOOOO O",
                "O                  O",
                "O OOOOOOOOOXOOOOOO O",
                "O O              O O",
                "O O OOOOOYOOOO C O O",
                "O O O        O O O O",
                "O O O OOOXOO O O O O",
                "O O O O    O O O O O",
                "O X O O TO O O O C O",
                "O O O O  O O O O O O",
                "O O O OOOO O O O O O",
                "O O O      O O O O O",
                "O O C OOOOOO O Y O O",
                "O O O        O O O O",
                "O O OOOOOXOOOO O O O",
                "O O            O O O",
                "O OOOOOOOOOOOOOO O O",
                "O                O O",
                "OOOOOOOOOCOOOOOOOO O",
                "                   G"
        });

        // 🗺️ แมพที่ 4
        allMaps.add(new String[]{
                "SOOOXOOOOOOOOCOOOOOO",
                "O                  O",
                "O OOOXOOOOOOOOOO O O",
                "O O            O O O",
                "O O OOOOOOYOOO O O O",
                "O O O        O O O O",
                "O O O OOOCOO O O O O",
                "O Y O O    O O O X O",
                "O O O O TO O O O O O",
                "O O O O  O O O O O O",
                "O O O OOOO O C O O O",
                "O O O      O O O O O",
                "O O X OOOOOO O O O O",
                "O C O        O O O O",
                "O O OOOOOCOOOO O O O",
                "O O            O O O",
                "O OOOOOOOOOOOOOO O O",
                "O                  O",
                "OOOOOOOOOXOOOOOOOOOO",
                "                   G"
        });

        int randomMapIndex = new Random().nextInt(allMaps.size());
        String[] mapLayout = allMaps.get(randomMapIndex);
        System.out.println("เริ่มเกมใหม่! สุ่มได้แผนที่: " + (randomMapIndex + 1));

        // รอบที่ 1: ค้นหา Start
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                if (mapLayout[y].charAt(x) == 'S') {
                    startTile = new SandTile("Start", x, y);
                    gridTiles[y][x] = startTile;
                    allTiles.add(startTile);
                }
            }
        }

        // รอบที่ 2: สร้างช่องต่างๆ
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

        // รอบที่ 3: เชื่อมทางเดิน
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

    // Getter เอาไว้ให้ Main ดึงไปใช้
    public Tile[][] getGridTiles() { return gridTiles; }
    public Tile getStartTile() { return startTile; }
}