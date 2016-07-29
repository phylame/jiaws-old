package pw.phylame.jiaws.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class Pair<A, B> {
    @Getter
    protected A first;

    @Getter
    protected B second;
}
