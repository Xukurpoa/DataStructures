import java.util.Scanner;
import java.io.*;
/**
 * @author Tom Mroz
 * This programs adds two numbers of arbitrary lengths by importing them from files
 * Seperates the digits into linked lists, and adds each Digit seperatly.
 * Now has support for uneven number sizes and carrying
 */ 
public class InfiniteAdder{
    /**
     * Main method, adds the integers and prints the result to standard output
     */
    public static void main(){
        Digit currList1 = listCreator("num1.txt");
        Digit currList2 = listCreator("num2.txt");
        int currValue;  
        int temp = 0;
        Digit resultTail = new Digit();
        Digit currList3 = resultTail;
        Digit prev = resultTail;
        while((currList1 != null) && (currList2 != null)){
            currValue = currList1.digit + currList2.digit + temp;
            if(currValue >= 10){
                currValue = currValue - 10;
                temp = 1;
            }
            else{
               temp = 0;
            }   
            prev.setPrev(new Digit(currValue));
            currList3 = prev;               
            prev = prev.getPrev();
            prev.setNext(currList3);
            currValue = 0;
            currList1 = currList1.getPrev();
            currList2 = currList2.getPrev();
        }
        int x = 0;
        while((currList1 != null) || (currList2 != null)){
            if(currList1 == null){
                 if(temp == 1){
                     x = temp + currList2.digit;
                     temp = 0;
                 }
                 else{
                    x = currList2.digit;
                 }
                 prev.setPrev(new Digit(x));
                 currList3 = prev;               
                 prev = prev.getPrev();
                 prev.setNext(currList3);
                 currList2 = currList2.getPrev();
                }
            else{
                 if(temp == 1){
                     x = temp + currList1.digit;
                     temp = 0;
                 }
                 else{
                     x = currList1.digit;
                 }
                 prev.setPrev(new Digit(x));
                 currList3 = prev;               
                 prev = prev.getPrev();
                 prev.setNext(currList3);
                 currList1 = currList1.getPrev();
            }
        } 
        if(temp == 1){
            prev.setPrev(new Digit(1));
            currList3 = prev;               
            prev = prev.getPrev();
            prev.setNext(currList3);
        }   
        while(prev != null){
            System.out.print(prev.digit);
            prev = prev.getNext();
        } 
            
    }
    /** 
     * Converts the files into a doubly linked list
     * @param file File that contains an integer will be turned into linked list
     * @return Tail of created doubly linked list
     * 
     */    
    public static Digit listCreator(String file){
        Digit curr = new Digit();
        try{
            Scanner scan = new Scanner(new BufferedReader(new FileReader(file)));
            Digit head = new Digit(scan.nextInt());
            curr = head;
            Digit prev = head;
            while(scan.hasNext()){
                curr.setNext(new Digit(scan.nextInt()));
                prev = curr;
                curr = curr.getNext();
                curr.setPrev(prev);
            }
            scan.close();
        }
        catch(Exception e){
            System.out.println("error: " + e.getMessage());
        } 
        return curr;
    }    
}