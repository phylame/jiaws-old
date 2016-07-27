package pw.phylame.jiaws.core;

public interface Server extends Lifecycle {
    Context addWebapp(String path);
}
