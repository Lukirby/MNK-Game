package mnkgame;
import java.util.Arrays;


public class Strategy implements Comparable<Strategy> {
    static public enum direction {Horizontal,Vertical,Diagonal,Antidiagonal};
    private direction D;
    private int location;
    private int combo;
    //private MNKCell[] cells;

    public Strategy(direction D, int location, int K){
        this.D=D;
        this.location=location;
        this.combo=1;
        /* this.cells= new MNKCell[K];
        Arrays.fill(this.cells,null); */
    };

    public void update(direction D, int location){
        this.D=D;
        this.location=location;
    };

    public direction get_direction(){
        return this.D;
    }

    public int get_location(){
        return this.location;
    }

    public int get_combo(){
        return this.combo;
    }

    public boolean is_in_Strategy(MNKCell C){
        switch(this.D){
            case Vertical:
                return C.j==this.location;
            case Horizontal:
                return C.i==this.location;
            case Diagonal:
                return (C.i-C.j)==this.location;
            case Antidiagonal:
                return (C.i+C.j)==this.location;
            default:
                return false;
        }
    }

    public boolean out_of_Strategy(MNKCell C, int K){
        
        return false;
    }

    @Override
    public int compareTo(Strategy S){
        //chi ha un this.value superiore ha meno importanza
        //così da sfuttare la collezione PriorityQueue che implementa
        //una Coda di Priorità Minima.
        if (S==null){throw new NullPointerException("Errore Strategia Vuota");}
        if (this.combo<S.combo){
            return 1;
        } else
        if (this.combo>S.combo){
            return -1;
        } else {return 0;}
    }
}