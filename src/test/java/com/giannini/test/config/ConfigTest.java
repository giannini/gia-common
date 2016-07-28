package com.giannini.test.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.giannini.common.config.Configuration;
import com.giannini.common.config.ElementNode;
import com.giannini.common.config.XMLConfigDocument;

public class ConfigTest {

    private static final String configFilePath = "test/test.xml";
    
    private static final String CONF_ADDRESS_LIST_NODE = "server.address-list";

    private static final String CONF_VOLUME_NODE = "server.volume";

    private static final String CONF_TEST_LIST_NODE = "server.test-lilst";

    private static final String CONF_ADDRESS = CONF_ADDRESS_LIST_NODE
            + ".address";

    private static final String CONF_THREADS = "server.threads";

    private static final String CONF_TEST_EL = CONF_TEST_LIST_NODE + ".test-el";

    private static final String CONF_IP_ADDRESSES = "server.ip-addresses";

    private static final String CONF_VOLUME_ID = CONF_VOLUME_NODE + ".id";

    private static final String CONF_VOLUME_NAME = CONF_VOLUME_NODE + ".name";

    private static final String CONF_VOLUME_CONFIG = CONF_VOLUME_NODE
            + ".config";

    @Before
    public void setEnv() {
        // System.out.println(System.getProperty("user.dir"));
        System.setProperty("CONF_HOME", System.getProperty("user.dir"));
    }

    @Test
    public void testXMLConfigDocument() throws Exception {

        XMLConfigDocument doc = new XMLConfigDocument(configFilePath);
        doc.load();
        System.out.println("xml config doc load done.");
        ElementNode root = doc.getRootElement();
        Assert.assertNotNull(root);
        Assert.assertEquals(32, root.getInteger(CONF_THREADS, 16));

    }

    // public static void main(String[] args) throws Exception {
        //
        // String configPath = "test/test.xml";
        // Configuration configuration = ConfigurationHelper
        // .loadConfiguration(configPath);
        // // configuration.addWatcher(new ConfigWatcher());
        //
        // loadFromConfigNode(configuration);
    // }

    private static void loadFromConfigNode(Configuration configuration) {
        ElementNode root = configuration.getRootElement();
    }
}
