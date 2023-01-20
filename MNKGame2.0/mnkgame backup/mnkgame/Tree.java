package mnkgame;

import java.util.ArrayList;

public class Tree implements Comparable<Tree>,Cloneable { 
    
    //data
    //Situazione di Gioco da Valutare
    //private MNKBoard Board;

    //Valore della Situazione:
    //positivo -> possibilità di vittoria
    //negativo -> possibilità di sconfitta
    //zero -> possibilità di pareggio
    private float value;

    //indica se l'albero è stato valutato o meno
    private boolean evalueted;

    //casella scelta
    private MNKCell choise;

    private int capacity;

    //connection
    private Tree father;
    //private Tree[] children;
    //private int n_children;
    //private LinkedList<Tree> children;
    private ArrayList<Tree> children;

    //impone un ordine naturare sugli alberi Tree
    //sfruttato dalla PriorityQueue
    @Override
    public int compareTo(Tree T) {
        if (T!=null){
            if (this.value < T.get_value()){
                return 1;
            } else
            if (this.value > T.get_value()){
                return -1;
            } else
            return 0;
        } else {
            throw new IllegalArgumentException("Albero in Input Vuoto.");
        }

    }

    @Override
    public boolean equals(Object o){
        if (this==o){
            return true;
        } else
        if (o==null){
            throw new NullPointerException("Albero Nullo");
        } else
        if (o instanceof Tree){
            Tree O = (Tree)o;
            return this.choise.equals(O.get_choise());
        }
        else {
            throw new IllegalArgumentException("Non un Albero in Input");
        }
    }

    //Costruttore    
    public Tree(MNKCell Cell, int capacity){
        //data
        //this.Board  = Board;
        //un albero non valutato ha valore -infito quindi a il minor valore possibile
        this.value  = Float.NEGATIVE_INFINITY;
        this.evalueted=false;
        this.choise = Cell;
        this.capacity=capacity;

        //connection
        this.father = null;
        
        this.children = new ArrayList<Tree>(capacity);
        //this.children   = new LinkedList<Tree>();
        //inizializzo l'array
        //this.children=new Tree[n_free_cells];
        //Arrays.fill(this.children,null);
        //this.n_children=0;
    }

    public Tree(Tree T){
        this.choise=new MNKCell(T.get_choise().i,T.get_choise().j,T.get_choise().state);
        this.capacity=T.get_capacity();
        this.father=T.get_father();
        this.value=T.get_value();
        this.evalueted=T.is_evalueted();
        this.children=new ArrayList<Tree>(capacity);;
    }

    public float get_value(){
        return this.value;
    }

    public void set_value(float new_value){
        this.value=new_value;
    }

    public boolean is_evalueted(){
        return this.evalueted;
    }

    public void check_evalueted(boolean b){
        this.evalueted=b;
    }

    public Tree get_father(){
        return this.father;
    }

    public void set_father(Tree new_father){
        this.father=new_father;
    }

    /* public PriorityQueue<Tree> get_children(){
        return this.children;
    } */

    /* public Tree[] get_children(){
        return this.children;
    } */

    /* public LinkedList<Tree> get_children(){
        return this.children;
    } */

    public ArrayList<Tree> get_children(){
        return this.children;
    }

    public void add_child(Tree child){
        if (child!=null){
            child.set_father(this);
            this.children.add(child);
        } else {throw new IllegalArgumentException("Albero in Input Vuoto.");}

    }

    /* public void markCell(int i, int j){
        this.Board.markCell(i, j);
    } */

    public void set_choise(MNKCell new_choise){
        this.choise=new MNKCell(new_choise.i, new_choise.j, new_choise.state);
    }

    public void set_children(ArrayList<Tree> new_children){
        this.children=new_children;
    }

    public int get_capacity(){
        return this.capacity;
    }

    public MNKCell get_choise(){
        return this.choise;
    }
    
    /* public MNKBoard get_board(){
        return this.Board;
    }*/
    
    public boolean is_Leaf(){
        return this.children.size()==0;
    }

    public boolean is_Root(){
        return this.father==null;
    }

    //controllo se l'albero ha un figlio con la scelta in input
    public Tree has_Choise(MNKCell C){
        Tree newTree = new Tree(C, 0); 
        int index=this.children.indexOf(newTree);
        if (index==-1){
            return null;
        } else {
            newTree = this.children.get(index);
            return newTree;
        }
    }

    //elimino i figli dell'albero
    public void clear_Children(int capacity){
        this.children=new ArrayList<Tree>(capacity);
    }

    //ritorna lo stato delle cella nella Board dell'Nodo
    /* public MNKCellState get_CellState(MNKCell C){
        return this.Board.cellState(C.i, C.j);
    } */

    @Override
    public String toString(){
        return this.choise.toString();
    }

    @Override
    public Tree clone(){
        Tree Clone = new Tree(this);
        this.set_children(new ArrayList<Tree>(this.children));
        return Clone;
    }
}