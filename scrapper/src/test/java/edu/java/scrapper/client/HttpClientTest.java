package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public abstract class HttpClientTest {

    protected static WireMockServer wireMockServer;

    protected static String baseUrl;

    protected static int eventsCount;

    @BeforeAll
    public static void setUpWireMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        baseUrl = "http://localhost:" + wireMockServer.port();
        eventsCount = 10;
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
