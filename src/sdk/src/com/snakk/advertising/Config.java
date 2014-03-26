package com.snakk.advertising;

import java.io.IOException;
import java.util.Properties;

public final class Config {
    /**
     * Descriptor of which environment this SDK is built for
     */
    public static final String ENVIRONMENT;
    /**
     * This is the version of this SDK
     */
    public static final String SDK_VERSION;
    /**
     * This is the version dependency for Core
     */
    protected static final String CORE_REQUIRED_VERSION;

    static {
        Properties prop = new Properties();

        String t_env = "prod";
        String t_sdk_v = "1.1.0";
        String t_core_req = "1.0.0";
        try {
            // load a properties file
            prop.load(Config.class.getClassLoader().getResourceAsStream("com/snakk/advertising/internal/config.properties"));

            t_env = prop.getProperty("environment");
            t_sdk_v = prop.getProperty("sdkVersion");
            t_core_req = prop.getProperty("coreReq");

        } catch(NullPointerException e) {
            //This should only happen on a prod build
        } catch (IOException ex) {
        }

        ENVIRONMENT = t_env;
        SDK_VERSION = t_sdk_v;
        CORE_REQUIRED_VERSION = t_core_req;
    }

}
