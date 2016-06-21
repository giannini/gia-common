package com.giannini.common.config;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 对Element封装，支持多层级的xml解析 <br>
 * 节点名不能以'.'开始，中间可以包含'.'但是需要在代码使用中改写成'..'
 * 
 * @author giannini
 */
public class ElementNode
        implements Comparable<ElementNode>, Iterable<ElementNode> {

    /**
     * 配置参数名
     */
    private String name;

    /**
     * 配置参数值
     */
    Object value;

    /**
     * 子配置参数映射表
     * <p>
     * 存在子配置参数节点时, 本参数值无效
     * <p>
     * 映射表中应只包含ElementNode或者List<ElementNode>对象
     */
    private final Map<String, Object> children = new LinkedHashMap<String, Object>();

    /**
     * 属性映射表
     * <p>
     * JDBC类型的配置节点暂不支持
     */
    private Map<String, String> attributes = null;

    public ElementNode(String name, Object value) {
        if (name == null || name.isEmpty() || name.charAt(0) == '.') {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        if (this.hasChildren()) {
            // 如果当前节点包含了子节点，那么当前节点没有value
            return null;
        }
        return value;
    }

    /**
     * 当前节点是否包含子节点
     * 
     * @return
     */
    private boolean hasChildren() {
        return (!children.isEmpty());
    }

    public Iterator<ElementNode> iterator() {
        return new InnerIterator();
    }

    public int compareTo(ElementNode arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 内部迭代器实现类
     * 
     * @author giannini
     */
    private class InnerIterator implements Iterator<ElementNode> {

        /**
         * 当前子节点迭代器
         */
        private final Iterator<Object> currentIter;

        /**
         * 下次ElementNode节点对象(null=没有其他子节点了)
         */
        private ElementNode next = null;

        /**
         * 当前节点为List时的ElementNode迭代器
         */
        private Iterator<ElementNode> listElemIter = null;

        public InnerIterator() {
            currentIter = children.values().iterator();
            this.next();
        }

        public boolean hasNext() {
            return (next != null);
        }

        public ElementNode next() {
            ElementNode cn = next;

            next = iterInList();
            if (next != null) {
                return cn;
            }

            while (currentIter.hasNext()) {
                Object obj = currentIter.next();
                if (obj instanceof ElementNode) {
                    next = (ElementNode) obj;
                    return cn;
                } else if (obj instanceof List) {
                    listElemIter = ((List<ElementNode>) obj).iterator();
                    next = iterInList();
                    if (next != null) {
                        return cn;
                    }
                }
            }

            return cn;
        }

        /**
         * 在List节点中遍历ConfigNode节点
         * 
         * @return
         */
        private ElementNode iterInList() {
            next = null;
            while (listElemIter != null && listElemIter.hasNext()
                    && next == null) {
                next = listElemIter.next();
            }

            if (next == null) {
                listElemIter = null;
            }

            return next;
        }

        public void remove() {
            throw new UnsupportedOperationException("not implemented.");
        }

    }
}
