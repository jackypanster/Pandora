package com.gf.dencode.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private static final Object lockObj = new Object();
    private static ConfigManager instance;
    private String headerApiToken;
    private String headerRequestId;
    private String headerContentType;
    private String formatEncodeUrl;
    private String formatDecodeUrl;

    public String getHeaderRequestId() {
        return this.headerRequestId;
    }

    public String getHeaderApiToken() {
        return this.headerApiToken;
    }

    public String getHeaderContentType() {
        return this.headerContentType;
    }

    public String getFormatEncodeUrl() {
        return this.formatEncodeUrl;
    }

    public String getFormatDecodeUrl() {
        return this.formatDecodeUrl;
    }

    public static ConfigManager getInstance() throws IOException {
        if (instance == null) {
            synchronized (lockObj) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private ConfigManager() throws IOException {
        Properties prop = new Properties();
        InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (input == null) {
            throw new IOException("Failed to find " + CONFIG_FILE);
        }
        // load a properties file
        prop.load(input);
        // get the property value
        this.headerApiToken = prop.getProperty("HeaderApiToken");
        this.headerRequestId = prop.getProperty("HeaderRequestId");
        this.headerContentType = prop.getProperty("HeaderContentType");
        this.formatEncodeUrl = prop.getProperty("FormatEncodeUrl");
        this.formatDecodeUrl = prop.getProperty("FormatDecodeUrl");
        input.close();
    }
}
