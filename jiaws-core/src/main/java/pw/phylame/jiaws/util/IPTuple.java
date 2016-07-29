package pw.phylame.jiaws.util;

import java.net.InetAddress;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IPTuple {
    private String host;
    private String ip;
    private int port;

    public IPTuple(InetAddress address, int port) {
        this(address.getHostName(), address.getHostAddress(), port);
    }
}
