# Katcher

#### 概要 | Synopsis
以Netty实现类似SpringMVC的功能

内置测试：

- 聊天页面: http://localhost:2048/chat.html



#### 预定义接口 | API

- ##### 类

  - ###### KatcherApplication Katcher启动类

    - start (InetSocketAddress address) : 用于手动启动Katcher
      - address 端口信息
    - destroy () : 关闭Katcher
    - run (String[] args, int port, String scanPath) : 用于启动Spring IOC容器与Katcher并自动管理服务生存状态
      - args 初始化参数 （暂时无用）
      - port 端口
      - scanPath 自定义包名称（即你写的代码所在的包名，可复数）

- ##### 枚举

  - ###### HttpMethod 表示Http请求方法

    - HttpMethod.GET : Get方法
    - HttpMethod.POST : POST方法
    - HttpMethod.PUT : PUT方法
    - HttpMethod.DELETE : DELETE方法

- ##### 注解

  - ###### @Controller 用于修饰控制器类

    - String uri : 该类下的路径前缀

  - ###### @Mapping 用于修饰方法作路径映射

    - String uri : 映射路径
    - HttpMethod method : Http请求方法

#### 使用 | Use

1、新建运行类，并在main函数中执行KatcherApplication.run()方法 ;

```java
public class TestApplication {
    public static void main(String[] args) {
        KatcherApplication.run(args, 2048, "com.kould.test");
    }
}
```

2、新建控制器，并使用@Controller注解修饰，同时对外接口方法也需要使用@Mapping修饰

```java
@Controller(uri = "/test")
public class TestController {

    @Mapping(uri = "/test", method = HttpMethod.GET)
    public String test(String args,String args1, String args2) {
        return args + args1 + args2;
    }
}

```

3、即刻运行！

#### 部署 | Build

Pom.xml打包推荐插件：

（该框架由于类似Spring Boot，基本只是替代Spring MVC，故无法直接使用Spring Boot Maven的打包插件，所以推荐使用下述插件进行替换）

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



#### 版本 | Version

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

