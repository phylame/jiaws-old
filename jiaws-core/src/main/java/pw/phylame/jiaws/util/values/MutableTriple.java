package pw.phylame.jiaws.util.values;

public class MutableTriple<A, B, C> extends Triple<A, B, C> {
    public <AX extends A, BX extends B, CX extends C> MutableTriple(AX first, BX second, CX third) {
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

    public Triple<A, B, C> copyOf() {
        return new Triple<>(first, second, third);
    }
}
