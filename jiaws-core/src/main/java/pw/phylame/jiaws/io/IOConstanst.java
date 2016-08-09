package pw.phylame.jiaws.io;

import java.io.File;

public interface IOConstanst {
    int DEFAULT_BUFFER_SIZE = 8192;
    String CR = "\r";
    String LF = "\n";
    String CRLF = "\r\n";
    String LINE_SEPARATOR = System.getProperty("line.separator");
    String PATH_SEPARATOR = File.pathSeparator;

}
