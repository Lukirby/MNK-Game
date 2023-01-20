import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;
public class Coda_P_in_Array {
    public static void main(String[] args) {
        int n = 30; char c = 'a'; Coppia C = new Coppia('0', 0); int m;
        Random R = new Random();
        PriorityQueue<Coppia> Q = new PriorityQueue<Coppia>(n);
        long start = System.currentTimeMillis();
        for (int i=0;i<n;i++){
            m = R.nextInt(51);
            C = new Coppia(c, m);
            Q.add(C);
            c++;
        }
        Coppia[] A = Q.toArray(new Coppia[0]);
        String S = Arrays.toString(A);
        System.out.println(S);
        long end = System.currentTimeMillis();
        System.out.println(end-start);
    }
}
