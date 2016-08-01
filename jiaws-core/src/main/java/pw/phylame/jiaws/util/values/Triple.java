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
}
