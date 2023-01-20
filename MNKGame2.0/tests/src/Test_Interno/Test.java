package Test_Interno;

public class Test {

    static public MNKCell AdiacentCell(MNKCell choise, direction d, MNKCellState sign){
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
                i=choise.i+1;
                j=choise.j+1;
                break;
            case up_left:
                i=choise.i+1;
                j=choise.j-1;
                break;
            case dw_left:
                i=choise.i-1;
                j=choise.j-1;
                break;
            case dw_right:
                i=choise.i-1;
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


    static public void set_children(Tree T, MNKCell choise, MNKCellState sign){
        //creamo l'albero con la Board e la Cell t1,t2
        MNKBoard TreeBoard=T.get_board();
        MNKBoard ChildBoard = null;
        Tree Child = null;
        MNKCell cellChild = null;
        for (direction d:direction.values()) {
            cellChild = AdiacentCell(choise, d, sign);
            if (cellChild!=null){
                //continuo nella direzione data finché non trovo Libero o Ostacolo
                while(cellChild!=null && TreeBoard.B[cellChild.i][cellChild.j]!=T.get_choise().state){
                    cellChild=AdiacentCell(cellChild, d, sign);
                }
                //se è una cella libera la marco altrumenti la ignoro
                if (cellChild!=null && TreeBoard.B[cellChild.i][cellChild.j]==MNKCellState.FREE){
                    ChildBoard = TreeBoard;
                    ChildBoard.markCell(cellChild.i, cellChild.j);
                    Child= new Tree(ChildBoard,cellChild,capacity);
                    T.add_child(Child); 
                }
            }
        }
        System.out.println("Fine Set Chldren");
    }

    

}
