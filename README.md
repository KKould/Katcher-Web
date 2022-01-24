# Katcher

#### 介绍
以Netty实现类似SpringMVC的功能，目前作为个人Netty学习项目

聊天页面: http://localhost:2048/chat.html



Pom.xml打包推荐插件：

```xml
<plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <configuration>
    	<appendAssemblyId>false</appendAssemblyId>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
          </descriptorRefs>
          <archive>
            <manifest>
              <!-- 此处指定main方法入口的class -->
               <!-- <mainClass>com.kould.katcher.TestApplication</mainClass> -->
            </manifest>
          </archive>
        </configuration>
        <executions>
          <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
              <goal>assembly</goal>
            </goals>
          </execution>
        </executions>
</plugin>
```



#### Version

- 0.1.0:
  - 实现基本Http响应序列化
  - 实现参数Json等格式传入
  - 通过反射自动对方法进行参数导入和业务方法装填
  - 内置Spring IOC容器
  - 实现基于Netty的启动器：KachterApplication
  - 基本实现Http下多HttpMethod的业务操作支持
- 0.0.1：
  - 通过webSocket实现Echo功能
  - 支持静态html文件返回
  - 内置/chat端点，用于Echo交互

