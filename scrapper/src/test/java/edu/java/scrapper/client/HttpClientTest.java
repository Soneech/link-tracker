package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class HttpClientTest {
    protected WireMockServer wireMockServer;
    protected String baseUrl;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        baseUrl = "http://localhost:" + wireMockServer.port();

    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }
}
