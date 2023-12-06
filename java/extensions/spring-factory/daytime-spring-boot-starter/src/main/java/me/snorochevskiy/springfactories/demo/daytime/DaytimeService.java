package me.snorochevskiy.springfactories.demo.daytime;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

public class DaytimeService {

    private final String daytimeHost;

    private final int port;

    public DaytimeService(String daytimeHost, int port) {
        this.daytimeHost = daytimeHost;
        this.port = port;
    }

    public String now() {
        try (Socket s = new Socket(daytimeHost, port)) {
            return IOUtils.toString(s.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
