package data;

/**
 * Created by gpeng on 1/9/18.
 */
public class PepPos implements Comparable<PepPos>{
    // start, end, and y are all from 0
    private int start;
    private int end;
    private int y;
    private Peptide pep;

    public PepPos(int start, int end, Peptide pep){
        this.start = start;
        this.end = end;
        this.pep = pep;
        this.y = 0;
    }

    public void setY(int y) {this.y = y;}
    public int getY() {return y;}

    public int getStart() {return start;}
    public int getEnd() {return end;}
    public Peptide getPep() {return pep;}

    public boolean contains(int x){
        if(start <= x && end >= x){
            return true;
        }
        return  false;
    }

    public boolean contains(int x, int y){
        if(this.y == y && start <= x && end >= x){
            return true;
        }
        return  false;
    }

    @Override
    public int compareTo(PepPos o) {
        if(this.start < o.getStart()){
            return -1;
        } else if(this.start > o.getStart()){
            return 1;
        } else {
            if(this.end > o.getEnd()){
                return -1;
            } else if(this.end < o.getEnd()){
                return 1;
            } else{
                return 0;
            }
        }
    }
}