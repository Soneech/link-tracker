package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public abstract class HttpClientTest {
    protected static WireMockServer wireMockServer;
    protected static String baseUrl;

    @BeforeAll
    public static void setUpWireMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        baseUrl = "http://localhost:" + wireMockServer.port();
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
