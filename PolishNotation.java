import java.util.Scanner;
import java.io.*;

public class PolishNotation{
    public static void main(String[] main){
        Scanner scan;
        try{
            scan = new Scanner(new BufferedReader(new FileReader("polish.txt")));

            Stack<Double> operandStack = new Stack<Double>();
            Stack<String> operatorStack = new Stack<>();

            boolean pending = false;
            while(scan.hasNext()){
                String temp = scan.next();
                if(temp.matches("[\\+\\*\\/\\-]")){
                    operatorStack.push(temp);
                    pending = false;
                }
                else if (temp.matches("[+-]?\\d*\\.?\\d+")){
                    double operand = Double.parseDouble(temp);
                    if(pending){
                        while(operandStack.peek() != null){
                            double d = operandStack.pop();
                            String operator = operatorStack.pop();
                            if(operator.equals("+")){
                                operand = operand + d;
                            }
                            else if(operator.equals("-")){
                                operand = operand - d;
                            }    
                            else if(operator.equals("*")){
                                operand = operand * d;
                            }    
                            else if(operator.equals("/")){
                                operand = operand / d;
                            }  
                        }
                    }    
                    operandStack.push(operand);
                    pending = true;
                }
            }
            System.out.println(operandStack.pop());
        }  
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }
}