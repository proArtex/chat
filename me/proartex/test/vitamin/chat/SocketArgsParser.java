package me.proartex.test.vitamin.chat;

public class SocketArgsParser {
    private final String[] args;
    private String host;
    private int port;

    public SocketArgsParser(String[] args) {
        this.args = args;
        parseParams();
        setDefaultsIfEmpty();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private void parseParams() {
        for (int i = 0; i < args.length; i++) {
            if ("-host".equals(args[i])) {
                host = args[i + 1];
            }
            else if ("-port".equals(args[i])) {
                port = Integer.parseInt(args[i + 1]);
            }
        }
    }

    private void setDefaultsIfEmpty() {
        if (host == null)
            host = "localhost";

        if (port == 0)
            port = 9993;
    }
}