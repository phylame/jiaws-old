package pw.phylame.jiaws.core;

import lombok.Getter;
import lombok.ToString;

/**
 * Holds version information of Jiaws.
 *
 */
@Getter
@ToString
public class Assembly {
    private String name = "Jiaws";

    private String version = "1.0";

    public String getVersionInfo() {
        return name + '/' + version;
    }
}
