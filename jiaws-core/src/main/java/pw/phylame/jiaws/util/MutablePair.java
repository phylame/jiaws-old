package pw.phylame.jiaws.util;

public class MutablePair<A, B> extends Pair<A, B> {

    public MutablePair(A first, B second) {
        super(first, second);
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public void set(A first, B second) {
        this.first = first;
        this.second = second;
    }
}
