package edu.java.bot.client.retry;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public class HttpClientTest {

    protected static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUpWireMockServer() {
        wireMockServer = new WireMockServer(8082);
        wireMockServer.start();
    }

    @AfterEach
    public void resetWireMockServer() {
        wireMockServer.resetAll();
    }

    @AfterAll
    public static void tearDownWireMockServer() {
        wireMockServer.stop();
    }
}
