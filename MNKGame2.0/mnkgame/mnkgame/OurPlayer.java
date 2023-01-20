package mnkgame;

import java.util.ArrayDeque;
import java.util.ListIterator;
import java.lang.Math;

public class OurPlayer implements MNKPlayer {

    //Tabella di Gioco, Albero e Relativi Dati
	private MNKBoard Board;
    private Tree T;
    private int M;
    private int N;
    private int K;
    //punteggio massimo vincita e perdita
    private float w_max;
    private float l_max;
    //capacità figli albero
    private int capacity;
    //profondità massima dell'iterative deeping
    private int max_depth;
    //Stato di Gioco (parita, vittoria, sconfitta ecc..)
	private MNKGameState myWin;
	private MNKGameState yourWin;
    //Stato della cella (p1, p2,...)
    private MNKCellState mySign;
    private MNKCellState yourSign;
	private int TIMEOUT;
    private long START_TIME;
    private boolean INTERUPT;
    private boolean first;
    private float start_IterDeepMode;
    private MNKCell Pl;
    //static public enum direction{up,right,down,left,up_right,up_left,dw_right,dw_left};
    static public enum Line {Horizontal,Vertical,Diagonal,Antidiagonal};

    public OurPlayer(){}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		// New random seed for each game
		//rand    = new Random(System.currentTimeMillis()); 
		this.Board   = new MNKBoard(M,N,K);
        this.T=null;
        this.M=M;
        this.N=N;
        this.K=K;
        w_max=4*K;
        l_max=-w_max;
        capacity=8;
        max_depth=(M*N)-2;
        //if-else
		myWin   = first ? MNKGameState.WINP1 : MNKGameState.WINP2; 
		//if-else
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        mySign = first ? MNKCellState.P1 : MNKCellState.P2;
        yourSign = first ? MNKCellState.P2 : MNKCellState.P1;

