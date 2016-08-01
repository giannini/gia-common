package com.giannini.common.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.giannini.common.codec.MD5Utils;

/**
 * 对Element封装，支持多层级的xml解析 <br>
 * 节点名不能包含<b>西文句号(.)
 * 
 * @author giannini
 */
public class ElementNode
        implements Comparable<ElementNode>, Iterable<ElementNode> {

    /**
     * 配置节点层级分隔符
     */
    private static final char splitter = '.';

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
     * 不存在子配置参数节点时, 本参数值无效
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

    /**
     * 添加子配置节点
     * 
     * @param node
     */
    public void addChild(ElementNode node) {
        if (node == null) {
            return;
        }
        
        Object child = children.get(node.getName());
        if (child == null) {
            // 没有同名的配置节点
            this.children.put(node.getName(), node);
        } else {
            // 子节点可能是多个同名（配置节点名），需要用list保存.
            if (child instanceof List) {
                // 已经有多个同名节点
                ((List) child).add(node);
            } else {
                ArrayList<ElementNode> nodes = new ArrayList<ElementNode>();
                nodes.add((ElementNode) child);
                nodes.add(node);
                this.children.put(node.getName(), nodes);
            }
        }
    }

    /**
     * 获取所有子节点集
     * <p>
     * 注意: 子节点可能是一个单一的配置节点，也可能有多个同名的配置节点(list)
     * 
     * @return
     */
    Collection<Object> getAllChildren() {
        return this.children.values();
    }

    /**
     * 解析配置名路径 *
     * <p>
     * <li>参数路径中使用句号(.)作为层级节点参数名之间的分隔符<br>
     * <li>配置节点中的参数名请不要包含句号(.)<br>
     * 例如：存在节点 test 和 其子节点 example<br>
     * 使用参数路径指定item节点时, 应使用"test.example"
     * 
     * @param key
     * @return
     */
    private String[] parseConfigKeyPath(String key) {
        if (key == null) {
            return null;
        }

        ArrayList<String> keys = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        int index = key.indexOf(splitter);
        if (index < 0) {
            keys.add(key);
        } else {
            while (index > 0) {
                keys.add(key.substring(0, index));
                key = key.substring(index + 1);
                index = key.indexOf(splitter);
            }

            if (index == 0) {
                // 配置路径不能以.作为开始(子路径也不行)
                return null;
            } else {
                keys.add(key);
            }
        }

        String[] result = new String[keys.size()];
        return keys.toArray(result);
    }

    /**
     * 根据所给的节点路径，从当前节点开始网下查找子节点
     * <p>
     * 如果存在多个同名节点，只返回第一个
     * 
     * @param key
     * @return
     */
    public ElementNode getChild(String key) {
        if (this.children.isEmpty() || key == null) {
            return null;
        }

        String[] keys = parseConfigKeyPath(key);
        Map<String, Object> curMap = this.children;
        Object ch = null;
        for (String name: keys) {
            ch = curMap.get(name);

            if (ch == null) {
                // 配置文件路径有误，没有该节点(子节点)
                return null;
            } else if (ch instanceof ElementNode) {
                // 只有一个子节点的情况
                curMap = ((ElementNode) ch).children;
            } else if (ch instanceof List) {
                // 多个子节点的情况
                if (((List) ch).isEmpty()) {
                    // 不应该为空
                    return null;
                }
                // 取第一个子节点
                ch = ((List) ch).get(0);
                curMap = ((ElementNode) ch).children;
            } else {
                // 解析错误的情况
                return null;
            }
        }
        return (ElementNode) ch;
    }

    /**
     * 根据指定的路径，从当前节点开始寻找子节点列表
     * <p>
     * 参数路径可以是配置节点名称, 或者是采用'.'分隔的嵌套参数节点路径
     * <p>
     * 注：如果中间节点也存在同名多个的情况，会将所有同名节点的子节点全部返回
     * 
     * @param key
     *            配置参数名路径
     * @return 如果不存在指定路径的配置节点, 则返回null
     */
    public List<ElementNode> getChildList(String key) {
        if (children.isEmpty() || key == null) {
            return null;
        }

        String[] keys = parseConfigKeyPath(key);
        ArrayList<ElementNode> chList = new ArrayList<ElementNode>();
        this.doGetChildList(keys, 0, chList);

        return chList;
    }

    /**
     * 遍历配置节点树, 将符合参数路径的节点对象放入<code>cnList</code>列表
     * 
     * @param names
     *            参数路径解析后的参数名数组
     * @param index
     *            当前节点对应的数组下标
     * @param cnList
     *            符合参数路径的节点列表
     */
    private void doGetChildList(String[] names, int index,
            List<ElementNode> cnList) {
        Map<String, Object> curMap = this.children;
        for (int i = index; i < names.length; i++) {
            if (curMap == null) {
                return;
            }
            String curKey = names[i];
            Object target = curMap.get(curKey);
            if (target == null) {
                return;
            }

            if (names.length <= (i + 1)) {
                // 最后一个节点，将获取到的elementNode全部放入返回列表中
                if (target instanceof ElementNode) {
                    cnList.add((ElementNode)target);
                } else if (target instanceof List) {
                    cnList.addAll((List<ElementNode>) target);
                }
                continue;
            }

            if (target instanceof ElementNode) {
                // 中间节点，无同名
                curMap = ((ElementNode) target).children;
            } else if (target instanceof List) {
                // 有同名的中间节点， 需要对每个节点遍历
                List<ElementNode> midList = (List<ElementNode>) target;
                for (ElementNode node: midList) {
                    node.doGetChildList(names, i, cnList);
                }
            }
        }
    }

    /**
     * 强制以Boolean类型返回指定的配置参数值
     * <p>
     * 如果存在多个相关配置参数, 则选择第一个配置参数值
     * <p>
     * 参数路径可以是配置节点名称, 或者是采用'.'分隔的嵌套参数节点路径
     * 
     * @param key
     *            配置参数名路径
     * @return 参数值
     * @throws NullPointerException
     *             指定的配置参数不存在
     * @throws ClassCastException
     *             转换参数值类型失败
     */
    public boolean getBoolean(String key) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            throw new NullPointerException(key + " is null");
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        }

        if (node.value instanceof Boolean) {
            return ((Boolean) node.value).booleanValue();
        }

        return Boolean.parseBoolean(node.value.toString());
    }

    /**
     * 根据参数路径返回boolean类型的值，如果指定的节点不存在或者未设置值，则返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            return defaultValue;
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        } else if (node.value == null) {
            return defaultValue;
        }

        if (node.value instanceof Boolean) {
            return ((Boolean) node.value).booleanValue();
        }

        return Boolean.parseBoolean(node.value.toString());
    }

    /**
     * 强制以int类型返回指定的配置参数值
     * <p>
     * 如果存在多个相关配置参数, 则选择第一个配置参数值
     * <p>
     * 参数路径可以是配置节点名称, 或者是采用'.'分隔的嵌套参数节点路径
     * 
     * @param key
     *            配置参数名路径
     * @return 参数值
     * @throws NullPointerException
     *             指定的配置参数不存在
     * @throws ClassCastException
     *             转换参数值类型失败
     */
    public int getInteger(String key) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            throw new NullPointerException(key + " is null");
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        }

        if (node.value instanceof Integer) {
            return ((Integer) node.value).intValue();
        }

        return Integer.parseInt(node.value.toString());
    }

    /**
     * 根据参数路径返回boolean类型的值，如果指定的节点不存在或者未设置值，则返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInteger(String key, int defaultValue) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            return defaultValue;
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        } else if (node.value == null) {
            return defaultValue;
        }

        if (node.value instanceof Integer) {
            return ((Integer) node.value).intValue();
        }

        return Integer.parseInt(node.value.toString());
    }

    /**
     * 强制以Long类型返回指定的配置参数值
     * <p>
     * 如果存在多个相关配置参数, 则选择第一个配置参数值
     * <p>
     * 参数路径可以是配置节点名称, 或者是采用'.'分隔的嵌套参数节点路径
     * 
     * @param key
     *            配置参数名路径
     * @return 参数值
     * @throws NullPointerException
     *             指定的配置参数不存在
     * @throws ClassCastException
     *             转换参数值类型失败
     */
    public Long getLong(String key) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            throw new NullPointerException(key + " is null");
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        }

        if (node.value instanceof Long) {
            return ((Long) node.value).longValue();
        }

        return Long.parseLong(node.value.toString());
    }

    /**
     * 根据参数路径返回Long类型的值，如果指定的节点不存在或者未设置值，则返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public Long getLong(String key, Long defaultValue) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            return defaultValue;
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        } else if (node.value == null) {
            return defaultValue;
        }

        if (node.value instanceof Long) {
            return ((Long) node.value).longValue();
        }

        return Long.parseLong(node.value.toString());
    }

    /**
     * 强制以Double类型返回指定的配置参数值
     * <p>
     * 如果存在多个相关配置参数, 则选择第一个配置参数值
     * <p>
     * 参数路径可以是配置节点名称, 或者是采用'.'分隔的嵌套参数节点路径
     * 
     * @param key
     *            配置参数名路径
     * @return 参数值
     * @throws NullPointerException
     *             指定的配置参数不存在
     * @throws ClassCastException
     *             转换参数值类型失败
     */
    public Double getDoube(String key) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            throw new NullPointerException(key + " is null");
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        }

        if (node.value instanceof Double) {
            return ((Double) node.value).doubleValue();
        }

        return Double.parseDouble(node.value.toString());
    }

    /**
     * 根据参数路径返回Double类型的值，如果指定的节点不存在或者未设置值，则返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public Double getDouble(String key, Double defaultValue) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            return defaultValue;
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        } else if (node.value == null) {
            return defaultValue;
        }

        if (node.value instanceof Long) {
            return ((Double) node.value).doubleValue();
        }

        return Double.parseDouble(node.value.toString());
    }

    /**
     * 强制以String类型返回指定的配置参数值
     * <p>
     * 如果存在多个相关配置参数, 则选择第一个配置参数值
     * <p>
     * 参数路径可以是配置节点名称, 或者是采用'.'分隔的嵌套参数节点路径
     * 
     * @param key
     *            配置参数名路径
     * @return 参数值
     * @throws NullPointerException
     *             指定的配置参数不存在
     * @throws ClassCastException
     *             转换参数值类型失败
     */
    public String getString(String key) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            throw new NullPointerException(key + " is null");
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        }

        if (node.value instanceof String) {
            return node.value.toString();
        }

        return String.valueOf(node.value);

    }

    /**
     * 根据参数路径返回String类型的值，如果指定的节点不存在或者未设置值，则返回默认值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        ElementNode node = this.getChild(key);
        if (node == null) {
            return defaultValue;
        } else if (node.hasChildren()) {
            throw new ClassCastException(key + " no value.");
        } else if (node.value == null) {
            return defaultValue;
        }

        if (node.value instanceof String) {
            return node.value.toString();
        }

        return String.valueOf(node.value);
    }

    /**
     * 获取属性值
     * 
     * @param name
     *            属性名
     * @return
     */
    public String getAttribute(String name) {
        if (attributes == null) {
            return null;
        } else {
            return attributes.get(name);
        }
    }

    /**
     * 获取属性值
     * 
     * @param name
     * @param defaultValue
     *            如果不存在返回默认值
     * @return
     */
    public String getAttribute(String name, String defaultValue) {
        if (attributes == null || !attributes.containsKey(name)) {
            return defaultValue;
        } else {
            return attributes.get(name);
        }
    }

    /**
     * 获取属性映射
     * 
     * @return
     */
    public Map<String, String> attributes() {
        return this.attributes;
    }

    /**
     * 添加属性
     * 
     * @param key
     * @param val
     */
    public void addAttributes(String key, String val) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, String>();
        }
        this.attributes.put(key, val);
    }

    /**
     * 更新属性（清空当前已有属性）
     * 
     * @param attrs
     */
    public void setAttributes(Map<String, String> attrs) {
        if (this.attributes != null) {
            this.attributes.clear();
        }
        this.attributes.putAll(attrs);
    }

    public void clearAttributes() {
        if (this.attributes != null) {
            this.attributes.clear();
        }
    }

    public Iterator<ElementNode> iterator() {
        return new InnerIterator();
    }


    @SuppressWarnings("unchecked")
    public int compareTo(ElementNode other) {
        if (other == null) {
            return 1;
        }

        // compare name
        if (other.name == null || !other.name.equals(this.name)) {
            return 1;
        }

        // comapre val
        if (this.value == null && other.value != null) {
            return -1;
        } else if (this.value != null && other.value == null) {
            return 1;
        } else if (this.value != null && other.value != null
                && !this.value.equals(other.value)) {
            return (this.value.hashCode() - other.value.hashCode());
        }

        // compare all attributes
        int attrSize = this.attributes == null ? 0 : attributes.size();
        int otherAttrSize = other.attributes == null ? 0
                : other.attributes.size();
        if (attrSize != otherAttrSize) {
            return (attrSize - otherAttrSize);
        } else {
            // comapre attrs' md5_name & md5_value
            String attrsNames = null, attrsValues = null;
            for (Entry<String, String> attr: this.attributes.entrySet()) {
                attrsNames += attr.getKey();
                attrsValues += attr.getValue();
            }
            String otherAttrsNames = null, otherAttrsVaules = null;
            for (Entry<String, String> attr: other.attributes.entrySet()) {
                otherAttrsNames += attr.getKey();
                otherAttrsVaules += attr.getValue();
            }

            int diff = (MD5Utils.getMD5(attrsValues).hashCode()
                    - MD5Utils.getMD5(otherAttrsVaules).hashCode())
                    + (MD5Utils.getMD5(attrsNames).hashCode()
                            - MD5Utils.getMD5(otherAttrsNames).hashCode());
            if (diff != 0) {
                return diff;
            }

        }

        // compare children size
        if (this.children.size() != other.children.size()) {
            return (children.size() - other.children.size());
        }

        // compare all children
        for (Entry<String, Object> entry: this.children.entrySet()) {
            Object child = entry.getValue();
            Object otherChild = other.children.get(entry.getKey());
            if (otherChild == null) {
                return 1;
            }

            if (child instanceof ElementNode) {
                if (otherChild instanceof List) {
                    return -1;
                } else {
                    int diff = ((ElementNode) child)
                            .compareTo((ElementNode) otherChild);
                    if (diff != 0) {
                        return 0;
                    }
                }
            } else if (child instanceof List) {
                List<ElementNode> childList = (List<ElementNode>) child;
                List<ElementNode> otherChildList = (List<ElementNode>) otherChild;
                
                if (childList.size() != otherChildList.size()) {
                    return (childList.size() - otherChildList.size());
                } else {
                    for (int i = 0; i < childList.size(); i++) {
                        int diff = childList.get(i)
                                .compareTo(otherChildList.get(i));
                        if (diff != 0) {
                            return diff;
                        }
                    }
                }

            }
        }

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

        @SuppressWarnings("unchecked")
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
