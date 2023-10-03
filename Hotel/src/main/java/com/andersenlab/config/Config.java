package com.andersenlab.config;

import com.andersenlab.util.ConfigHandler;
import org.springframework.stereotype.Component;

@Component
public class Config {

    public Config() {
        configData = ConfigHandler.createConfig(null);
    }

    private ConfigData configData;

    public ConfigData getConfigData() {
        return configData;
    }

    public void setConfigData(ConfigData configData) {
        this.configData = configData;
    }
}


