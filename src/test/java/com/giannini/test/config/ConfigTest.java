package com.giannini.test.config;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.giannini.common.config.ConfigurationHelper;
import com.giannini.common.config.ConfigurationHelper.ConfigType;
import com.giannini.common.config.ElementNode;
import com.giannini.common.config.PropertiesConfigDocument;
import com.giannini.common.config.XMLConfigDocument;

public class ConfigTest {

    private static final String configFilePath = "test/test.xml";
    
    private static final String CONF_ADDRESS_LIST_NODE = "server.address-list";

    private static final String CONF_VOLUME_NODE = "server.volume";

    private static final String CONF_TEST_LIST_NODE = "server.test-list";

    private static final String CONF_ADDRESS = CONF_ADDRESS_LIST_NODE
            + ".address";

    private static final String CONF_THREADS = "server.threads";

    private static final String CONF_TEST_EL = CONF_TEST_LIST_NODE + ".test-el";

    private static final String CONF_IP_ADDRESSES = "server.ip-addresses";

    private static final String CONF_VOLUME_ID = CONF_VOLUME_NODE + ".id";

    private static final String CONF_VOLUME_NAME = CONF_VOLUME_NODE + ".name";

    private static final String CONF_VOLUME_CONFIG = CONF_VOLUME_NODE
            + ".config";

    private static final String CONF_MILLS = "server.mills";

    private static final String CONF_ENABLE = "server.enable";

    private static final String CONF_PI = "server.pi";

    private static final String ATTR_NAME = "name";

    private static final String ATTR_PRI = "pri";

    private static final String ATTR_METHOD = "method";

    // =================================================

    private static final String propConfigFile = "test/test.prop";

    @Before
    public void setEnv() {
        // System.out.println(System.getProperty("user.dir"));
        System.setProperty("CONF_HOME", System.getProperty("user.dir"));
    }

    @Test
    public void testXMLConfigDocument() throws Exception {
        XMLConfigDocument doc = (XMLConfigDocument) ConfigurationHelper
                .loadConfiguration(configFilePath, ConfigType.XML);
        doc.load();
        System.out.println("xml config doc load done.");
        ElementNode root = doc.getRootElement();
        Assert.assertNotNull(root);
        // int
        Assert.assertEquals(32, root.getInteger(CONF_THREADS, 16));
        // string
        Assert.assertEquals("com.gianinni.test",
                root.getString(CONF_VOLUME_NAME));
        // long
        Assert.assertEquals(Long.valueOf(1234567890987654321L),
                root.getLong(CONF_MILLS, 0L));
        // boolean
        Assert.assertEquals(Boolean.TRUE, root.getBoolean(CONF_ENABLE));
        // double
        Assert.assertEquals(Double.valueOf(3.1415926), root.getDoube(CONF_PI));
        // node list
        List<ElementNode> children = root.getChildList(CONF_TEST_EL);
        Assert.assertNotNull(children);
        Assert.assertEquals(3, children.size());
        // attribute
        for (ElementNode node: children) {
            if (node.getAttribute(ATTR_NAME).equals("test1")) {
                Assert.assertEquals("100", node.getAttribute(ATTR_PRI));
            } else if (node.getAttribute(ATTR_NAME).equals("test2")) {
                Assert.assertEquals("get", node.getAttribute(ATTR_METHOD));
            } else if (node.getAttribute(ATTR_NAME).equals("test3")) {
                Assert.assertNull(node.getAttribute(ATTR_PRI));
            } else {
                System.out.println("error list node");
            }
        }
    }

    public void testAutoReload() throws Exception {
        XMLConfigDocument config = new XMLConfigDocument(configFilePath,
                30 * 1000);// 30s reload
        config.load();
        ElementNode root = config.getRootElement();
        Assert.assertEquals(32, root.getInteger(CONF_THREADS, 16));
        // XXX modify xml manully
        Thread.sleep(80 * 1000);// sleep 40s

        root = config.getRootElement();
        Assert.assertNotEquals(32, root.getInteger(CONF_THREADS, 16));
    }

    @Test
    public void testPropertyConfig() throws Exception {
        PropertiesConfigDocument config = (PropertiesConfigDocument) ConfigurationHelper
                .loadConfiguration(propConfigFile, ConfigType.Proerity);
        config.load();
        // string
        Assert.assertEquals("com.giannini.config.XMLConfigDocument",
                config.getString("class"));
        // boolean
        Assert.assertEquals(true, config.getBoolean("enable"));;
        // int
        Assert.assertEquals(32, config.getInt("threads"));
        // long
        Assert.assertEquals(new Long(1234567890098765L),
                (Long) config.getLong("mills"));
        // double
        Assert.assertEquals(Double.valueOf(3.1415926),
                (Double) config.getDouble("pi"));
    }

}
