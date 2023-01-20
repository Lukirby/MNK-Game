import java.util.Arrays;
import java.util.Random;
import java.util.LinkedList;

public class Ordina_Coppia {
    public static void print(Coppia[] A) {
        for (int i=0; i<A.length;i++){
            System.out.println(A[i].toString());
        }
        
    }


    public static void main(String[] args) {
        Coppia[] AR = new Coppia[0];
        int n = 30; char c = 'a'; Coppia C = new Coppia('0', 0); int m;
        Random R = new Random();
        LinkedList<Coppia> L = new LinkedList<>();
        long start = System.currentTimeMillis();
        for (int i=0;i<n;i++){
            m = R.nextInt(51);
            C = new Coppia(c, m);
            L.add(C);
            c++;
        }
        AR = L.toArray(new Coppia[0]);
        Arrays.sort(AR);
        System.out.println(Arrays.toString(AR));
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

}
