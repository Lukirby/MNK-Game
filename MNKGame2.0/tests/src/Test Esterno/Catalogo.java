import java.util.LinkedList;

public class Catalogo {
    private LinkedList<Coppia> L;

    public Catalogo (){
        this.L=null;
    }

    public LinkedList<Coppia> gList(){
        return L;
    }

    public void sList(LinkedList<Coppia> I){
        this.L=I;
    }
}
