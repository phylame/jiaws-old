package pw.phylame.jiaws.util.values;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class Triple<A, B, C> {
    @Getter
    protected A first;

    @Getter
    protected B second;

    @Getter
    protected C third;

    public static <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
        return new Triple<>(first, second, third);
    }
}
