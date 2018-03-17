


import netgame.common.Client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class ChainReactionClient extends Client{
    private final static int PORT = 37829;
    private static int currentPlayer;
    private static ChainReaction reaction2 = new ChainReaction();

    private ChainReactionClient(String hubHostName) throws IOException {
        super(hubHostName, PORT);

    }

    public static void main(String[] args){
        try {
            new ChainReactionClient("localhost");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void messageReceived(Object message) {

        if(message instanceof ChainReaction) {

            reaction2 = new ChainReaction((ChainReaction) message);
        }
        if (message instanceof String){
            new MouseLoop(reaction2);
        }
        if(message instanceof Integer){
            reaction2.close();
        }
    }

    private class MouseLoop implements MouseListener {
        ChainReaction reaction;
        MouseLoop(ChainReaction r) {
            reaction = r;
            reaction.content.addMouseListener(this);
        }

        public void mouseReleased(MouseEvent evt){
            int locationX = evt.getX();
            int locationY = evt.getY();
            reaction.close();
            send(reaction.new MouseData(locationX,locationY));

        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent evt) {
        }

        public void mouseClicked(MouseEvent evt) {
        }


    }
}

