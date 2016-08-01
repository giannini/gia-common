package com.giannini.common.config;

public final class ConfigurationHelper {

    /** 构造函数 */
    private ConfigurationHelper() {};

    /**
     * 构造配置文件实例，并且载入配置
     * 
     * @param filePath
     * @param type
     * @return
     * @throws Exception
     */
    public static Configuration loadConfiguration(String filePath,
            ConfigType type) throws Exception {
        // TODO
        Configuration config = null;

        switch (type) {
            case XML:
                config = new XMLConfigDocument(filePath);
                config.load();
                break;
            case Proerity:
                config = new PropertiesConfigDocument(filePath);
                config.load();
                break;
            case Others:
            default:
                break;

        }

        return config;
    }

    public enum ConfigType {
        XML, Proerity, Others;
    }
}
