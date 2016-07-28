package com.giannini.common.config;

import java.io.File;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * xml配置文件类
 * 
 * @author giannini
 */
public class XMLConfigDocument extends AbstractConfigDocument {

    /**
     * XML配置文件路径
     */
    private String configFilePath = null;

    /**
     * XML配置的根节点
     */
    private volatile ElementNode rootNode = null;

    public XMLConfigDocument(String configFilePath) {
        super();

        File config = new File(configFilePath);
        if (config.isAbsolute()) {
            this.configFilePath = config.getAbsolutePath();
        } else {
            this.configFilePath = this.getConfHome() + "/" + configFilePath;
        }

        // System.out.println(this.configFilePath);
    }

    /**
     * 根据xml的root节点来创建配置文件实例
     * 
     * @param rootNode
     */
    public XMLConfigDocument(ElementNode rootNode) {
        super();
        this.rootNode = rootNode;
    }

    public synchronized void load() throws Exception {
        if (this.configFilePath == null && this.rootNode != null) {
            return;
        } else if (this.configFilePath == null && this.rootNode == null) {
            throw new Exception("no config file.");
        } else {
            // 根据configFilePath加载，获得root node
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File(configFilePath));
            Element rootElem = document.getRootElement();
            System.out.println(rootElem.getName());
            this.rootNode = doLoadXMLElements(rootElem);
        }
    }

    @SuppressWarnings("unchecked")
    private ElementNode doLoadXMLElements(Element element) {
        // 新建Element node 节点
        ElementNode node = new ElementNode(element.getName(),
                element.elements().isEmpty() ? element.getData() : null);

        // 属性填充
        List<Attribute> attrs = element.attributes();
        if (attrs != null && !attrs.isEmpty()) {
            node.clearAttributes();
            for (Attribute attr: attrs) {
                node.addAttributes(attr.getName(),
                        String.valueOf(attr.getData()));
            }
        }

        // 填充子节点
        List<Element> children = element.elements();
        if (children == null || children.isEmpty()) {
            return node;
        } else {
            // 递归遍历子节点
            for (Element child: children) {
                ElementNode childNode = doLoadXMLElements(child);
                node.addChild(childNode);
            }
        }

        return node;
    }

    public ElementNode getRootElement() {
        return this.rootNode;
    }

}
