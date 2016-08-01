package pw.phylame.jiaws.util.values;

public class MutableTriple<A, B, C> extends Triple<A, B, C> {
    public MutableTriple(A first, B second, C third) {
        super(first, second, third);
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public void setThird(C third) {
        this.third = third;
    }

    public void set(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
