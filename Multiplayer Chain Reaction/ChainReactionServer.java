
import netgame.common.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.Inet4Address;

public class ChainReactionServer extends Hub {
    private final static int PORT = 37829;
    static ChainReactionServer server;
    ChainReaction reaction;
    private int playerCount;
    private HashMap<Integer, Integer> IDHashMap = new HashMap<>();
    private HashMap<Player, Integer> playerHashMap = new HashMap<>();
    private ArrayList<Player> playerList = new ArrayList<>();
    private Integer currentPlayer = 0;

    private ChainReactionServer() throws IOException {
        super(PORT);
        setAutoreset(true);
        reaction = new ChainReaction();
        setAutoreset(true);
    }

    public static void main(String[] args) {
        try {
            server = new ChainReactionServer();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void messageReceived(int playerID, Object message) {
        sendToAll(0);
        currentPlayer = reaction.new GameLoop().ballPlacer((ChainReaction.MouseData) message, playerList.get(currentPlayer).getPlayerColor(), playerCount, reaction);
        gameLoop();
    }

    @Override
    protected void playerDisconnected(int playerID) {
        //DOESNT WORK FIX LATER
        int playerNumber = IDHashMap.get(playerID);
        reaction.removePlayer(playerNumber);
    }

    private void newGame() {
        reaction.setBoard(reaction.startNewGame());
        playerList = new ChainReaction().createPlayerList(playerCount);
        sendToAll(reaction);
        sendToOne(IDHashMap.get(currentPlayer),currentPlayer.toString());
    }

    private void gameLoop() {
        sendToAll(reaction);
        sendToOne(currentPlayer, currentPlayer.toString());
    }


    protected void playerConnected(int playerID) {
        Player player = new Player("TOM", reaction.getColors().get(playerCount), playerCount);
        IDHashMap.put(playerCount, playerID);
        playerHashMap.put(player, playerID);
        playerCount++;

        if (getPlayerList().length == 2) {
            server.shutdownServerSocket();
            newGame();

        }
    }

}

