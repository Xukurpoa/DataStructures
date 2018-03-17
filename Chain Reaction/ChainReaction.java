import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.awt.geom.Ellipse2D;


/**
 * @author Tomasz Mroz
 * Simulates the game of Chain Reaction in a 11x11 grid
 * Supports up to 8 players
 * Now actually winnable
 * ANIMATIONS and SHADERS
 * HOT FIX: WORKING ANIMATIONS
 * @version 2.0 ALPHA
 */
public class ChainReaction extends JPanel {
    private ArrayList<Player> playerList = new ArrayList<>();
    private static int[] bounds = new int[4];
    private static ArrayList<Color> colors = new ArrayList<>(9);
    private static final int height = 700;
    private static final int width = 625;
    private static int currentPlayer;
    private static int playerAmount;
    static Frame frame;
    private Ball[][] board;
    private LinkedBlockingQueue<ExplodeEvent> explodeQueue;
    private LinkedBlockingQueue<BallEvent> ballQueue = new LinkedBlockingQueue<>();


    private ChainReaction() {
        colors.add(Color.RED );
        colors.add(new Color(0, 120, 0));
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.WHITE );
        colors.add(Color.ORANGE);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN );
        colors.add(Color.BLACK);

        setLayout(new BorderLayout(3, 3));
        setPreferredSize(new Dimension(625, 700));
        DrawingLoop game = new DrawingLoop(playerAmount);

        add(game, BorderLayout.CENTER);
        JLabel message = new JLabel("CHAIN REACTION", JLabel.CENTER);
        message.setForeground(colors.get(4));
        message.setFont(new Font("Monospaced", Font.BOLD, 36));
        message.setOpaque(false);
        add(message, BorderLayout.NORTH);

        new GameLoop();
    }
    //Only one instance can exist, need to change for client server model

    /**
     * Runs the program
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        frame = new Frame("Chain Reaction");
        JPanel content = new JPanel();
        frame.setContentPane(content);
        MainMenu menu = new MainMenu();
        content.add(menu);
        frame.pack();

    }

    /**
     * Creates the second window that contains the game board
     *
     * @param players number of players in the game
     */
    static void createWindow(int players) {
        frame = new Frame("Chain Reaction");
        playerAmount = players;
        JPanel content = new JPanel();
        frame.setContentPane(content);
        ChainReaction displayPanel = new ChainReaction();
        content.add(displayPanel);
        displayPanel.setBackground(Color.BLACK);
        displayPanel.setForeground(Color.WHITE);
        content.setPreferredSize(new Dimension(width, height));
        frame.pack();
    }

    /**
     * Nested class that handles graphics
     */
    private class DrawingLoop extends JPanel {
        int[] xblock = new int[25];
        int[] yblock = new int[25];
        int temp = 1;
        Timer timer;
        DrawingLoop(int playerCount) {
            setBackground(Color.BLACK);
            for (int x = 0; x < playerCount; x++) {
                playerList.add(new Player("TOM", ChainReaction.colors.get(x)));
            }
            currentPlayer = 0;
            playerAmount--;
            startNewGame();
            timer = new Timer(1, new RepaintAction());
        }

        /**
         * Initializes the game board and calls repaint
         */
        private void startNewGame() {
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
            repaint();

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

            if (ballQueue.peek() != null) {
                boolean ballDone = false;
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
                timer.start();
                int x = ballQueue.peek().getStartX();
                int y = ballQueue.peek().getStartY();
                for (int foo = 0; foo < 25; foo++) {
                    xblock[foo] = ballQueue.peek().getMouseX() + 5;
                    yblock[foo] = ballQueue.peek().getMouseY() + 5;
                }
                GradientPaint gradientPaint;
                if (board[x][y].getMaxValue() == 2) {
                    if ((x == 0 && y == 0)) {
                        gradientPaint = new GradientPaint((float) xblock[0], (float) yblock[0], ballQueue.peek().getBallColor(), (float) xblock[0] + 40, (float) yblock[0] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[0] + temp, yblock[0], 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[1], (float) yblock[1], ballQueue.peek().getBallColor(), (float) xblock[1] + 40, (float) yblock[1] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[1], yblock[1] + temp, 40, 40));

                        xblock[0] = xblock[0] + temp;
                        yblock[1] = yblock[1] + temp;

                        if ((xblock[0] == board[0][x + 1].getHorizontalLine() + 5)){ballDone = true;}
                        if ((yblock[1] == board[y + 1][0].getVerticalLine() + 5)){ballDone = true;}

                    } else if (x == 0 && y == (board[0].length - 1)) {
                        gradientPaint = new GradientPaint((float) xblock[2], (float) yblock[2], ballQueue.peek().getBallColor(), (float) xblock[2] + 40, (float) yblock[2] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[2] + temp, yblock[2], 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[3], (float) yblock[3], ballQueue.peek().getBallColor(), (float) xblock[3] + 40, (float) yblock[3] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[3], yblock[3] - temp, 40, 40));

                        xblock[2] = xblock[2] + temp;
                        yblock[3] = yblock[3] - temp;

                        if ((xblock[2] == board[0][x + 1].getHorizontalLine() + 5)){ballDone = true;}
                        if ((yblock[3] == board[y - 1][0].getVerticalLine() + 5)){ballDone = true;}

                    } else if (x == (board.length - 1) && y == 0) {
                        gradientPaint = new GradientPaint((float) xblock[4], (float) yblock[4], ballQueue.peek().getBallColor(), (float) xblock[4] + 40, (float) yblock[4] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[4] - temp, yblock[4], 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[5], (float) yblock[5], ballQueue.peek().getBallColor(), (float) xblock[5] + 40, (float) yblock[5] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[5], yblock[5] + temp, 40, 40));

                        xblock[4] = xblock[4] - temp;
                        yblock[5] = yblock[5] + temp;

                        if ((xblock[4] == board[0][x - 1].getHorizontalLine() + 5)){ballDone = true;}
                        if ((yblock[5] == board[y + 1][0].getVerticalLine() + 5)){ballDone = true;}

                    } else {
                        gradientPaint = new GradientPaint((float) xblock[6], (float) yblock[6], ballQueue.peek().getBallColor(), (float) xblock[6] + 40, (float) yblock[6] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[6] - temp, yblock[6], 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[7], (float) yblock[7], ballQueue.peek().getBallColor(), (float) xblock[7] + 40, (float) yblock[7] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[7], yblock[7] - temp, 40, 40));

                        xblock[6] = xblock[6] - temp;
                        yblock[7] = yblock[7] - temp;

                        if ((xblock[6] == board[0][x - 1].getHorizontalLine() + 5)){ballDone = true;}
                        if ((yblock[7] == board[y - 1][0].getVerticalLine() + 5)){ballDone = true;}

                    }
                } else if (board[x][y].getMaxValue() == 3) {
                    if (x > 0 && y == 0) {
                        gradientPaint = new GradientPaint((float) xblock[8], (float) yblock[8], ballQueue.peek().getBallColor(), (float) xblock[8] + 40, (float) yblock[8] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[8] - temp, yblock[8], 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[9], (float) yblock[9], ballQueue.peek().getBallColor(), (float) xblock[9] + 40, (float) yblock[9] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[9], yblock[9] + temp, 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[10], (float) yblock[10], ballQueue.peek().getBallColor(), (float) xblock[10] + 40, (float) yblock[10] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[10] + temp, yblock[10], 40, 40));


                        xblock[8] = xblock[8] - temp;
                        yblock[9] = yblock[9] + temp;
                        xblock[10] = xblock[10] + temp;

                        if ((xblock[8] == board[0][x - 1].getHorizontalLine() + 5)){ballDone = true;}
                        if ((yblock[9] == board[y + 1][0].getVerticalLine() + 5)){ballDone = true;}
                        if ((xblock[10] == board[0][x + 1].getHorizontalLine() + 5)){ballDone = true;}

                    } else if (x > 0 && y == board[0].length - 1) {
                        gradientPaint = new GradientPaint((float) xblock[11], (float) yblock[11], ballQueue.peek().getBallColor(), (float) xblock[11] + 40, (float) yblock[11] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[11], yblock[11] - temp, 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[12], (float) yblock[12], ballQueue.peek().getBallColor(), (float) xblock[12] + 40, (float) yblock[12] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[12] - temp, yblock[12], 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[13], (float) yblock[13], ballQueue.peek().getBallColor(), (float) xblock[13] + 40, (float) yblock[13] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[13] + temp, yblock[13], 40, 40));


                        yblock[11] = yblock[11] - temp;
                        xblock[12] = xblock[12] - temp;
                        xblock[13] = yblock[13] + temp;

                        if ((yblock[11] == board[y - 1][0].getVerticalLine() + 5)){ballDone = true;}
                        if ((xblock[12] == board[0][x - 1].getHorizontalLine() + 5)){ballDone = true;}
                        if ((xblock[13] == board[0][x + 1].getVerticalLine() + 5)){ballDone = true;}

                    } else if (x == 0 && y > 0) {
                        gradientPaint = new GradientPaint((float) xblock[14], (float) yblock[14], ballQueue.peek().getBallColor(), (float) xblock[14] + 40, (float) yblock[14] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[14], yblock[14] - temp, 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[15], (float) yblock[15], ballQueue.peek().getBallColor(), (float) xblock[15] + 40, (float) yblock[15] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[15] + temp, yblock[15], 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[16], (float) yblock[16], ballQueue.peek().getBallColor(), (float) xblock[16] + 40, (float) yblock[16] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[16], yblock[16] + temp, 40, 40));

                        yblock[14] = yblock[14] - temp;
                        xblock[15] = xblock[15] + temp;
                        yblock[16] = yblock[16] + temp;

                        if ((yblock[14] == board[y - 1][0].getVerticalLine() + 5)){ballDone = true;}
                        if ((xblock[15] == board[0][x + 1].getHorizontalLine() + 5)){ballDone = true;}
                        if ((yblock[16] == board[y + 1][0].getVerticalLine() + 5)){ballDone = true;}

                    } else if (x == board.length - 1 && y > 0) {
                        gradientPaint = new GradientPaint((float) xblock[17], (float) yblock[17], ballQueue.peek().getBallColor(), (float) xblock[17] + 40, (float) yblock[17] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[17] - temp, yblock[17], 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[18], (float) yblock[18], ballQueue.peek().getBallColor(), (float) xblock[18] + 40, (float) yblock[18] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[18], yblock[18] - temp, 40, 40));

                        gradientPaint = new GradientPaint((float) xblock[19], (float) yblock[19], ballQueue.peek().getBallColor(), (float) xblock[19] + 40, (float) yblock[19] + 40, Color.BLACK);
                        g2.setPaint(gradientPaint);
                        g2.fill(new Ellipse2D.Double(xblock[19], yblock[19] + temp, 40, 40));

                        xblock[17] = xblock[17] - temp;
                        yblock[18] = yblock[18] - temp;
                        yblock[19] = yblock[19] + temp;

                        if ((xblock[17] == board[0][x - 1].getHorizontalLine() + 5)){ballDone = true;}
                        if ((yblock[18] == board[y - 1][0].getVerticalLine() + 5)){ballDone = true;}
                        if ((yblock[19] == board[y + 1][0].getHorizontalLine() + 5)){ballDone = true;}

                    }
                } else if (board[x][y].getMaxValue() == 4) {

                    gradientPaint = new GradientPaint((float) xblock[20], (float) yblock[20], ballQueue.peek().getBallColor(), (float) xblock[20] + 40, (float) yblock[20] + 40, Color.BLACK);
                    g2.setPaint(gradientPaint);
                    g2.fill(new Ellipse2D.Double(xblock[20] - temp, yblock[20], 40, 40));

                    gradientPaint = new GradientPaint((float) xblock[21], (float) yblock[21], ballQueue.peek().getBallColor(), (float) xblock[21] + 40, (float) yblock[21] + 40, Color.BLACK);
                    g2.setPaint(gradientPaint);
                    g2.fill(new Ellipse2D.Double(xblock[21] + temp, yblock[21], 40, 40));

                    gradientPaint = new GradientPaint((float) xblock[22], (float) yblock[22], ballQueue.peek().getBallColor(), (float) xblock[22] + 40, (float) yblock[22] + 40, Color.BLACK);
                    g2.setPaint(gradientPaint);
                    g2.fill(new Ellipse2D.Double(xblock[22], yblock[22] - temp, 40, 40));

                    gradientPaint = new GradientPaint((float) xblock[23], (float) yblock[23], ballQueue.peek().getBallColor(), (float) xblock[23] + 40, (float) yblock[23] + 40, Color.BLACK);
                    g2.setPaint(gradientPaint);
                    g2.fill(new Ellipse2D.Double(xblock[23], yblock[23] + temp, 40, 40));

                    xblock[20] = xblock[20] - temp;
                    xblock[21] = xblock[21] + temp;
                    yblock[22] = yblock[22] - temp;
                    yblock[23] = yblock[23] + temp;

                    if ((xblock[20] == board[0][x - 1].getHorizontalLine() + 5)){ballDone = true;}
                    if ((xblock[21] == board[0][x + 1].getHorizontalLine() + 5)){ballDone = true;}
                    if ((yblock[22] == board[y - 1][0].getVerticalLine() + 5)){ballDone = true;}
                    if ((yblock[23] == board[y + 1][0].getVerticalLine() + 5)){ballDone = true;}
                }
                temp++;

                    if (ballDone) {
                        ballQueue.poll();
                        temp = 0;
                        timer.stop();
                    }

                repaint();
            }
            else {
                for (int x = 0; x < board.length; x++) {
                    for (int y = 0; y < board[0].length; y++) {
                        if (board[x][y].getValue() != 0) {
                            GradientPaint gradientPaint = new GradientPaint(board[0][x].getHorizontalLine() + 5, (board[y][0].getVerticalLine()) + 5, board[x][y].getBallColor(), (float) (board[0][x].getHorizontalLine()) + 45, (float) board[y][0].getVerticalLine() + 45, Color.BLACK);
                            g2.setPaint(gradientPaint);
                            g2.fill(new Ellipse2D.Double(board[0][x].getHorizontalLine() + 5, (board[y][0].getVerticalLine()) + 5, 40, 40));
                            //g.fillOval(((board[0][x].getHorizontalLine())) + 5, (board[y][0].getVerticalLine()) + 5, 40, 40);
                            g.drawString(String.valueOf(board[x][y].getValue()), ((board[0][x].getHorizontalLine()) + 5), (board[y][0].getVerticalLine()) + 5);

                        }

                    }
                }
            }
        }

    }



    /**
     * Handles all of the mouse events
     */
    private class GameLoop implements MouseListener {

        GameLoop() {
            addMouseListener(this);
        }

        public void mouseReleased(MouseEvent evt) {
            int locationX = evt.getX();
            int locationY = evt.getY();
            ballPlacer(locationX, locationY, playerList.get(currentPlayer).getPlayerColor());

        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent evt) {
        }

        public void mouseClicked(MouseEvent evt) {
        }

        /**
         * Called after a mouse click, places the balls, and controls the flow of logic
         * @param x     x coordinate of the mouse click
         * @param y     y coordinate of the mouse click
         * @param color Color of the ball placed
         */
        private void ballPlacer(int x, int y, Color color) {
            boolean moveLegal = isMoveLegal(x, y, color);
            int xBallPosition = 0;
            int yBallPosition = 0;
            if (!moveLegal) {
                return;
            }
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
                return;
            }
            board[xBallPosition][yBallPosition].setValue((board[xBallPosition][yBallPosition].getValue()) + 1);
            board[xBallPosition][yBallPosition].setColor(color);

            if (currentPlayer == playerAmount) {
                currentPlayer = 0;
            } else {
                currentPlayer++;
            }
            explodeQueue = new LinkedBlockingQueue<>();
            ballQueue = new LinkedBlockingQueue<>();
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
            repaint();
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
    private boolean isMoveLegal(int x, int y, Color color) {
        if ((x <= bounds[0]) || (x >= bounds[1]) || (y <= 115) || (y >= 665)) {
            return false;
        } else if ((color == null)) {
            return true;
        } else {
            return true;
        }
    }

    /**
     * Tests if a square is about to explode
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

            if(board[a][b].getBallColor() != null) {
                ballQueue.add(new BallEvent(a, b, board[a][b].getMaxValue(), board[a][b].getBallColor(), board[0][a].getHorizontalLine(), board[b][0].getVerticalLine()));

            }
            else{
                ballQueue.add(new BallEvent(a, b, board[a][b].getMaxValue(), playerList.get(currentPlayer).getPlayerColor(), board[0][a].getHorizontalLine(), board[b][0].getVerticalLine()));

            }


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
            endGame();
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
        return playerList.size() == 1;

    }

    private void endGame() {

        setBackground(Color.BLACK);
        frame.dispose();
        frame = new Frame("Chain Reaction");
        JPanel content = new JPanel();
        frame.setContentPane(content);
        content.add(new EndMenu());
        frame.pack();

    }

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

    /**
     * Subclass representing the first menu, which initializes the number of players
     */
    private static class MainMenu extends JPanel implements ActionListener {
        JTextField playerSize;

        private MainMenu() {
            setLayout(new BorderLayout(3, 3));
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(300, 400));
            JLabel message = new JLabel("CHAIN REACTION", JLabel.CENTER);
            message.setForeground(Color.WHITE);
            message.setFont(new Font("Monospaced", Font.BOLD, 24));
            message.setOpaque(false);
            add(message, BorderLayout.NORTH);
            JButton newGame = new JButton("New Game");
            newGame.addActionListener(this);
            newGame.setBackground(Color.BLACK);
            newGame.setPreferredSize(new Dimension(70, 30));
            add(newGame, BorderLayout.CENTER);
            playerSize = new JTextField("Player Number");
            add(playerSize, BorderLayout.SOUTH);

            repaint();
        }

        public void actionPerformed(ActionEvent evt) {
            try {
                if ((playerSize.getText() != null) && (Integer.parseInt(playerSize.getText()) >= 2) && (Integer.parseInt(playerSize.getText()) <= 8)) {
                    frame.setVisible(false);
                    frame.dispose();
                    createWindow(Integer.parseInt(playerSize.getText()));
                }
            } catch (NumberFormatException e) {
                System.out.println("cant do that homeslice");
            }

        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
        }

    }

    /**
     * Subclass of JFrame used to create frames in the beginning
     */
    private static class Frame extends JFrame {
        Frame(String name) {
            this.setName(name);
            this.setLocation(150, 0);
            this.setVisible(true);
            this.setResizable(false);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

    }
    private class EndMenu extends JPanel {
        EndMenu() {
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(200, 350));
            setLayout(new BorderLayout());
            JLabel label = new JLabel("GAME OVER!", JLabel.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Monospaced", Font.BOLD, 24));
            label.setOpaque(false);
            add(label, BorderLayout.CENTER);
            repaint();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
        }
    }
    private class BallEvent{
        private int startX;
        private int startY;
        private int ballValue;
        private Color ballColor;
        private int mouseX;
        private int mouseY;
        BallEvent(int x, int y, int value, Color color, int x2, int y2){
            startX = x;
            startY = y;
            ballValue = value;
            ballColor = color;
            mouseX = x2;
            mouseY = y2;
        }

        public int getStartX() {
            return startX;
        }
        public int getStartY() {
            return startY;
        }
        public int getMouseY() {
            return mouseY;
        }
        public int getMouseX() {

            return mouseX;
        }
        public Color getBallColor() {
            return ballColor;
        }

    }
    private class RepaintAction implements ActionListener{
        public void actionPerformed(ActionEvent evt){
            repaint();
        }
    }
}