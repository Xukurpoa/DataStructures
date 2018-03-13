import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
/**
 * @author Tomasz Mroz
 * Solves a maze inputted from a text file
 * Prints the path taken excluding any backtracks
 */
public class MazeSolver{
    private Stack<MazeLocation> maze  = new Stack<>();
    private ArrayList<String> mazeList = new ArrayList<>();
    private ArrayList<MazeLocation> oldMaze = new ArrayList();
    private int down;
    private int left;
    /**
     * Needed a nonstatic way to create the ArrayList
     * Searches for the starting location and starts the game
     * When finished finding the end the game prints the solution set in reverse order
     */
    public MazeSolver(){
        listCreator("maze.txt");
        for(int a = 0; a < mazeList.size(); a++){
            for(int b = 0; b < mazeList.get(a).length(); b++){
                if(mazeList.get(a).substring(b, b+1).equals("@")){
                    maze.push(new MazeLocation(a,b));
                }
            }
        }
        if(maze.peek() == null){
            System.out.println("Maze has no starting point");
        }    
        else{
            try{
                while(!(mazeList.get(maze.peek().getDownLocation()).substring(maze.peek().getLeftLocation(), maze.peek().getLeftLocation() + 1).equals("$"))){
                    solver();
                }
            }
            catch(NullPointerException e){
                System.out.println("Maze is impossible");
            }  
        }

        Stack<MazeLocation> temp = new Stack<>();
        //prints in reverse order
        while(maze.peek() != null){
            temp.push(maze.pop());
        }
        int x = 0;
        while(temp.peek() != null){
            System.out.printf("Step #%d: %d, %d \n", x, temp.peek().getDownLocation(), temp.peek().getLeftLocation());
            temp.pop();
            x++;
        }  
    }    

    /**
     * Creates the object that runs the program
     */
    public static void main(String[] args){
        new MazeSolver();
    }    

    /**
     * Creates a Scanner object which reads in the maze line by line
     * Stores it in a arraylist
     * @param filename Name of the file that contains the maze
     */
    private void listCreator(String filename){
        try{
            Scanner scan = new Scanner( new BufferedReader( new FileReader(filename)));
            while(scan.hasNextLine()){
                mazeList.add(scan.nextLine());
            }
            scan.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }    
    }    

