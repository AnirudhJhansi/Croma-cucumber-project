
package com.anirudh.croma.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger log = LogManager.getLogger(ConfigLoader.class);
    private static ConfigLoader instance;
    private final Properties props = new Properties();

    private ConfigLoader() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config/config.properties")) {
            if (in == null) throw new RuntimeException("config.properties not found in classpath under config/");
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed loading config.properties", e);
        }
    }

    public static ConfigLoader getInstance() {
        if (instance == null) instance = new ConfigLoader();
        return instance;
    }

    public String get(String key) { return props.getProperty(key); }
    public String get(String key, String def) { return props.getProperty(key, def); }
}
