package com.giannini.test.dom4j;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用dom4j读取xml文档
 */
public class Dom4jTest {

    private static final Logger logger = LoggerFactory
            .getLogger(Dom4jTest.class);

    public static void main(String[] args) throws Exception {
        SAXReader saxReader = new SAXReader();

        Document document = saxReader.read(new File("test/test.xml"));

        // 获取根元素
        Element root = document.getRootElement();
        logger.info("Root: " + root.getName());

        // 获取server元素
        Element server = root.element("server");
        logger.info("Server: {}", server.getName());

        // 获取所有server子元素
        List<Element> childList = server.elements();
        logger.info("total server child count: " + childList.size());

        // 获取特定名称的子元素
        List<Element> childList2 = server.elements("test-list");
        logger.info("test-list child: " + childList2.size());

        // 获取名字为指定名称的第一个子元素
        Element firstWorldElement = childList2.get(0).element("test-el");
        logger.info(
                "first test-el Attr: "
                        + firstWorldElement.attribute(0).getName()
                        + "=" + firstWorldElement.attributeValue("name"));

        logger.info("迭代输出-----------------------");
        // 迭代输出
        for (Iterator iter = server.elementIterator(); iter.hasNext();) {
            Element e = (Element) iter.next();
            logger.info(e.getName());
        }

        int threads = Integer
                .valueOf(server.element("threads").getStringValue());
        logger.info("threads count: {}", threads);

    }

}
