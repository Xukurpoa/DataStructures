public class BST<T>{
    BSTNode<T> root;
    public BST(){
        root = new BSTNode<T>();
    }

    void insert(T val){
        if(root.get() == null){
            root.set(val);
        }
        else{
            compare(new BSTNode(val));
        }    
    }

    void inOrderPrint(){
        printLoop(root);
    }

    boolean exists(T checkMe){
        BSTNode curr = root;
        while(true){
            if(checkMe.equals(curr)){
                return true;
            }
            return false;
        }
    }   
    private void compare(BSTNode curr){
        if(curr.getLeft() == null && curr.getRight() == null){
            return;
        }    
        int temp =  curr.getc().compareTo(curr.getLeft().getc());
        if(temp < 0){
            compare(curr.getLeft());
        }    
        else if(temp > 0){
            compare(curr.getRight());
        }    
    }    
    void printLoop(BSTNode B){
        if(head != null){
            printLoop(B.getLeft());
            System.out.println(B.get())
            printLoop(B.getRight());
        }    
    }    
    public class BSTNode <X>
    {
        X val;
        BSTNode left;
        BSTNode right;
        public BSTNode(){}
        public BSTNode(X val){
            this.val = val;
        }    
        BSTNode getLeft() {return left;}

        BSTNode getRight() {return right;}

        void setLeft(BSTNode bn) {left = bn;}

        void setRight(BSTNode bn) {right = bn;}

        X get() {return val;}

        void set(X v) {val = v;}

        //need a version of get that returns a comparable object,
        //because compareTo won't work on generic types by default
        //use get when you need to access the value, use getc
        //when you need to do a comparison
        //This will crash if a non-comparable object is used.
        Comparable getc() {return (Comparable) val;}
    }
}
