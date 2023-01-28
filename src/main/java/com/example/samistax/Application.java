package com.example.samistax;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.example.samistax.astra.DataStaxAstraProperties;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.pulsar.annotation.EnablePulsar;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Path;


/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@Push
@EnablePulsar
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
@Theme(value = "datastock")
public class Application implements AppShellConfigurator {

    @Value("${alphavantage.api.key}")
    private String ALPHA_VANTAGE_API_KEY;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    void AlphaVantageInitializer() {
        // Initialize AlphaVantage Java API client
        Config cfg = Config.builder()
                .key(ALPHA_VANTAGE_API_KEY)
                .timeOut(10)
                .build();
        AlphaVantage.api().init(cfg);
    }

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

}
