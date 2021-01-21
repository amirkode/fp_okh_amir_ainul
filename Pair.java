import java.lang.*;
import java.util.*;

class Pair<F, S> implements Comparable<Pair<F, S>>{
    public F first;
    public S second;
    public Boolean comparedBySecond = true; 
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public int compareTo(Pair<F, S> o) {
        if(comparedBySecond)
            return ((Comparable)this.second).compareTo((Comparable)o.second);
        return ((Comparable)this.first).compareTo((Comparable)o.first);
    }
}