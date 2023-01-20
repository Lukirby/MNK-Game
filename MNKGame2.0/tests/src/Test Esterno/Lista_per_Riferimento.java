import java.util.ArrayList;
import java.util.ListIterator;

public class Lista_per_Riferimento {

    public static int j =10;

    public static void gain(Coppia C) {
        C.setVal(j);
    }


    public static void main(String[] args) {
        ArrayList<Coppia> L = new ArrayList<Coppia>(5);
        char c='a';
        for (int i=0; i<4; i++){
            Coppia C = new Coppia(c, i);
            c++;
            L.add(C);
        }
        System.out.println(L.toString());
        ListIterator<Coppia> I = L.listIterator();
        while(I.hasNext()){
            gain(I.next());
            j++;
        }
        System.out.println(L.toString());
        System.out.println(L.size());
    }
}
