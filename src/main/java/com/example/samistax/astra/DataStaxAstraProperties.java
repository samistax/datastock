package com.example.samistax.astra;

import java.io.File;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datastax.astra")
public class DataStaxAstraProperties {

    public File getSecureConnectBundle() {
        return secureConnectBundle;
    }

    public void setSecureConnectBundle(File secureConnectBundle) {
        this.secureConnectBundle = secureConnectBundle;
    }

    private File secureConnectBundle;
}
