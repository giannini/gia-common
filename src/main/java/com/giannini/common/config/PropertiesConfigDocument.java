package com.giannini.common.config;

/**
 * properties格式的配置文件，部分接口可能不可用
 *
 * @author giannini
 */
public class PropertiesConfigDocument extends AbstractConfigDocument {

    public PropertiesConfigDocument(String configFile) {
        super(configFile);
    }

    public ElementNode getRootElement() {
        return null;
    }

    public void load() throws Exception {
        // TODO Auto-generated method stub

    }

}
