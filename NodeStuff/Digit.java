public class Digit
{
    int digit;
    Digit next;
    Digit prev;
    
    public Digit(){}
    public Digit(int d){
        digit = d;
    }    
    public int getDigit() {
        return digit;
    }
    public void setDigit(int d) {
        digit = d;
    }
    public Digit getNext() {
        return next;
    }
    public void setNext(Digit d) {
        next = d;
    }
    public Digit getPrev() {
        return prev;
    }
    public void setPrev(Digit d) {
        prev = d;
    }
}