package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.ScrapperWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    @Value("${api.scrapper.base-url}")
    private String scrapperBaseUrl;

    @Bean
    public ScrapperClient scrapperWebClient() {
        return new ScrapperWebClient(scrapperBaseUrl);
    }
}
