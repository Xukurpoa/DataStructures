import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Tom Mroz
 * Simulates the game of Chain Reaction in a 11x11 grid
 * Supports up to 8 players
 * terrible intro gui
 * Now actually winnable
 * @version 1.7
 */
public class ChainReaction extends JPanel implements Serializable{
    private static ArrayList<Player> playerList = new ArrayList<>();
    static int[] bounds = new int[4];
    private static ArrayList<Color> colors = new ArrayList<>(9);
    private static final int height = 700;
    private static final int width = 625;
    private static int currentPlayer;
    private static int playerAmount;
    Frame frame;
    private Ball[][] board;
    private LinkedBlockingQueue<ExplodeEvent> explodeQueue;
    JPanel content;
    private final static long serialVersionUID = 123456789;

    //Temp constructor
    public ChainReaction() {
        colors.add(Color.RED);
        colors.add(new Color(0, 120, 0));
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.WHITE);
        colors.add(Color.ORANGE);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        colors.add(Color.BLACK);
    }

    //Client placing constructor
    public ChainReaction(ChainReaction c) {
        this.board = c.getBoard();
        colors.add(Color.RED);
        colors.add(new Color(0, 120, 0));
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.WHITE);
        colors.add(Color.ORANGE);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        colors.add(Color.BLACK);
        frame = new Frame("Chain Reaction");
        content = new JPanel();
        frame.setContentPane(content);
        content.setPreferredSize(new Dimension(width,height));
        content.setLayout(new BorderLayout());
        content.add(new DrawingLoop(), BorderLayout.CENTER);

        JLabel message = new JLabel("CHAIN REACTION", JLabel.CENTER);
        message.setForeground(colors.get(4));
        message.setFont(new Font("Monospaced", Font.BOLD, 36));
        message.setOpaque(true);
        message.setBackground(Color.BLACK);
        content.add(message, BorderLayout.NORTH);
        frame.pack();
        repaint();
    }

    /**
     * Runs the program
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {

    }

    public Ball[][] startNewGame() {
        board = new Ball[11][11];
        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 11; y++) {
                if ((x == 0 && y == 0) || (x == 0 && y == (board[0].length - 1)) || (x == (board.length - 1) && y == 0) || (x == (board.length - 1) && y == (board[0].length - 1))) {
                    board[x][y] = new Ball("Corner");

                } else if ((x != 0 && x != (board.length - 1) && y == 0) || (x != 0 && x != (board.length - 1) && y == (board[0].length - 1)) || (x == 0 && y != 0 && y != (board[0].length - 1)) || (x == (board.length - 1) && y != 0 && y != (board[0].length - 1))) {
                    board[x][y] = new Ball("Edge");
                } else {
                    board[x][y] = new Ball("Middle");
                }
            }
        }
        return board;
    }

    public Ball[][] getBoard() {
        return board;
    }

    public void removePlayer(int playerNumber) {
        colors.remove(playerNumber);
        playerAmount--;
    }

    public ArrayList<Color> getColors() {
        return colors;
    }
    public ArrayList<Player> createPlayerList(int players){
        playerAmount = players;
        for (int x = 0; x < playerAmount; x++) {
            playerList.add(new Player("TOM", ChainReaction.colors.get(x), 1));
        }
        currentPlayer = 0;
        playerAmount--;
        return playerList;
    }
    public Player getPlayer(int playerAmount, int currentPlayer){
        playerList = createPlayerList(playerAmount);
        return playerList.get(currentPlayer);
    }
    public void setBoard(Ball[][] balls){
        board = balls;
    }
    public void close(){
        frame.dispose();
    }
    /**
     * Nested class that handles graphics
     */
    private class DrawingLoop extends JPanel {
        private DrawingLoop(){
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(625, 700));
            playerList = createPlayerList(8);
        }
        /**
         * Draws everything mostly lines
         *
         * @param g Graphics object used in drawing
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor((playerList.get(currentPlayer)).getPlayerColor());
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int length = (int) (width * .07);
            int a = length;
            int down = 65;
            bounds[0] = length;
            g.drawLine(length - 1, down - 1, length - 1, 614);
            //vertical lines
            for (int x = 0; x < 11; x++) {

                board[0][x].setHorizontalLine(length);
                length = length + (int) (width * .08);
                g.drawLine(length - 1, down - 1, length - 1, 614);
            }

            bounds[1] = length + 2;
            bounds[2] = down;
            //horizontal lines
            for (int x = 0; x < 11; x++) {
                g.drawLine(a - 1, down - 1, length - 1, down - 1);
                board[x][0].setVerticalLine(down);
                down = down + (int) (width * .08);
            }
            g.drawLine(a, down, length, down);

            for (int x = 0; x < board.length; x++) {
                for (int y = 0; y < board[0].length; y++) {
                    if (board[x][y].getValue() != 0) {
                        GradientPaint gradientPaint = new GradientPaint(board[0][x].getHorizontalLine() + 5, (board[y][0].getVerticalLine()) + 5, board[x][y].getBallColor(), (float) (board[0][x].getHorizontalLine()) + 45, (float) board[y][0].getVerticalLine() + 45, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(board[0][x].getHorizontalLine() + 5, (board[y][0].getVerticalLine()) + 5, 40, 40));
                        g.drawString(String.valueOf(board[x][y].getValue()), ((board[0][x].getHorizontalLine()) + 5), (board[y][0].getVerticalLine()) + 5);

                    }

                }
            }
        }

    }


    /**
     * Handles all of the mouse events
     */

    public class GameLoop{
        /**
         * Called after a mouse click, places the balls, and controls the flow of logic
         * @param m    Inner class representing the color of the balls that are placed as well as their location
         *
         */
         int ballPlacer(MouseData m, Color color, int maxPlayer, ChainReaction reaction){
             Ball[][] board = reaction.getBoard();
            int x = m.getxMouse();
            int y = m.getyMouse();
            boolean moveLegal = isMoveLegal(x, y, color, reaction.bounds);
            int xBallPosition = 0;
            int yBallPosition = 0;
            if (moveLegal == false) {

                return currentPlayer;
            }
            if (moveLegal) {
                for (int a = 0; a < board.length - 1; a++) {
                    if ((((board[0][a].getHorizontalLine()) <= x)) && ((board[0][a + 1].getHorizontalLine()) >= x)) {
                        xBallPosition = a ;

                    }
                }
                for (int a = 0; a < board[0].length - 1; a++) {
                    if ((((board[a][0].getVerticalLine()) <= y)) && (board[a + 1][0].getVerticalLine()) >= y) {
                        yBallPosition = a - 1;
                    }
                }
                if ((y >= 565) && (y < 615)){
                    yBallPosition = 9;
                }
                if ((y >= 615) && (y <= 665)) {
                    yBallPosition = 10;
                }
                if (x >= 542 && x <= bounds[1]) {
                    xBallPosition = 10;
                }
                if (!(color.equals(board[xBallPosition][yBallPosition].getBallColor()) || (board[xBallPosition][yBallPosition].getBallColor() == null))) {
                    return currentPlayer;
                }
                board[xBallPosition][yBallPosition].setValue((board[xBallPosition][yBallPosition].getValue()) + 1);
                board[xBallPosition][yBallPosition].setColor(color);

                if (currentPlayer == maxPlayer) {
                    currentPlayer = 0;
                } else {
                    currentPlayer++;
                }
            }
            explodeQueue = new LinkedBlockingQueue<>();
            for (int a = 0; a < 11; a++) {
                for (int b = 0; b < 11; b++) {
                    if ((board[a][b].getValue() >= board[a][b].getMaxValue())) {
                        explodeQueue.add(new ExplodeEvent(a, b));
                    }
                }
            }

            while (explodeQueue.size() != 0) {
                explode(explodeQueue.peek().getX(), explodeQueue.peek().getY());
                explodeQueue.poll();
            }
            return currentPlayer;
        }
    }

    /**
     * Tests if the mouse clicks are not in an occupied spot
     *
     * @param x     x coordinate of the mouse click
     * @param y     y coordinate of the mouse click
     * @param color Color of the ball placed
     * @return True if the move is legal, false if it is not
     */
    private boolean isMoveLegal(int x, int y, Color color, int[] bounds) {
        if ((x <= bounds[0]) || (x >= bounds[1]) || (y <= 115) || (y >= 665)) {

            System.out.printf("Coordinates: %d ,%d \n Bounds[0]: %d", x, y, bounds[1]);
            return false;
        } else if ((color == null)) {

            System.out.println("Quite a bit of gang activity");
            return true;
        } else {
            System.out.println("Quite a bit of gang activity");
            return true;
        }
    }

    /**
     * Tests if a square is about to explsoe
     *
     * @param a a place in the array of the explosion
     * @param b b place in the array of the explosion
     */
    private void doesExplode(int a, int b) {
        if ((board[a][b].getValue() >= board[a][b].getMaxValue())) {
            explodeQueue.add(new ExplodeEvent(a, b));
        }

    }

    /**
     * Does all the math to see if a square will explode
     *
     * @param a a place in the array of the explosion
     * @param b b place in the array of the explosion
     */
    private void explode(int a, int b) {

        board[a][b].setValue(0);
        if (board[a][b].getMaxValue() == 2) {
            if ((a == 0 && b == 0)) {
                board[a + 1][b].setValue(board[a + 1][b].getValue() + 1);
                board[a][b + 1].setValue(board[a][b + 1].getValue() + 1);

                board[a + 1][b].setColor(board[a][b].getBallColor());
                board[a][b + 1].setColor(board[a][b].getBallColor());

                doesExplode(a + 1, b);
                doesExplode(a, b + 1);

            } else if (a == 0 && b == (board[0].length - 1)) {
                board[a + 1][b].setValue(board[a + 1][b].getValue() + 1);
                board[a][b - 1].setValue(board[a][b - 1].getValue() + 1);

                board[a + 1][b].setColor(board[a][b].getBallColor());
                board[a][b - 1].setColor(board[a][b].getBallColor());

                doesExplode(a + 1, b);
                doesExplode(a, b - 1);

            } else if (a == (board.length - 1) && b == 0) {
                board[a - 1][b].setValue(board[a - 1][b].getValue() + 1);
                board[a][b + 1].setValue(board[a][b + 1].getValue() + 1);

                board[a][b + 1].setColor(board[a][b].getBallColor());
                board[a - 1][b].setColor(board[a][b].getBallColor());

                doesExplode(a, b + 1);
                doesExplode(a - 1, b);
            } else {
                board[a - 1][b].setValue(board[a - 1][b].getValue() + 1);
                board[a][b - 1].setValue(board[a][b - 1].getValue() + 1);

                board[a - 1][b].setColor(board[a][b].getBallColor());
                board[a][b - 1].setColor(board[a][b].getBallColor());

                doesExplode(a - 1, b);
                doesExplode(a, b - 1);
            }
        } else if (board[a][b].getMaxValue() == 3) {
            if (a > 0 && b == 0) {
                board[a - 1][b].setValue(board[a - 1][b].getValue() + 1);
                board[a][b + 1].setValue(board[a][b + 1].getValue() + 1);
                board[a + 1][b].setValue(board[a + 1][b].getValue() + 1);

                board[a + 1][b].setColor(board[a][b].getBallColor());
                board[a][b + 1].setColor(board[a][b].getBallColor());
                board[a - 1][b].setColor(board[a][b].getBallColor());

                doesExplode(a + 1, b);
                doesExplode(a, b + 1);
                doesExplode(a - 1, b);
            } else if (a > 0 && b == board[0].length - 1) {
                board[a - 1][b].setValue(board[a - 1][b].getValue() + 1);
                board[a][b - 1].setValue(board[a][b - 1].getValue() + 1);
                board[a + 1][b].setValue(board[a + 1][b].getValue() + 1);

                board[a + 1][b].setColor(board[a][b].getBallColor());
                board[a - 1][b].setColor(board[a][b].getBallColor());
                board[a][b - 1].setColor(board[a][b].getBallColor());

                doesExplode(a + 1, b);
                doesExplode(a - 1, b);
                doesExplode(a, b - 1);

            } else if (a == 0 && b > 0) {
                board[a][b - 1].setValue(board[a][b - 1].getValue() + 1);
                board[a + 1][b].setValue(board[a + 1][b].getValue() + 1);
                board[a][b + 1].setValue(board[a][b + 1].getValue() + 1);

                board[a][b + 1].setColor(board[a][b].getBallColor());
                board[a][b - 1].setColor(board[a][b].getBallColor());
                board[a + 1][b].setColor(board[a][b].getBallColor());

                doesExplode(a + 1, b);
                doesExplode(a, b + 1);
                doesExplode(a, b - 1);
            } else if(a == board.length - 1 && b > 0){
                board[a][b - 1].setValue(board[a][b - 1].getValue() + 1);
                board[a - 1][b].setValue(board[a - 1][b].getValue() + 1);
                board[a][b + 1].setValue(board[a][b + 1].getValue() + 1);

                board[a][b + 1].setColor(board[a][b].getBallColor());
                board[a - 1][b].setColor(board[a][b].getBallColor());
                board[a][b - 1].setColor(board[a][b].getBallColor());

                doesExplode(a, b + 1);
                doesExplode(a - 1, b);
                doesExplode(a, b - 1);
            }
        } else if (board[a][b].getMaxValue() == 4) {
            board[a + 1][b].setValue(board[a + 1][b].getValue() + 1);
            board[a - 1][b].setValue(board[a - 1][b].getValue() + 1);
            board[a][b + 1].setValue(board[a][b + 1].getValue() + 1);
            board[a][b - 1].setValue(board[a][b - 1].getValue() + 1);

            board[a + 1][b].setColor(board[a][b].getBallColor());
            board[a - 1][b].setColor(board[a][b].getBallColor());
            board[a][b + 1].setColor(board[a][b].getBallColor());
            board[a][b - 1].setColor(board[a][b].getBallColor());

            doesExplode(a + 1, b);
            doesExplode(a, b + 1);
            doesExplode(a - 1, b);
            doesExplode(a, b - 1);
        }

        board[a][b].setColor(null);
        if (isGameOver()) {
            //endGame();
        }
    }

    private boolean isGameOver() {
        int x;
        ArrayList<Integer> gameEnd = new ArrayList<>(8);
        for(x = 0;  x < 8; x++){
            gameEnd.add(0);
        }
        for (x = 0; x < gameEnd.size() - 1; x++) {
            for (int a = 0; a < 11; a++) {
                for (int b = 0; b < 11; b++) {
                    if (board[a][b].getBallColor() != null) {
                        if ((board[a][b].getBallColor().equals(colors.get(x)))) {
                            gameEnd.set(x, gameEnd.get(x)+ 1);
                        }
                    }
                }
            }
            if(gameEnd.get(x) == 0 && x <= playerList.size() - 1){
                playerList.remove(x);
                gameEnd.remove(x);
                playerAmount--;
                colors.remove(x);
            }
        }
        if(playerList.size() == 1){
            return true;
        }
        return false;

    }

    /*void endGame() {

        setBackground(Color.BLACK);
        frame.dispose();
        frame = new Frame("Chain Reaction");
        JPanel content = new JPanel();
        frame.setContentPane(content);
        content.add(new EndMenu());
        frame.pack();

    }*/

    /**
     * subclass defining some essential variables
     */
    private class ExplodeEvent {
        private int x;
        private int y;

        private ExplodeEvent(int a, int b) {
            x = a;
            y = b;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
    class MouseData implements Serializable{
        private int xMouse;
        private int yMouse;
        private final static long serialVersionUID = 123456789;

        public MouseData(int x, int y){
            xMouse = x;
            yMouse = y;
        }
        public int getxMouse() {
            return xMouse;
        }

        public void setxMouse(int xMouse) {
            this.xMouse = xMouse;
        }


        public int getyMouse() {  return yMouse; }

        public void setyMouse(int yMouse) {
            this.yMouse = yMouse;
        }

    }

    static class Frame extends JFrame {
        Frame(String name) {
            this.setName(name);
            this.setLocation(150, 0);
            this.setVisible(true);
            this.setResizable(false);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

    }

}