

import java.awt.*;

/**
 * Defined variables for Players
 */
public class Player {
    private final String playerName;
    private final Color playerColor;
    private final Integer playerNumber;

    public Player(String name, Color color, Integer number){
        playerName = name;
        playerColor = color;
        playerNumber = number;
    }
    public String getPlayerName(){
        return playerName;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public Integer getPlayerNumber(){ return  playerNumber;}

}
