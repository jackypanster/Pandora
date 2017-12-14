package cn.com.gf.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private static final Object lockObj = new Object();
    private static ConfigManager instance;
    private String host;
    private String appId;

    public String getAppId() {
        return this.appId;
    }

    public String getHost() {
        return this.host;
    }

    public static ConfigManager getInstance() throws PandoraException {
        if (instance == null) {
            synchronized (lockObj) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private ConfigManager() throws PandoraException {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if (input == null) {
                throw new PandoraException("Failed to find " + CONFIG_FILE);
            }
            // load a properties file
            prop.load(input);
            // get the property value
            this.host = prop.getProperty("host");
            this.appId = prop.getProperty("appid");
        } catch (IOException e) {
            throw new PandoraException("Failed to read property", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
