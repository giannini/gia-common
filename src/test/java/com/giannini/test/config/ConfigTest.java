package com.giannini.test.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.giannini.common.config.Configuration;
import com.giannini.common.config.ConfigurationHelper;
import com.giannini.common.config.ElementNode;

public class ConfigTest {

    private static final Logger logger = LoggerFactory
            .getLogger(ConfigTest.class);

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

    public static void main(String[] args) throws Exception {

        String configPath = "test/test.xml";
        Configuration configuration = ConfigurationHelper
                .loadConfiguration(configPath);
        // configuration.addWatcher(new ConfigWatcher());

        loadFromConfigNode(configuration);
    }

    private static void loadFromConfigNode(Configuration configuration) {
        ElementNode root = configuration.getRootElement();
    }
}
