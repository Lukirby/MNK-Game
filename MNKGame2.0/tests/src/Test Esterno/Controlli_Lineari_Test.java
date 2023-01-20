import java.util.ArrayDeque;

public class Controlli_Lineari_Test {
    static public enum Line {Horizontal,Vertical,Diagonal,Antidiagonal};

    public static int[][] create_tab(int M, int N){
        int[][] T = new int[M][N];
        for (int i=0; i<M; i++){
            for (int j=0; j<N; j++){
                T[i][j]=(i*10)+(j);
            }
        }
        return T;
    }

    public static void print(int[][] T){
        int M=T.length;
        int N=T[0].length;
        for (int i=0; i<M; i++){
            for (int j=0; j<N; j++){
                System.out.print(T[i][j]);
                System.out.print('\t');       
            }
            System.out.print('\n');
        }
    }

    public static void print(int n){
        System.out.println(n);
    }

    public static void print(Integer A[]) {
        for (int i=0;i<A.length;i++){
            System.out.print(A[i]);
            System.out.print('\t');
        }
        System.out.println();
    }

    //controllo di vittoria verticale
    public static Integer[] Vertical_Check(int[][] T, int K, int choise){
        int M=T.length;
        //int N=T[0].length;
        int i = choise/10;
        int j = (choise-(i*10));
        int limit_up = i-(K-1)>=0 ? i-(K-1) : 0;
        int limit_dw = i+(K-1)<M ? i+(K-1) : M-1;

        print(limit_up); print(limit_dw);
        
        return count_cell(limit_up, limit_dw, T, i,j,K, Line.Vertical); 
    }

    //controllo di vittoria orizzontale
    public static Integer[] Horizontal_Check(int[][] T, int K, int choise){
        //int M=T.length;
        int N=T[0].length;
        int i = choise/10;
        int j = (choise-(i*10));
        int limit_up = j-(K-1)>=0 ? j-(K-1) : 0;
        int limit_dw = j+(K-1)<N ? j+(K-1) : N-1;

        print(limit_up); print(limit_dw);

        return count_cell(limit_up, limit_dw, T, i,j,K, Line.Horizontal); 
    }

    public static Integer[] Diagonal_Check(int[][] T, int K, int choise){
        int M=T.length;
        int N=T[0].length;
        int i = choise/10;
        int j = (choise-(i*10));
        int dist = j-i;
        int e = i>j ? -dist : 0;
        int f = dist>=(N-M) ? (N-1)-dist : M-1;
        int limit_up = (i-(K-1)>=0)&&(j-(K-1)>=0) ? i-(K-1) : e;
        int limit_dw = (i+(K-1)<M)&&(j+(K-1)<N) ? i+(K-1) : f;

        print(limit_up); print(limit_dw);

        return count_cell(limit_up, limit_dw, T, i,j,K, Line.Diagonal);
    }

    public static Integer[] Antidiagonal_Check(int[][] T, int K, int choise){
        int M=T.length;
        int N=T[0].length;
        int i = choise/10;
        int j = (choise-(i*10));
        int dist = j+i;
        int e = dist>=N ? dist-(N-1) : 0;
        int f = dist<=M ? dist : M-1;
        int limit_up = (i-(K-1)>=0)&&(j+(K-1)<N) ? i-(K-1) : e;
        int limit_dw = (i+(K-1)<M)&&(j-(K-1)>=0) ? i+(K-1) : f;

        print(limit_up); print(limit_dw);
        // print(dist);print(N);
        return count_cell(limit_up, limit_dw, T, i,j,K, Line.Antidiagonal);
    }

    private static Integer[] count_cell(int limit_up,int limit_dw,int[][] T,int i,int j,int k,Line L){
        int C=0; ArrayDeque<Integer> D = new ArrayDeque<Integer>(k+1);
        int distm = j-i; int distp=i+j;
        for (int iter=limit_up; iter<=limit_dw; iter++){
            switch (L) {
                case Vertical:
                    C = T[iter][j];
                    break;
                case Horizontal:
                    C = T[i][iter];
                    break;
                case Diagonal:
                    C = T[iter][iter+distm];
                    break;
                case Antidiagonal:
                    C = T[iter][distp-iter];
                    break;
                default:
                    throw new IllegalArgumentException("Linea Inesistente");
            }
            D.add(C);
        }
        Integer[] A = D.toArray(new Integer[0]);
        return A;
    }

    public static void main(String[] args) {
        int K = 5; int choise = 65;
        int[][] T = create_tab(12,12);
        print(T);
        Integer[] V = Vertical_Check(T,K,choise);
        Integer[] H = Horizontal_Check(T, K, choise);
        Integer[] D1 = Diagonal_Check(T, K, choise);
        // Integer[] D2 = Diagonal_Check(T, K, 18);
        // Integer[] D3 = Diagonal_Check(T, K, 31);
        Integer[] A1 = Antidiagonal_Check(T, K, choise);
        // Integer[] A2 = Antidiagonal_Check(T, K, 51);
        // Integer[] A3 = Antidiagonal_Check(T, K, 90);
        print(V);
        print(H);
        print(D1);
        // print(D2);
        // print(D3);
        print(A1);
        // print(A2);
        // print(A3);
    }
}
