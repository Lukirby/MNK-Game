public class Coppia implements Comparable<Coppia>{
    protected char name;
    protected int val;
    public Coppia(char N, int V){
        this.name=N;
        this.val=V;
    }
    public char getName() {
        return name;
    }
    public int getVal() {
        return val;
    }
    public void setName(char name) {
        this.name = name;
    }
    public void setVal(int val) {
        this.val = val;
    }
    @Override
    public String toString() {
        return "{"+ val + "." +name  + "}";
    }
    @Override
    public int compareTo(Coppia C){
        if(this.val<C.getVal()){
            return 1;
        } else
        if (this.val>C.getVal()){
            return -1;
        } else {return 0;}
    }
}