    /**
     * Tests if a spot up down left or right is either a possible free spot or the end
     * If it has not moved there yet, it pushes the location on the stack
     * returns if finds a potential move
     * if not it pops the stack
     */
    private void solver(){
        down = maze.peek().getDownLocation();
        left = maze.peek().getLeftLocation();
        //Top left corner
        if(down == 0 && left == 0){
            if((mazeList.get(down + 1).substring(left, left + 1).equals(".")) || mazeList.get(down + 1).substring(left, left + 1).equals("$")){
                if(move(down + 1, left)){return;}
            } 
            if((mazeList.get(down).substring(left + 1, left + 2).equals(".")) || mazeList.get(down).substring(left + 1, left + 2).equals("$")){
                if(move(down, left + 1)){return;}
            } 
        }
        //Top Right Corner
        else if(down == 0 && left == mazeList.get(down).length() - 1){
            if((mazeList.get(down + 1).substring(left, left + 1).equals(".")) || mazeList.get(down + 1).substring(left, left + 1).equals("$")){
                if(move(down + 1, left)){return;}
            } 
            if((mazeList.get(down).substring(left - 1, left).equals(".")) || mazeList.get(down).substring(left - 1, left).equals("$")){
                if(move(down, left - 1)){return;}
            } 
        }
        //Bottom Left Corner
        else if(down == mazeList.size() - 1 && left == 0){
            if((mazeList.get(down - 1).substring(left, left + 1).equals(".")) || mazeList.get(down - 1).substring(left, left + 1).equals("$")){
                if(move(down - 1, left)){return;}
            } 
            if((mazeList.get(down).substring(left + 1, left + 2).equals(".")) || mazeList.get(down).substring(left + 1, left + 2).equals("$")){
                if(move(down, left + 1)){return;}
            } 
        }
        //Bottom Right conrner
        else if(down == mazeList.size() - 1 && left == mazeList.get(down).length() - 1){
            if((mazeList.get(down - 1).substring(left, left + 1).equals(".")) || mazeList.get(down - 1).substring(left, left + 1).equals("$")){
                if(move(down - 1, left)){return;}
            } 
            if((mazeList.get(down).substring(left - 1, left).equals(".")) || mazeList.get(down).substring(left - 1, left).equals("$")){
                if(move(down, left - 1)){return;}
            } 
        }    
        //top row
        else if(down == 0  && left > 0){
            if((mazeList.get(down + 1).substring(left, left + 1).equals(".")) || mazeList.get(down + 1).substring(left, left + 1).equals("$")){
                if(move(down + 1, left)){return;}
            }
            if((mazeList.get(down).substring(left - 1, left).equals(".")) || mazeList.get(down).substring(left - 1, left).equals("$")){                    
                if(move(down, left - 1)){return;}
            } 
            if((mazeList.get(down).substring(left + 1, left + 2).equals(".")) || mazeList.get(down).substring(left + 1, left + 2).equals("$")){
                if(move(down, left + 1)){return;}
            }
        }
        //Bottom row
        else if( down == mazeList.size() - 1 && left > 0){
            if((mazeList.get(down - 1).substring(left, left + 1).equals(".")) || mazeList.get(down - 1).substring(left, left + 1).equals("$")){
                if(move(down - 1, left)){return;}
            }
            if((mazeList.get(down).substring(left - 1, left).equals(".")) || mazeList.get(down).substring(left - 1, left).equals("$")){
                if(move(down, left - 1)){return;}
            } 
            if((mazeList.get(down).substring(left + 1, left + 2).equals(".")) || mazeList.get(down).substring(left + 1, left + 2).equals("$")){
                if(move(down, left + 1)){return;}
            }
        }
        //Left row
        else if( left == 0 && down > 0){
            if((mazeList.get(down - 1).substring(left, left + 1).equals(".")) || mazeList.get(down - 1).substring(left, left + 1).equals("$")){
                if(move(down - 1, left)){return;}
            } 
            if((mazeList.get(down + 1).substring(left, left + 1).equals(".")) || mazeList.get(down + 1).substring(left, left + 1).equals("$")){
                if(move(down + 1, left)){return;}
            }
            if((mazeList.get(down).substring(left + 1, left + 2).equals(".")) || mazeList.get(down).substring(left + 1, left + 2).equals("$")){
                if(move(down, left + 1)){return;}
            }
        }
        //Right Row
        else if( left == mazeList.get(down).length() - 1 && down > 0){
            if((mazeList.get(down - 1).substring(left, left + 1).equals(".")) || mazeList.get(down - 1).substring(left, left + 1).equals("$")){
                if(move(down - 1, left)){return;}
            } 
            if((mazeList.get(down + 1).substring(left, left + 1).equals(".")) || mazeList.get(down + 1).substring(left, left + 1).equals("$")){
                if(move(down + 1, left)){return;}
            }
            if((mazeList.get(down).substring(left - 1, left).equals(".")) || mazeList.get(down).substring(left - 2, left - 1).equals("$")){
                if(move(down, left - 1)){return;}
            } 
        }
        //Everything in the middle
        else{
            if((mazeList.get(down - 1).substring(left, left + 1).equals(".")) || mazeList.get(down - 1).substring(left, left + 1).equals("$")){
                if(move(down - 1, left)){return;}
            } 
            if((mazeList.get(down + 1).substring(left, left + 1).equals(".")) || mazeList.get(down + 1).substring(left, left + 1).equals("$")){
                if(move(down + 1, left)){return;}
            }
            if((mazeList.get(down).substring(left - 1, left).equals(".")) || mazeList.get(down).substring(left - 1, left).equals("$")){
                if(move(down, left - 1)){return;}
            } 
            if((mazeList.get(down).substring(left + 1, left + 2).equals(".")) || mazeList.get(down).substring(left + 1, left + 2).equals("$")){
                if(move(down, left + 1)){return;}
            }
        }    
        noMove();
    }  

    /**
     * Called if no move has been made
     * Tries to pop and if it cannot the maze cannot be completed
     * if not pops the stack
     */
    private void noMove(){
        if(maze.peek() == null){
            System.out.println("Maze is impossible");
        }    
        else{
            maze.pop();
        } 
    }  
    /**
     * Tests for overlap or places on stack
     * @param nDown New Down var to test or put onto the stack
     * @param nLeft New Left var to test or put onto stack
     * @return If true spot is valid and method returns. If flase, continues to look for spots
     */
    private boolean move(int nDown, int nLeft){
        boolean beenThere = false;
        for(int foo = 0; foo < oldMaze.size(); foo++){
            if(oldMaze.get(foo).equals(new MazeLocation(nDown, nLeft))) {
                beenThere = true;
            }
        }    
        if(!beenThere){
            oldMaze.add(new MazeLocation(down, left));
            maze.push(new MazeLocation(nDown,nLeft));
            return true;
        } 
        return false;
    }    
    /**
     * Inner class that represents a location on the maze
     * Used in the stack and arraylists 
     */
    private class MazeLocation{
        private int downLocation;
        private int leftLocation;
        public MazeLocation(int downLocation, int leftLocation){
            this.downLocation = downLocation;
            this.leftLocation = leftLocation;
        }

        public int getDownLocation(){
            return downLocation;
        }

        public int getLeftLocation(){
            return leftLocation;
        }

        /**
         * Modification to the equals method to ensure it working
         * @param obj Object that is compared to 
         */
        @Override
        public boolean equals(Object obj){
            if(obj == this){
                return true;
            }
            if(obj instanceof MazeLocation){
                MazeLocation location = (MazeLocation) obj;
                return (downLocation == location.getDownLocation()) && (leftLocation == location.getLeftLocation());
            }    
            return false;
        }    

    }   
    
}
