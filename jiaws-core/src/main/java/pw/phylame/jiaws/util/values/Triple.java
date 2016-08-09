package pw.phylame.jiaws.util.values;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Triple<A, B, C> {
    @Getter
    protected A first;

    @Getter
    protected B second;

    @Getter
    protected C third;

    public <AX extends A, BX extends B, CX extends C> Triple(AX first, BX second, CX third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <A, B, C, AX extends A, BX extends B, CX extends C> Triple<A, B, C> of(AX first, BX second,
            CX third) {
        return new Triple<>(first, second, third);
    }
}
