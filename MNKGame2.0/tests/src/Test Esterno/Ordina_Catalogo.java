import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Ordina_Catalogo {

    static public LinkedList<Coppia> initiate(int l){
        LinkedList<Coppia> L = new LinkedList<Coppia>();
        char c = 'a'; Coppia C = new Coppia('0', 0); int n;
        Random R = new Random();
        for (int i=0;i<l;i++){
            n = R.nextInt(51);
            C = new Coppia(c, n);
            L.add(C);
            c++;
        }
        return L;
    } 

    public static void main(String[] args) {
        Catalogo T=new Catalogo();
        LinkedList<Coppia> L = initiate(10);
        T.sList(L);
        System.out.println(T.gList().toString());
        T.gList().sort(null);
        System.out.println(T.gList().toString());
        ListIterator<Coppia> I = T.gList().listIterator();
        Random R = new Random();
        while (I.hasNext()){
            I.next().setVal(R.nextInt(51));
        }
        System.out.println(T.gList().toString());
        T.gList().sort(null);
        System.out.println(T.gList().toString());
    }
}
