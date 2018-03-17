import java.io.*;
import java.util.Scanner;
public class ParenthesisCheck{
    public static void main(){
        String list = "";
        try{
            Scanner scan = new Scanner(new BufferedReader(new FileReader("Parenthesis.txt")));
            list = scan.next();
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        Stack<Integer> stack= new Stack<>();
        try{
            for(int x = 0;x < list.length(); x++){
                if(list.substring(x, x + 1).equals("(")){
                    stack.push(1);
                }
                if(list.substring(x, x + 1).equals(")")){
                    stack.pop();
                }    
            }
        }    
        catch(NullPointerException n){
            System.out.println("Too many closing parenthesis!");
        }
        if(stack.peek() != null){
            System.out.println("Too many opening parenthesis!");
        }
        else if(stack.peek() == null){
            System.out.println("File is correct!");
        }    
    }
}