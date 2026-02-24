package logic;

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

        // FIRST MAP
        allMaps.add(new String[]{
                "                    ",
                " OXOCOOOCO          ",
                " O   O   OOOCOOOOC  ",
                " C   X   O   O   O  ",
                " O   O   C   O   OOG",
                "SO   OOOTO   O   T  ",
                " O   O   O   O   O  ",
                " OTOOX   OOT Y   O  ",
                " C   O   O   O   C  ",
                " O   C   C   OOXOO  ",
                " C   O   O   O      ",
                " O   OTOOO   O      ",
                " O   O   X   C      ",
                " O   Y   O   O      ",
                " OOOOO   OOCOOOOO   ",
                " C   O          C   ",
                " O   O          O   ",
                " OOOOOCOYOOCOOOXO   ",
                "                    ",
                "                    "
        });

        // SECOND MAP
        allMaps.add(new String[]{
                "                    ",
                "   OCOOYOOCOOCOOCO  ",
                "   O   O    O    O  ",
                "   O   O    Y    O  ",
                "   O   O    O    X  ",
                "   O   O    O    C  ",
                "   XTOCOTOOOOOOTTO  ",
                "   O      O   O  O  ",
                "   O      C   O  O  ",
                " OOXOOO   X   C  O  ",
                " O    O   O   O  C  ",
                " C    O   O   O  O  ",
                " O    OCTOO   O  C  ",
                " O        O   X  O  ",
                "SO        O   O  C  ",
                " O        O   C  OOG",
                " O        T   O     ",
                " X        O   O     ",
                " OCOXOOCYOOOTOO     ",
                "                    "
        });

        // THIRD MAP
        allMaps.add(new String[]{
                "                    ",
                " TXOO            TOG",
                " O  O            Y  ",
                " O  COXOTOOOOCOYOO  ",
                " C        C      C  ",
                " O        O      O  ",
                " OXOCOO   YOOTOOOO  ",
                " O    O   O     X   ",
                " C    O   O     O   ",
                " O    OOCOOYOO  C   ",
                " XO   Y  O   O  X   ",
                "  O   O  X   C  O   ",
                "  COOOO  O   O  C   ",
                "  Y   T  OCOOO  O   ",
                "  O   C  O      C   ",
                "SCO   O  O      O   ",
                "  O   O  C      Y   ",
                "  O   T  X      O   ",
                "  OOCOOXOOCOTOCOO   ",
                "                    "
        });

        // FOURTH MAP
        allMaps.add(new String[]{
                "                    ",
                " OOXCOOOOCOOOOCOOC  ",
                " C      O  O  O  O  ",
                " O      X  O  Y  O  ",
                " OCOOXCOO  C  OTOO  ",
                "   O    O  O  C  C  ",
                "   O    Y  Y  O  O  ",
                "   C    O  O  X  OOG",
                "   O    OTOO  O  T  ",
                "   OOXOOO  C  C  O  ",
                "   C    O  O  O  C  ",
                "   O    C  OTOOTOO  ",
                "   Y    O  C  O     ",
                "SOCO    X  O  O     ",
                "   O    O  O  C     ",
                "   C    OTOO  O     ",
                "   O    C  O  X     ",
                "   O    Y  O  O     ",
                "   OCOOOO  OXOO     ",
                "                    "
        });

        int randomMapIndex = new Random().nextInt(allMaps.size());
        String[] mapLayout = allMaps.get(randomMapIndex);
        System.out.println("new game! random map generated: " + (randomMapIndex + 1));

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

                if (c == 'O') newTile = new SandTile("Sand", x, y);
                else if (c == 'G') newTile = new GoalTile("Goal", x, y);
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