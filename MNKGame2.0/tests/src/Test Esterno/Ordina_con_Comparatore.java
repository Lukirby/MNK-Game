import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Ordina_con_Comparatore {
    public static void main(String[] args) {
        int l=30;
        LinkedList<Coppia> L = new LinkedList<Coppia>();
        char c = 'a'; Coppia C = new Coppia('0', 0); int n;
        Random R = new Random();
        long start = System.currentTimeMillis();
        for (int i=0;i<l;i++){
            n = R.nextInt(51);
            C = new Coppia(c, n);
            L.add(C);
            c++;
        }
        L.sort(null);
        System.out.println(L.toString());
        long end = System.currentTimeMillis();
        System.out.println(end-start);
        ListIterator<Coppia> I = L.listIterator();
        while(I.hasNext()){
            I.next().setVal(0);
        }
        System.out.println(L.toString());
    }    
}
