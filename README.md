# gia-common

自己平时使用比较顺手的一些基本操作辅助类。仅供自己学习用。
***

## Config

#### 解析xml，对dom4j做了一点封装。

dom4j中多级的xml需要一级级的解析，希望是支持多级表示的解析方式。

比如，

	<server>
        <ip-address>
            <ip>127.0.0.1</ip>
        </ip-address>
	</server>
	
可以使用`server.ip-address.ip`这种形式直接获取xml中的元素。

#### 解析property文件

对java property文件的解析

#### 统一日志接口
对上述两种配置文件的接口统一


## Cache
#### ehcache
使用ehcache作为本地缓存，封装了该框架下的一些操作。

#### redis
远程缓存redis的java client **jedis**.

封装的内容只是对单点redis的基本操作、连接缓存池这类，并没有涉及到集群相关内容。

#### 缓存的统一接口
使用缓存的统一接口来实现应用程序对缓存的使用。

应用端不用关系后端使用何种缓存框架。

当然应用端还是要自己选择使用本地还是远程

## Utils

一些基本的小工具，平时自己累积或者其他框架中单独的工具类比较实用。