		TIMEOUT = timeout_in_secs;
        this.first=first;
        this.start_IterDeepMode=Float.POSITIVE_INFINITY; 
        this.Pl = null;
	}

    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC){
        MNKCell choiseCell;
        //se siamo i primi a iniziare con una Board vuota -> selezionamo l'elemento in mezzo
        if (MC.length==0){
            choiseCell = new MNKCell((int)Math.floor(M/2),(int)Math.floor(N/2));
            this.Board.markCell(choiseCell.i,choiseCell.j);
            this.T=new Tree(choiseCell,capacity);
            return choiseCell;
        } else
        //scelgo l'unica mossa possibile
        if (FC.length==1) {
            return FC[0];
        } else {
            START_TIME = System.currentTimeMillis();
            INTERUPT=false;
            MNKCell Av = MC[MC.length-1];        // Recover the last move from MC - Avversario
            this.Board.markCell(Av.i,Av.j);
            //se l'avversario inizia prima e l'albero non è inizializzato
            if (this.T==null){
                this.T=new Tree(AdiacentCell(Av,centralize_direction(Av), mySign),capacity);
                Board.markCell(T.get_choise().i,T.get_choise().j);
                return T.get_choise();
            } else {
                //se ho già un albero valutato con la scelta dell'avversario imposto quello
                Tree newTree = T.has_Choise(Av);
                if (newTree!=null){
                    T=newTree.clone();
                } else {
                    //altrimenti ne creo uno nuovo
                    initialize_Tree(this.T,Av);
                }
            }
            if (MC.length-2>=0) {Pl = MC[MC.length-2];}       // Recover the second last move from MC - Nostra Player

            //se ci sono poche celle selezionate parto con la valutazione senza attivare l'iterative deepening
            //if(MC.length<this.start_IterDeepMode){    
            if(MC.length<2){throw new IllegalArgumentException("Troppe Poche Mosse Selezionate");}
            
            this.T = IterativeDeeping(T,Pl,true, w_max, l_max,max_depth, 0);
            
            Board.markCell(T.get_choise().i,T.get_choise().j);
            //salvo la cella di immediata sconfitta
            MNKCell lossCell = ImmidiateLost(Av);
            //se ho una cella di immediata sconfitta e non ho una cella di immediata vittoria
            if (lossCell!=null && T.get_value()<w_max){
                this.Board.unmarkCell();
                Tree newTree = T.has_Choise(lossCell);
                if (newTree==null){
                    initialize_Tree(T, lossCell);
                } else {
                    T=newTree.clone();
                }
                this.Board.markCell(lossCell.i, lossCell.j);
            }
            
            if (T.get_choise()==null){throw new NullPointerException("Scelta Vuota");}
            //print(T.get_value());
            print_tree_deep(T, max_depth);
            return T.get_choise();
        }
    }

    //scegli le celle adiacenti in base alla direzione - TESTATA CORRETTA
    public MNKCell AdiacentCell(MNKCell choise, direction d, MNKCellState sign){
        int i=0; int j=0;
        switch (d){
            case up:
                i=choise.i-1;
                j=choise.j;
                break;
            case right:
                i=choise.i;
                j=choise.j+1;
                break;
            case down:
                i=choise.i+1;
                j=choise.j;
                break;
            case left:
                i=choise.i;
                j=choise.j-1;
                break;
            case up_right:
                i=choise.i-1;
                j=choise.j+1;
                break;
            case up_left:
                i=choise.i-1;
                j=choise.j-1;
                break;
            case dw_left:
                i=choise.i+1;
                j=choise.j-1;
                break;
            case dw_right:
                i=choise.i+1;
                j=choise.j+1;
                break;
            default:
            throw new IllegalArgumentException("Direzione Inesistente");
        }
        
        //controllo che la cella sia dentro alla tabella
        if (!legal_cell(i, j, this.M, this.N)){
            return null;
        } else {
            MNKCellState s;
            s = sign==mySign ? mySign : yourSign;
            MNKCell adiacentCell=new MNKCell(i,j,s);
            return adiacentCell;
        }
        
    }

    public float AlphaBeta(Tree Tr, boolean player, float a, float b, int depht, float val){

        this.Board.markCell(Tr.get_choise().i,Tr.get_choise().j);
        checkTime();
        if (depht == 0 || Tr.is_Leaf()){
            //print("Section 1S");
            if (!INTERUPT){
                if (!Tr.is_evalueted()){
                    val = Evaluate(Tr, player, depht);
                    checkTime();
                } else {val=Tr.get_value();}
                if (Tr.is_Leaf() && !INTERUPT){
                    MNKCell N = this.Board.MC.get(this.Board.MC.size()-2);
                    print(T.get_choise().state);
                    print(N.state);
                    print("END");
                    set_children(Tr,N,N.state);
                }
            }
            //print("Section 1E");
        } else if(player == true){
            //print("Section 2S");
            val = Float.NEGATIVE_INFINITY;
            ListIterator<Tree> child = Tr.get_children().listIterator();
            while (child.hasNext()&&!INTERUPT){
                val = Math.max(val,AlphaBeta(child.next(),false,a,b,depht-1,val));
                a = Math.max(val,a);
                checkTime();
                if(b <= a || INTERUPT){
                    break;
                }
            }
            //print("Section 2E");
        } else {
            //print("Section 3S");
            val = Float.POSITIVE_INFINITY;
            ListIterator<Tree> child = Tr.get_children().listIterator();
            while (child.hasNext()&&!INTERUPT){
                val = Math.min(val,AlphaBeta(child.next(),true,a,b,depht-1,val));
                b = Math.min(val,b);
                checkTime();
                if(b <= a || INTERUPT){
                    break;
                }
            }
            //print("Section 3E");
        }
        Tr.set_value(val);
        if (!INTERUPT) Tr.check_evalueted(true);
        this.Board.unmarkCell();
        return val;
    }

    public Tree IterativeDeeping(Tree Tr, MNKCell choise, boolean player, float a, float b, int depht, float val){
        
        if(Tr==null){throw new NullPointerException("Albero Nullo in IterativeDeeping");}
        //print("setta figli");
        set_children(Tr,choise, mySign);
        int j=4;
        while(Tr.is_Leaf()){
            set_children(Tr,this.Board.MC.get(this.Board.MC.size()-j), mySign);
            j=j+2;
        }
        //print(T.get_children().toString());
        //print("fine setta figli");

        for (int i=0; i<=depht; i++){
            
            if (Tr.is_Leaf()){throw new NullPointerException("L'albero non ha figli 1");}
            ListIterator<Tree> Child = Tr.get_children().listIterator();
            while (Child.hasNext()){
                if (!INTERUPT){
                    AlphaBeta(Child.next(),player,a,b,i,val);
                }
                checkTime();
            }
            //print("end figli");
            //Child=null;                                   RIMOSSO
            Tr.get_children().sort(null);
        }
        if (Tr.is_Leaf()){throw new NullPointerException("L'albero non ha figli 2");}
        if(Tr.get_children().get(0)==null){throw new NullPointerException("Scelta Nulla in Indice 0");}
        //print("end iterative");
        return Tr.get_children().get(0);   
    }

    private MNKCell ImmidiateLost(MNKCell AV){
        if (AV==null){throw new NullPointerException("Scelta Avversaria Nulla in Immediate Lost");}
        Tree LostTree = new Tree(AV,this.capacity);
        //print(AV);
        set_children(LostTree,LostTree.get_choise(), yourSign);
        float eval;
        MNKCell L = null;
        for(Tree t:LostTree.get_children()){
            //print("Valuto il Figlio: "+t.toString());
            this.Board.markCell(t.get_choise().i,t.get_choise().j);
            //print("246");
            eval = Evaluate(t, first, 0);
            //print(eval);
            //print("248");
            this.Board.unmarkCell(); 
            if (eval==l_max){
                L=new MNKCell(t.get_choise().i,t.get_choise().j);
                break;
            }
        }
        return L;
    }

    private float Evaluate(Tree T, boolean myTurn, int depth){
        //MNKBoard TB=T.get_board();
        switch (this.Board.gameState()){
            case OPEN:
                return EvaluateOpenGame(this.Board,T.get_choise(),myTurn);
            
            case WINP1:
                if (myWin==MNKGameState.WINP1) return w_max;
                else return l_max;

            case WINP2:
                if (myWin==MNKGameState.WINP2) return w_max;
                else return l_max;

            case DRAW:
                return 0;

            default:
                throw new IllegalArgumentException("Caso di Gioco non Possibile");
        }
    }

    //ho cambiato somma in max
    private float EvaluateOpenGame(MNKBoard TB, MNKCell choise, boolean myTurn){
        float eval=0;
        int range = this.K-1;
        int n_win=0;
        float v;
        //print("Verticale");
        if (TB.K<=TB.M){
            //eval=eval+Vertical_Check(TB, myTurn, choise, range);
            v=Vertical_Check(TB, myTurn, choise, range);
            eval = Math.max(eval,v);
            if (v==K-1){n_win++;}
        }

        //print("Orizzontale");
        if (TB.K<=TB.N){
            //eval=eval+Horizontal_Check(TB, myTurn, choise, range);
            v=Horizontal_Check(TB, myTurn, choise, range);
            eval=Math.max(eval,v);
            if (v==K-1){n_win++;}
        }

        //print("Diagonale");
        if (TB.K<=TB.M && TB.K<=TB.N){
            //eval=eval+Diagonal_Check(TB, myTurn, choise, range);
            v=Diagonal_Check(TB, myTurn, choise, range);
            eval=Math.max(eval,v);
            if (v==K-1){n_win++;}
        }

        //print("AntiDiagonale");
        if (TB.K<=TB.M && TB.K<=TB.N){
            v=Antidiagonal_Check(TB, myTurn, choise, range);
            eval=Math.max(eval,v);
            if (v==K-1){n_win++;}
        }
        
        if (n_win>=2){eval=w_max-(float)0.5;}
        if (choise.state==mySign) return eval;
        // if (myTurn) return eval;
        else return -eval; 
    }

    //inserire le possibili mosse intorno alla mossa choise del tipo scelto sign
    private void set_children(Tree Tr, MNKCell choise, MNKCellState sign){
        if(Tr==null){throw new NullPointerException("Albero Nullo in SetChildren");}
        if(choise==null){throw new NullPointerException("Scelta Nulla in SetChildren");}
        if(sign==null){throw new NullPointerException("Segno Nullo in SetChildren");}


        if (this.Board.gameState!=MNKGameState.OPEN){return;}
        //creamo l'albero con la Board e la Cell t1,t2
        Tree Child = null;
        MNKCell cellChild = null;
        for (direction d:direction.values()) {
            cellChild = AdiacentCell(choise, d, sign);
            
            if (cellChild!=null){

                //continuo nella direzione data finché non trovo Libero o Ostacolo
                
                while(cellChild!=null && this.Board.B[cellChild.i][cellChild.j]==sign){
                    cellChild=AdiacentCell(cellChild, d, sign);
                }
                
                //se è una cella libera la marco altrumenti la ignoro
                if (cellChild!=null && this.Board.B[cellChild.i][cellChild.j]==MNKCellState.FREE){
                    

                    Child= new Tree(cellChild,capacity);
                    
                    Tr.add_child(Child); 
                    
                }
                
                //print(Tr.get_children().size());
            }
        }
        //print(Tr.get_children().toString());
    }

    //controllo di vittoria verticale range=K-1
    private float Vertical_Check(MNKBoard TB, boolean myTurn, MNKCell choise, int range){
        int limit_up = choise.i-range>=0 ? choise.i-range : 0;
        int limit_dw = choise.i+range<TB.M ? choise.i+range : TB.M-1;

        return Count_cell(limit_up, limit_dw, TB, choise, Line.Vertical); 
    }

    //controllo di vittoria orizzantale
    private float Horizontal_Check(MNKBoard TB, boolean myTurn, MNKCell choise, int range){
        int limit_up = choise.j-range>=0 ? choise.j-range : 0;
        int limit_dw = choise.j+range<TB.N ? choise.j+range : TB.N-1;

        return Count_cell(limit_up, limit_dw, TB, choise, Line.Horizontal); 
    }

    //controllo di vittoria diagonale
    private float Diagonal_Check(MNKBoard TB, boolean myTurn, MNKCell choise, int range){
        int Dist = choise.j-choise.i;
        int High = choise.i>choise.j ? -Dist : 0;
        int Low = Dist>=(TB.N-TB.M) ? (TB.N-1)-Dist : TB.M-1;
        int limit_up = legal_cell(choise.i-range, choise.j-range, TB.M, TB.N) ? choise.i-range : High;
        int limit_dw = legal_cell(choise.i+range, choise.j+range, TB.M, TB.N) ? choise.i+range : Low;
        if (limit_dw-limit_up+1<TB.K) {return 0;}
        else {return Count_cell(limit_up, limit_dw, TB, choise, Line.Diagonal);}
    }

    //controllo di vittoria antidiagonale
    private float Antidiagonal_Check(MNKBoard TB, boolean myTurn, MNKCell choise, int range){
        int Dist = choise.j+choise.i;
        int High = Dist>=TB.N ? Dist-(TB.N-1) : 0;
        int Low = Dist<TB.M ? Dist : TB.M-1;
        int limit_up = legal_cell(choise.i-range, choise.j+range, TB.M, TB.N) ? choise.i-range : High;
        int limit_dw = legal_cell(choise.i+range, choise.j-range, TB.M, TB.N) ? choise.i+range : Low;
        if (limit_dw-limit_up+1<TB.K) {return 0;}
        else {return Count_cell(limit_up, limit_dw, TB, choise, Line.Antidiagonal);}
    }

    //sistemare meglio il controllo -> salvare max punteggio e min ostacolo
    private float Count_cell(int limit_up, int limit_dw,MNKBoard TB, MNKCell choise, Line L){
        ArrayDeque<MNKCellState> Coda = new ArrayDeque<MNKCellState>(TB.K+2);
        MNKCellState Casella=null;
        MNKCellState Cstate=choise.state;
        float Points=0;
        int Ostacle=0;
        int DistA=choise.i+choise.j; //Distanza AntiDiagonale
        int DistD=choise.j-choise.i; //Distanza Diagonale
        float max_point=0;
        //print("Caselle: "+new Integer(limit_dw-limit_up).toString());
        /* print(limit_up);
        print(limit_dw); */
        for (int y=limit_up;y<=limit_dw;y++){
            if (y>=this.K){throw new ArrayIndexOutOfBoundsException("Errore con y = "+y+" "+L.toString());}
            if (DistD+y>=this.K && L==Line.Diagonal){throw new ArrayIndexOutOfBoundsException("Errore con DistD = "+y+" "+L.toString());}
            if (DistA-y>=this.K && L==Line.Antidiagonal){throw new ArrayIndexOutOfBoundsException("Errore con DistA = "+y+" "+L.toString());}
            switch(L){
                case Vertical:
                    Casella = TB.B[y][choise.j];
                    break;
                case Horizontal:
                    Casella = TB.B[choise.i][y];
                    break;
                case Diagonal:
                    Casella = TB.B[y][y+DistD];
                    break;
                case Antidiagonal:
                    Casella = TB.B[y][DistA-y];
                    break;
                default:
                    throw new IllegalArgumentException("Linea Inesistente");
            }
            Coda.add(Casella);
            if (Casella!=MNKCellState.FREE){
                if (Casella==Cstate){
                    Points++;
                } else {
                    Ostacle++;
                    if (Casella==null) {throw new NullPointerException("Casella Nulla in CountCell 1");}
                }
            }
            if (Coda.size()>TB.K){
                Casella = Coda.removeFirst();
                if(Coda.size()<this.K){break;}              //NEW
                if (Casella!=MNKCellState.FREE){
                    if (Casella==Cstate){
                        Points--;
                    } else {
                        Ostacle--;
                        if (Casella==null) {throw new NullPointerException("Casella Nulla in CountCell 2");}
                    }
                }
            }
            if (Coda.size()==this.K){
                if (Ostacle<=0){max_point=Math.max(max_point,Points);
                } else 
                if (Coda.size()>this.K) throw new OutOfMemoryError("La Coda è troppo Lunga");
            } 
            //print(Coda);
        }
            
        return max_point;  
    }

    //ritorna il segno opposto
    private MNKCellState oppositeSign(MNKCellState sign){
        if (sign==mySign){
            return yourSign;
        } else {return mySign;}
    }

    //verifica che la coordinata sia dentro la tabella
    private boolean inside_Board(int n, int border){
        if (n<0){return false;}else
        if (n>border-1){
            return false;
        } else {return true;}
    }

    //verifica che la cella sia dentro la tabella
    private boolean legal_cell(int i, int j, int M, int N){
        return inside_Board(i,M)&&inside_Board(j,N);
    }

    //inizializza l'albero
    private void initialize_Tree(Tree T,MNKCell C){
        T.set_choise(C);
        T.clear_Children(capacity);
        T.check_evalueted(false);
        T.set_value(Float.NEGATIVE_INFINITY);
        T.set_father(null);               
    }

    private direction centralize_direction(MNKCell Av){
        if (Av==null){throw new NullPointerException("Scelta Avversaria Nulla in Centralize");}
        direction dd = null;
        int M2 = (int)Math.floor(M/2);
        int N2 = (int)Math.floor(N/2);
        if (Av.i<M2){
            if (Av.j<N2){
                dd = direction.dw_right;
            } else if (Av.j>N2) {
                dd = direction.dw_left;
            } else {
                dd = direction.down;
            }
        }else if (Av.i>M2){
            if (Av.j<N2){
                dd = direction.up_right;
            } else if (Av.j>N2) {
                dd = direction.up_left;
            } else {
                dd = direction.up;
            }
        } else {
            if (Av.j<N2){
                dd = direction.right;
            } else if (Av.j>N2) {
                dd = direction.left;
            } else {
                dd = direction.up_left;
            }
        }
        if (dd==null){throw new NullPointerException("Direzione Vuota in Centralize");}
        return dd;
    }

    private void checkTime(){
        if ((System.currentTimeMillis()-START_TIME)/1000.0 > TIMEOUT*(99.0/100.0)){
            INTERUPT=true;
        };
    }

    public String playerName(){
        return "NOME DA INSERIRE";  //TrisTerminator //VanitàIllusione
    }
 
    public void print(Object o){
        System.out.println(o);
    }   

    public void print_children(Tree Tr){
        ListIterator<Tree> I = Tr.get_children().listIterator();
        Integer i=0;
        while(I.hasNext()){
            print("Child "+i.toString()+" "+I.next().toString());
            i++;
        }
    }

    public void print_tree_deep(Tree Tr, int depth){
        
        ListIterator<Tree> I = Tr.get_children().listIterator();
        for(int i = depth;i>0;i--){System.out.print('\t');}
        print("Nodo "+Tr.toString()+" val: "+Tr.get_value()+" a profondità: "+depth);
        if(Tr.get_children().size()>0){
            while(I.hasNext()){
                print_tree_deep(I.next(),depth+1);
            }
        }
    }

    public void print_board(){
        for (int i=0;i<this.M;i++){
            for (int j=0;j<this.N;j++){

                if (this.Board.B[i][j]==MNKCellState.P1) System.out.print('X');
                else
                if (this.Board.B[i][j]==MNKCellState.P2) System.out.print('O');
                else System.out.print('-');
                System.out.print(' ');
            }
            print("");
        }
    }

    public void test_adicent_cell(){
        MNKCell c = new MNKCell(0, 0, MNKCellState.P2);
        for (direction d : direction.values()){
            MNKCell l = AdiacentCell(c,d,c.state);
            if (l==null){
                print(d.toString()+"   null");
            } else {
                print(d.toString()+"  "+l.toString());
            }
        }
    }

    public void test_set_children(){
        Tree TR = new Tree(null, 8);
        Board.markCell(0, 0); //P1
        Board.markCell(1, 0); //P2
        Board.markCell(2, 2); //P1
        Board.markCell(2, 0); //P2
        Board.markCell(3, 2); //P1
        Board.markCell(0, 2); //P2
        Board.markCell(0, 3); //P1
        Board.markCell(1, 2); //P2 
        Board.markCell(1, 1); //P1
        TR.set_choise(new MNKCell(1, 1,MNKCellState.P1));
        set_children(TR, TR.get_choise(), MNKCellState.P1);
        Tree[] A = TR.get_children().toArray(new Tree[0]);
        ListIterator<Tree> I = TR.get_children().listIterator();
        int i=0;
        while(I.hasNext()){
            print("Child number: "+(int)i);
            //set_children(I.next(),A[i].get_choise(),oppositeSign(MNKCellState.P1));
            set_children(I.next(),A[i].get_choise(),oppositeSign(MNKCellState.P2));
            i++;
        }
        /* print("Lettura:");
        i=0;
        ListIterator<Tree> L = TR.get_children().listIterator();
        while(L.hasNext()){
            this.Board.markCell(A[i].get_choise().i,A[i].get_choise().j);
            this.Board.markCell(1, 3);
            print("Child number: "+(int)i);
            //set_children(L.next(),A[i].get_choise(),oppositeSign(MNKCellState.P1));
            set_children(L.next(),A[i].get_choise(),oppositeSign(MNKCellState.P2));
            i++;
            this.Board.unmarkCell();
            this.Board.unmarkCell();
        } */
        print("Albero Rappresentazione");
        print_tree_deep(TR,0);
        //print(TR.get_children().toString());
    }

    public void test_clone_tree(){
        Tree TR = new Tree(null, 8);
        Board.markCell(0, 0);
        Board.markCell(1, 0);
        Board.markCell(1, 1);
        TR.set_choise(new MNKCell(1, 1,MNKCellState.P1));
        set_children(TR, TR.get_choise(), TR.get_choise().state);
        Tree QR = TR.clone();
        Board.markCell(2, 0);
        Board.markCell(3, 0);
        QR.set_choise(new MNKCell(3, 0,MNKCellState.P1));
        QR.clear_Children(this.capacity);
        set_children(QR, QR.get_choise(), QR.get_choise().state);
        print("Albero TR "+TR.toString());
        print("Figli di TR");
        print_children(TR);
        print("Albero QR "+QR.toString());
        print("Figli di QR");
        print_children(QR);
    }

    public void test_evaluate(){
        this.Board.markCell(2, 2);
        this.Board.markCell(3, 0);
        this.Board.markCell(1, 3);
        this.Board.markCell(1, 2);
        this.Board.markCell(2, 3);
        this.Board.markCell(0, 3);
        this.Board.markCell(0, 4);
        this.Board.markCell(0, 0);
        print_board();
        Tree TT = new Tree(new MNKCell(0, 4, MNKCellState.P1), 8);
        set_children(TT, TT.get_choise(), TT.get_choise().state);
        for(Tree child : TT.get_children()){
            this.Board.markCell(child.get_choise().i,child.get_choise().j);
            print("Valuto la Cella "+child.toString());
            print(Evaluate(child, true, K));
            print_board();
            this.Board.unmarkCell();
        }
    }

    public void test_copy_cell(){
        MNKCell A = new MNKCell(0, 1,MNKCellState.P1);
        Tree T = new Tree(A, 8);
        A = new MNKCell(5, 5, MNKCellState.P2);
        Tree T2 = new Tree(A, 8);
        print(T.toString());
        print(T2.toString());
    }

    public void test_immediate_lost(){
        //this.T=new Tree(null,this.capacity);
        MNKCell AV=null;
        /* for (int i=0;i<3;i++){
            this.Board.markCell(0, i); //P1
            this.Board.markCell(1, i); //P2
            AV=new MNKCell(1, i,this.Board.cellState(1, i));
        }
        this.Board.markCell(3, 3); */
        this.Board.markCell(2, 2);
        this.Board.markCell(3, 0);
        this.Board.markCell(3, 3);
        this.Board.markCell(1, 2);
        this.Board.markCell(2, 3);
        this.Board.markCell(0, 3);
        this.Board.markCell(1, 3);
        AV=new MNKCell(0, 3, this.Board.B[0][3]);
        print_board();
        MNKCell L = ImmidiateLost(AV);
        print(L);
    }

    public void test_evaluate3x3(){
        this.Board.markCell(1,1);
        this.Board.markCell(2, 0);
        this.Board.markCell(0, 1);
        this.Board.markCell(2, 2);
        print_board();
        MNKCell PL=new MNKCell(0, 1,MNKCellState.P1);
        this.T=new Tree(PL, 8);       
        set_children(T, PL, mySign);
        print(T.get_children().toString());
        ListIterator<Tree> Child=T.get_children().listIterator();
        float v;
        while (Child.hasNext()){
            this.Board.markCell(Child.next().get_choise().i, Child.previous().get_choise().j);
            print_board();
            print("Valuto: "+Child.next().get_choise().toString());
            v=Evaluate(Child.previous(),true,3);
            Child.next().set_value(v);
            print("Valore "+v);
            this.Board.unmarkCell();
        }
        T.get_children().sort(null);
        print(T.get_children().toString());
        print("ho scelto: "+T.get_children().get(0).get_choise().toString());
    }

    public void test_centralize(){
        MNKCell A = new MNKCell(1,2);
        print(centralize_direction(A));
    }

    public void test_point(){
        print(this.w_max);
        print(this.l_max);
        this.Board.markCell(1,1);
        this.Board.markCell(2, 0);
        this.Board.markCell(0, 1);
        this.Board.markCell(1, 2);
        this.Board.markCell(0, 0);
        MNKCell V = new MNKCell(2, 2);
        this.Board.markCell(V.i, V.j);
        Tree A = new Tree(new MNKCell(V.i, V.j, yourSign), capacity);
        print_board();
        print(Evaluate(A, false, max_depth));
    }

    public void test_alfa_beta(){
        // DA FARE
        this.Board.markCell(0, 1);
        this.Board.markCell(0, 0);
        this.Board.markCell(0, 2);
        this.Board.markCell(1, 0);
        this.Board.markCell(2, 0);
        Tree A =  new Tree(this.Board.MC.get(this.Board.MC.size()-1),capacity);
        IterativeDeeping(A,this.Board.MC.get(this.Board.MC.size()-2), true, w_max, l_max, max_depth, 0);
        print_tree_deep(A,max_depth);
    }

    public static void main(String[] args) {
        OurPlayer O=new OurPlayer();
        O.initPlayer(3, 3, 3, false, 10);
        //O.test_set_children();
        //O.test_clone_tree();
        //O.test_immediate_lost();
        //O.test_evaluate();
        //O.test_evaluate3x3();
        //O.test_centralize();
        //O.test_point();
        O.test_alfa_beta();
    }

}
