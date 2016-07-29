package pw.phylame.jiaws.core;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.val;
import pw.phylame.jiaws.core.impl.LifecycleSupport;
import pw.phylame.jiaws.util.LifecycleStateException;
import pw.phylame.jiaws.util.Validator;

public class Server extends LifecycleSupport {
    @Getter
    @NonNull
    private final ServerConfig config;

    @Getter
    private final Assembly assembly = new Assembly();

    public Server(ServerConfig config) {
        this.config = config;
        init();
    }

    protected void init() {
        Validator.notNull(config.getAddress(), "Address cannot be null");
        Validator.notNull(config.getConnector(), "Connector cannot be null");
        config.getConnector().setAddress(config.getAddress());
        setRetainedTo(config.getConnector());
        // when JVM exit, stop the server
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!isStopped()) {
                    try {
                        Server.this.stop();
                    } catch (LifecycleStateException | IOException e) {
                        logger.error("Cannot stop the server when jvm exiting", e);
                    }
                }
            }
        });
    }

    @Override
    protected void doStart() throws IOException {
        logger.info("Starting server...");
        try {
            config.getConnector().start();
        } catch (LifecycleStateException e) {
            throw new IllegalStateException("Connector is already started", e);
        }
    }

    @Override
    protected void doStop() throws IOException {
        logger.info("Stopping server...");
        try {
            config.getConnector().stop();
        } catch (LifecycleStateException e) {
            throw new IllegalStateException("Connector is already stopped", e);
        }
    }

    public void setRetainedTo(Object o) {
        if (o instanceof ServerAware) {
            ((ServerAware) o).setServer(this);
        }
    }

    /**
     * Sends request and response to servlet.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     */
    public void handleRequest(ServletRequest request, ServletResponse response) {
        logger.debug("TODO: get servlet and filter");
        val cookie = new Cookie("name", "pw");
        cookie.setDomain("test.com");
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setComment("Test purpose");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.setContentType("text/html;charset=UTF-8");
        try {
            response.getWriter().append("<html><head>Test</head><body>Hello world</body></html>");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ((HttpServletResponse) response).addCookie(cookie);
    }
}
