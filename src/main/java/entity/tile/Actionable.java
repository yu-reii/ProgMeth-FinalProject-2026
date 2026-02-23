package entity.tile;
import entity.Player;

public interface Actionable {
    String applyAction(Player current, Player enemy);
}