# SpringCloudSample
A simple project of springcloud self-learning.

## 一.服务注册与发现
### Eureka server
#### 1. 引入依赖 
```xml
<!--加入的 spring-cloud-starter-eureka-server 会自动引入 spring-boot-starter-web -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>

```

#### 2. 注解 @EnableEurekaServer 

#### 3. application.yml配置问题

如果euraka是单机部署,可使用以下配置:
```yaml
server:
  port: 8761
eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false #是否将自己注册到Eureka服务器,(因为自己是服务器:false)
    fetchRegistry: false #是否到Eureka服务器中拉取注册信息,(因为自己是服务器:false,这两项如果不写,启动会报错)
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

spring:
  application:
    name: eureka-server

```

如果是集群部署,需要将自己注册到其他的eurakaserver 上,所以配置为:
(假设三台eureka server 的ip分别为 127.0.0.1 ,127.0.0.2 ,127.0.0.3,端口为 8761,其他两台同理)
```yaml
server:
  port: 8761
eureka:
  instance:
    prefer-ip-address: true
    instance-id: eureka-127.0.0.1
  client:
    registerWithEureka: true #是否将自己注册到Eureka服务器
    fetchRegistry: true #是否到Eureka服务器中拉取注册信息
    serviceUrl:
      defaultZone: http://127.0.0.2:8761/eureka/,http://127.0.0.3:8761/eureka/

spring:
  application:
    name: eureka-server
```

### 服务提供者 ,以service-hello为例
#### 1. pom.xml配置
```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```

#### 2. @EnableEurekaClient 或者 @EnableDiscoveryClient
#### 3. application.yzmdl
```yaml
server:
  port: 8762

spring:
  application:
    name: service-hello #服务与服务之间相互调用一般都是根据这个name

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

3.2 添加 以下注解属性的好处是 服务以ip:port展示,
```yaml
instance:
    prefer-ip-address: true
    instance-id: ${spring.cloud.client.ipAddress}:${server.port}
```
未添加:
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200225093447111.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)
添加 instance: 注解:
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200225092837870.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)


3.3 集群 配置:
```yaml
server:
  port: 8762

spring:
  application:
    name: service-hello #服务与服务之间相互调用一般都是根据这个name

eureka:
  client:
    serviceUrl:
      defaultZone: http://127.0.0.1:8761/eureka/,http://127.0.0.2:8761/eureka/,http://127.0.0.3:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: {spring.cloud.client.ipAddress}:${server.port}
```


### Consul 
配合consul 注册中心使用,consul 下载和使用: https://blog.csdn.net/ShelleyLittlehero/article/details/104391744



## 二.服务消费的两种方式
1.RestTemplate+Ribbon 
2.Feign去消费服务

###  Ribbon 客户端负载均衡
#### 1. pom.xml
```xml
<dependencies>
        <!--作为服务被euraka发现-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!--spring mvc-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--客户端负载均衡组件依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
        </dependency>
        <!--在ribbon使用断路器的依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>

    </dependencies>

```

#### 2. application.yml
```yaml
spring:
  application:
    name: service-ribbon
server:
  port: 8764
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

#### 3.注解  
@EnableDiscoveryClient -> 通过@EnableDiscoveryClient向服务中心注册

@EnableHystrix -> 开启Hystrix

@LoadBalanced ->开启客户端负载均衡功能

#### 4. 测试
1.启动 euraka-server ->     EurekaServerApplication;

2.启动 service-ribbon  ->     ServiceRibbonApplication;

3.以 8762 端口 启动 service-hello ->       ServiceHelloApplication;

4.以 8763 端口 启动 service-hello ->       ServiceHelloApplication;

5.调用service-ribbon 的 接口  `http://localhost:8764/hello?name=zhangsan`, service-ribbon会使用restTemplate调用 service-hello


 


###  Spring Cloud Feign 声明式服务调用
Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。
```text
简而言之：
1.Feign 采用的是基于接口的注解
2.Feign 整合了ribbon，具有负载均衡的能力
3.整合了Hystrix，具有熔断的能力
```

#### 1. pom.xml
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
</dependencies>
```

#### 2. application.yml
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8765
spring:
  application:
    name: service-feign

#Feign是自带断路器的，在D版本的Spring Cloud之后，它没有默认打开
# feign.hystrix.enabled: true  或者下面的写法
feign:
  hystrix:
    enabled: true

```

#### 3.注解
@EnableDiscoveryClient ->作为服务被发现

@EnableFeignClients -> 开启Feign的功能

@FeignClient(value = "service-hello",fallback = SchedualServiceHelloHystric.class) ->指定调用哪个服务下的接口,并加上 fallback 容错


#### 4. 测试
1.启动 euraka-server ->     EurekaServerApplication;

2.启动 service-feign  ->     ServiceFeignApplication;

3. 启动 service-hello ->       ServiceHelloApplication;

4.调用 `http://localhost:8765/hello?name=zhangsan`, 查看是否可以调通service-hello



### Feign 文件上传
在Spring Cloud封装的Feign中并不直接支持传文件，但可以通过引入Feign的扩展包来实现
#### 1.service-hello 作为上传服务的提供方,只需添加上传文件的接口即可
```yaml
UploadProviderController.java
```

#### 2. service-feign 作为服务的消费方,调用service-hello接口到达上传文件的目的
注意 : pom.xml中要新增feign-form 和feign-form-spring 的依赖,并且他们的版本和Feign的版本一定要对应,不然会报错
```yaml
<dependency>
            <groupId>io.github.openfeign.form</groupId>
            <artifactId>feign-form</artifactId>
            <version>3.8.0</version>
        </dependency>
        <dependency>
            <groupId>io.github.openfeign.form</groupId>
            <artifactId>feign-form-spring</artifactId>
            <version>3.8.0</version>
        </dependency>
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.3.3</version>
        </dependency>
```

**版本对应问题:**
```txt
The feign-form extension depend on OpenFeign and its concrete versions:

1. all feign-form releases before 3.5.0 works with OpenFeign 9.* versions;
2. starting from feign-form's version 3.5.0, the module works with OpenFeign 10.1.0 versions and greater.

IMPORTANT: there is no backward compatibility and no any gurantee that the feign-form's versions after 3.5.0work with OpenFeign before 10.*. OpenFeign was refactored in 10th release, so the best approach - use the freshest OpenFeign and feign-form versions.
```

#### 3. 增加@Configuration 配置
```java
@Configuration
public class FeignSupportConfig {
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
}
```

#### 4. 在@FeignClient 注解中指定
```text
@FeignClient(value = "service-hello", fallback = FeignServiceHelloHystric.class,configuration = FeignSupportConfig.class)
``` 

#### 5.  启动eureka-server ,service-hello ,service-feign ,使用postman测试即可

#### 6. 使用idea 自带的接口测试工具 测试

更多可以参考:https://blog.csdn.net/u012954706/article/details/89383076

```http request

POST http://localhost:8765/uploadFile
Accept: */*
Cache-Control: no-cache
Content-Type: multipart/form-data; boundary=WebAppBoundary

--WebAppBoundary
Content-Disposition: form-data; name="file"; filename="D:\back\1.txt"

```


## 四.Spring Cloud Config 分布式配置中心
Spring Cloud Config 分布式配置中心 由两部分组成 config-server 和config-client.

### config-server  基于Git仓库的配置中心

####  1. config-server
访问配置信息的URL与配置文件的映射关系如下：
- /{application}/{profile}[/{label}]

- /{application}-{profile}.yml
- /{label}/{application}-{profile}.yml

- /{application}-{profile}.properties
- /{label}/{application}-{profile}.properties

#### 2. config-server pom.xml
```xml
 <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

#### 3. config-server application.yml

`https://gitee.com/xuelaiLittleHero/config-repo-demo` 是远程配置文件仓库的地址.

```yaml
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/xuelaiLittleHero/config-repo-demo
          searchPaths: config
#          username:
#          password:
server:
  port: 8766
```
#### 4. 配置server 的启动类 ,并加注解  @EnableConfigServer, 开启Spring Cloud Config的服务端功能

### config-client  使用配置中心的客户端

#### 1.pom.xml
```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

#### 2. bootstrap.yml (使用配置服务的客户端,配置文件应为 bootstrap.xml或者 bootstrap.properties ,他的加载早于 application.yml)

```yaml
spring:
  application:
    name: config-client
  cloud:
    config:
      uri: http://localhost:8766/
      profile: dev
      label: master
```
**涉及到使用配置服务的配置要存放于bootstrap.xml或者 bootstrap.properties,这样才能保证config-server中的配置信息才能被正确加载**

#### 3. 测试 ,使用接口 `http://localhost:8767/getInfo` 测试配置是否能被拿到 .

### 分布式配置中心（加密解密,以对称加密为例）

#### 1.下载配置JCE 

地址 :http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip
配置到 jdk 目录中 .

#### 2. 配置 加密 key
config-server bootstrap.yml 配置对称加密的key
```yaml
encrypt:
  key: ryze
``` 

#### 3. 加密
启动config-server, 访问  http://localhost:8766/encrypt/status ，显示状态OK。
使用 curl 访问 /encrypt 端点进行加密, 获得属性 pa2sW0rd 加密后的值 ,配置在 远程git 仓库的配置文件中 
```curl
C:\Users\张 迪>curl http://localhost:8766/encrypt/ -d pa2sW0rd

9ae2d08f248ab77561cbea8fe88566b7665f8ad65527e7757dcf1cd3bffe1aae
```

git 仓库配置文件 config-client-dev.yml
**一定注意 当配置文件是yml格式的时候 ,使用 {cipher}要加单引号,因为yml格式严格,不加''无法解析 **

```yaml
info:
  profile: dev
  from: config/dev
  secretValue: '{cipher}9ae2d08f248ab77561cbea8fe88566b7665f8ad65527e7757dcf1cd3bffe1aae'
```

#### 4. 测试 
验证config client 是否可以获取到正确的加密值:
`http://localhost:8767/getInfo` -> 输出 `InfoController getInfo===============>profile=dev,from=config/dev,secretValue=pa2sW0rd`,ok.


### spring cloud config 高可用与动态刷新
#### 高可用
config server 的高可用,可以使用集群部署 config server ,让他们指向同一个 git配置文件库, 然后使用 负载均衡 ,config client 动态的去 指定 config server.
另一种更为简单的做法是,将集群部署的config server 也注册为服务,供eureka 发现. 这样config client可以 以服务的方式去访问 config server.

##### 1. config server  pom.xml
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

``` 

##### 2. config server application.yml 
增加 eureka 地址 : 

```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```
eureka 的配置最好放在最后,不要放在  git 和 spring 之间 ,不要想下面这样: 这样 spring会认为git 是配置在 eureka下的,启动会报错
```yaml

spring:
  application:
    name: config-server
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/xuelaiLittleHero/config-repo-demo
          searchPaths: config
#          username:
#          password:

server:
  port: 8766



```

##### 3. config server  @EnableDiscoveryClient 
  在启动类上 增加注解 @EnableDiscoveryClient ->注册为服务 ,供euraka发现
  
  
##### 4. config client pom.xml
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

``` 

##### 5. config client bootstrap.yml 
bootstrap.yml 做以下修改 : 注释 之前config-client 直连 config-server 的配置 ; 

增加 config-client 通过 服务注册与发现 调用 config-server 的配置. 

```yaml
  ## 注意 :  当 config client 不直接访问 config server 时 ,这段配置就需要注释掉了
#  cloud:
#    config:
#      uri: http://localhost:8766/
#      profile: dev
#      label: master
 ## 注意  config client 以服务的方式 访问 config server 时 ,要增加 eureka 的配置 和 config 的相关配置
  cloud:
    config:
      discovery:
        enabled: true  # 开启通过服务来访问Config Server的功能
        service-id: config-server # 指定Config Server注册的服务名
      profile: dev # 用于定位Git中的资源
# 指定服务注册中心，用于服务的注册与发现
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```
 
 #### 配置的自动刷新
   config-server 不用做修改,修改主要在config-client中.
   
 #####  config-client pom.xml
 增加监控 组件,它包含/refresh 端点:
 
 
 ```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
 
 ##### bootstrap.yml
 
 注意 但是SpringCloud 2.0.0 我们需要在bootstrap.yml里面加上需要暴露出来的地址 , 刷新地址不是/refresh了,默认是/actuator/refresh
  base-path可以自定义路径->/config/refresh;
  
 ```yaml
management:
  endpoints:
    web:
      exposure:
        include: refresh,health
      base-path: /config
``` 
 
 ##### @RefreshScope  
  在需要刷新参数的类上加@RefreshScope ,实现自动刷新

##### 测试 
使用post请求刷新端口,查看前后  `http://localhost:8767/getInfo` 是否有值的变化.

```http request
POST http://localhost:8767/config/refresh
Accept: */*
Cache-Control: no-cache

```

##  Hystrix
Hystrix具备了服务降级、服务熔断、线程隔离、请求缓存、请求合并以及服务监控等强大功能。

### Hystrix服务降级
涉及模块: eureka-server , service-ribbon, service-hello


####  service-ribbon pom.xml 
service-ribbon pom.xml增加hystrix 的依赖
```xml
<!--在ribbon使用断路器的依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

#### 注解 
使用 @SpringCloudApplication 或者 @EnableHystrix 或者 @EnableCircuitBreaker 开启服务降级

#### 注解  
在service-ribbon 中调用 service-hello 的方法上加上 @ HystrixCommand,并指定fallbackMethod熔断方法
```text

    @HystrixCommand(fallbackMethod = "helloError") //在ribbon中使用断路器,该注解对该方法创建了熔断器的功能，并指定了fallbackMethod熔断方法
    public String helloService(String name) {
        return restTemplate.getForObject("http://service-hello/hello?name=" + name, String.class);
    }
```

#### 测试 
启动 eureka-server,service-ribbon ,不启动 service-hello,访问 `http://localhost:8764/hello?name=zhangsan` ,提示 `hello,zhangsan,sorry,error!`


### Hystrix依赖隔离
线程池隔离和信号量隔离

### Hystrix断路器
“断路器”本身是一种开关装置，用于在电路上保护线路过载，当线路中有电器发生短路时，“断路器”能够及时的切断故障电路，防止发生过载、发热、甚至起火等严重后果。

在Hystrix服务降级一节中,我们没有启动service-hello 服务提供方,导致service-ribbon 触发了降级逻辑,
但是即使这样，受限于Hystrix超时时间的问题，我们的调用依然很有可能产生堆积。

这个时候断路器就会发挥作用，那么断路器是在什么情况下开始起作用呢？这里涉及到断路器的三个重要参数：**快照时间窗、请求总数下限、错误百分比下限**。这个参数的作用分别是：

- 快照时间窗：断路器确定是否打开需要统计一些请求和错误数据，而统计的时间范围就是快照时间窗，默认为最近的10秒。
- 请求总数下限：在快照时间窗内，必须满足请求总数下限才有资格根据熔断。默认为20，意味着在10秒内，如果该hystrix命令的调用此时不足20次，即时所有的请求都超时或其他原因失败，断路器都不会打开。
- 错误百分比下限：当请求总数在快照时间窗内超过了下限，比如发生了30次调用，如果在这30次调用中，有16次发生了超时异常，也就是超过50%的错误百分比，在默认设定50%下限情况下，这时候就会将断路器打开。

那么当断路器打开之后会发生什么呢？我们先来说说断路器未打开之前，对于之前那个示例的情况就是每个请求都会在当hystrix超时之后返回fallback，每个请求时间延迟就是近似hystrix的超时时间，如果设置为5秒，那么每个请求就都要延迟5秒才会返回。当熔断器在10秒内发现请求总数超过20，并且错误百分比超过50%，这个时候熔断器打开。打开之后，再有请求调用的时候，将不会调用主逻辑，而是直接调用降级逻辑，这个时候就不会等待5秒之后才返回fallback。

**通过断路器，实现了自动地发现错误并将降级逻辑切换为主逻辑，减少响应延迟的效果。**

在断路器打开之后，处理逻辑并没有结束，我们的降级逻辑已经被成了主逻辑，那么原来的主逻辑要如何恢复呢？对于这一问题，hystrix也为我们实现了自动恢复功能。当断路器打开，对主逻辑进行熔断之后，hystrix会启动一个休眠时间窗，在这个时间窗内，降级逻辑是临时的成为主逻辑，当休眠时间窗到期，断路器将进入半开状态，释放一次请求到原来的主逻辑上，如果此次请求正常返回，那么断路器将继续闭合，主逻辑恢复，如果这次请求依然有问题，断路器继续进入打开状态，休眠时间窗重新计时。


### Hystrix监控面板
涉及模块 eureka-server , service-hello (服务提供方), service-ribbon (调用service-hello的消费方), hystrix-dashboard(监控模块);


#### 1. 新建 hystrix-dashboard 模块
pom.xml 中引入关键依赖如下 :
```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
    </dependencies>

```

#### 2. application.yml 
```yaml
spring:
  application:
    name: hystrix-dashboard
server:
  port: 8768
```

#### 3. 添加启动类,并且添加合适的注解

```java
@EnableHystrixDashboard //开启监控页面
@SpringCloudApplication //包含三个注解 , 开启服务注册与发现 , 开启服务容错
public class HystrixDashBoardApplication {
    public static void main(String[] args) {
        SpringApplication.run(HystrixDashBoardApplication.class, args);
    }
}
```

#### 4.测试 
访问`http://localhost:8768/hystrix ` ,弹出以下界面则服务启动成功

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200227132358435.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)

Hystrix Dashboard共支持三种不同的监控方式，依次为：

- 默认的集群监控：通过URLhttp://turbine-hostname:port/turbine.stream开启，实现对默认集群的监控。
- 指定的集群监控：通过URLhttp://turbine-hostname:port/turbine.stream?cluster=[clusterName]开启，实现对clusterName集群的监控。
- 单体应用的监控：通过URLhttps://hystrix-app:port/actuator/hystrix.stream开启，实现对具体某个服务实例的监控。

参数 : 
Delay：该参数用来控制服务器上轮询监控信息的延迟时间，默认为2000毫秒，我们可以通过配置该属性来降低客户端的网络和CPU消耗。
Title：该参数对应了上图头部标题Hystrix Stream之后的内容，默认会使用具体监控实例的URL，我们可以通过配置该信息来展示更合适的标题。


#### 5. service-ribbon pom.xml
pom.xml 新增与hystrix 相关的依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
<!--dashboard-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
<!--监控-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### 6. service-ribbon新增配置 
service-ribbon 已经 添加过注解@EnableHystrix  , springboot2.x 之后还需要在service-ribbon模块中新增以下配置 :

```text
//springboot 2.x 之后 需要在想要监控的服务中添加 一下内容 ,
    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/actuator/hystrix.stream"); // 此地址是在hystrix-dashboard 中输入的监控的地址
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
```

#### 7. 需要监控的接口上一定要有 @HystrixCommand 注解,否则无法被监控到

#### 8. 测试
启动 eureka-server , service-hello , service-ribbon , hystrix-dashboard;

在 hystrix-dashboard 的首页(一个豪猪的页面) 输入监控模块的url `localhost:8764/actuator/hystrix.stream`

点击按钮,进入监控页面 ,发现 一直在loading ,此时需要调用 以下服务接口,否则数据不会出来 

调用接口 `http://localhost:8764/hello?name=lisi`  , 弹出 `hello lisi ,i am from port:8762` 

此时再查看hystrix-dashboard ,发现已经出现数据 :

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200227135624576.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)

根据各种颜色,区分请求状态对应的的请求数.



### Hystrix监控数据聚合
 
 涉及模块 :
 - eureka-server(注册中心),
 - service-hello(服务提供方),
 - service-ribbon(服务消费方,同时也是被监控者),
 - hystrix-dashboard(监控面板), 
 - turbine (数据聚合) 
 
 新建模块  turbine

#### turbine pom.xml
```xml
<!-- 提供者消费者 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!-- actuator -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!-- hystrix -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <!-- dashboard -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
        <!-- turbine -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-netflix-turbine</artifactId>
        </dependency>
```  

#### turbine application.yml

```yaml
server:
  port: 8770
spring:
  application:
    name: trubine
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
turbine:
  app-config: service-ribbon # 指定了需要收集监控信息的服务名；
  combine-host-port: true
  cluster-name-expression: new String('default') #参数指定了集群名称为default， "default" 会报错
management:
  port: 8771
```

#### 启动类 注解

```java
@SpringBootApplication 
@EnableTurbine //开启 turbine'
@EnableDiscoveryClient  //开启服务注册与 发现
@EnableHystrixDashboard //开启 hystrix
public class TurbineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TurbineApplication.class, args);

    }
}
```

####  测试 
启动 
 - eureka-server(注册中心),
 - service-hello(服务提供方),
 - service-ribbon(服务消费方,同时也是被监控者),
 - hystrix-dashboard(监控面板), 
 - turbine (数据聚合);
 
 访问`http://localhost:8768/hystrix` ,打开hystrix-dashboard 首页 ,输入 `localhost:8764/actuator/hystrix.stream` ,进入 service-ribbon 的 监控页面 
 
 访问`http://localhost:8768/hystrix` ,打开hystrix-dashboard 首页 ,输入 `http://localhost:8770/turbine.stream` ,进入 turbine 的 监控页面;
 
 访问`http://localhost:8764/hello?name=zhangsan` ;
 
 如果 两个监控页面发生了变化,证明ok
 
 service-ribbon 监控页面:
 
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200227164448228.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)
 
 turbine 聚合视图 :
 
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/2020022716451624.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)
  
  

 ### Hystrix监控数据聚合(amqp)
 涉及模块 :
- eureka-server(注册中心),
- service-hello(服务提供方),
- service-ribbon(服务消费方,同时也是被监控者),
- hystrix-dashboard(监控面板), 
- turbine-amqp (数据聚合-amqp 方式) 
- rabbit 服务也要正常启动
        
新建模块  turbine-amqp
        
 ####  turbine-amqp pom.xml
 ```xml
        <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
                </dependency>
                <!-- actuator -->
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-actuator</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-netflix-turbine-stream</artifactId>
                </dependency>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
                </dependency>
                <!--这个是多余的  todo :待验证-->
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-netflix-hystrix-stream</artifactId>
                </dependency>
```

#### turbine-amqp application.yml
```yaml
server:
  port: 8773
spring:
  application:
    name: turbine-amqp
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
management:
  port: 8774
```

#### 启动类
```java
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableTurbineStream //开启turbine流
public class TurbineAmqpApplication {
    public static void main(String[] args) {
        SpringApplication.run(TurbineAmqpApplication.class, args);
    }
}
```

#### 对 service-ribbon (service-hello 的服务消费端) 修改 pom.xml

pom.xml 增加依赖:
```xml
<!--turbine 通过 amqp方式聚合hystrix监控信息 需要添加的依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-netflix-hystrix-stream</artifactId>
        </dependency>
        <!--springboot 2.x 在引入 spring-cloud-netflix-hystrix-stream ,还要引入spring-cloud-starter-stream-rabbit
            否则会报错 : A default binder has been requested, but there is no binder available ,原因是因为 hystrix 需要一个持续的输出源,
            hystrix-stream的输出源有rabbit和kafa之类。加上相应的依赖解决报错问题
        -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
        </dependency>
    </dependencies>
```

#### service-ribbon application.yml
增加 rabbitmq 的配置
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8764
spring:
  application:
    name: service-ribbon
    rabbitmq:
      host: 127.0.0.1
      port: 5672
      username: guest
      password: guest
```

#### 测试 : 

1. 启动 rabbitMq ,并且访问   `http://localhost:15672/` 确认正常启动;

2. 启动 eureka-server, service-hello ,service-ribbon, hystrix-dashboard(监控面板), turbine-amqp; 

3. 访问 `http://localhost:8768/hystrix/`  ,打开豪猪页面 , 

4. 访问 `http://localhost:8764/hello?name=lisi` ,调用 接口

5. 在 豪猪页面输入 `http://localhost:8764/actuator/hystrix.stream`  ,打开对service-ribbon 的单个的监控;

6. 在 豪猪页面输入 `http://localhost:8773/turbine.stream`  ,打开对hystrix的聚合的turbine的页面, 发现无法连接, 这里有一个需要注意的地方:

#### 如何解决 trubine-amqp 无法显示 的问题 

1. 访问 `http://localhost:8773/turbine.stream` , 发现 
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020022817302755.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)

2. 打开RabbitMq监控页面 ,查看 交换机情况 ,两个交换机都在
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200228173138646.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)

3. 进入 hystrixStreamOutput 发现有输入 ,但是没有 输入
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200228172112756.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)
 
4. 进入hystrixStreamOutput ,将其与   turbineStreamInput 绑定起来 ,具体操作如下 

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020022817343190.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)

5. 查看是否绑定成功, 点击交换机 turbineStreamInput ,发现如下图则绑定成功,
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200228173523739.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)

6. 在 豪猪页面输入 `http://localhost:8773/turbine.stream`  ,打开对hystrix的聚合的turbine的页面 ,ok
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200228173658177.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L1NoZWxsZXlMaXR0bGVoZXJv,size_16,color_FFFFFF,t_70)

truebine-amqp 到此完成.


## 服务网关 Zuul

### 路由功能
新建模块 service-zuul,

涉及模块:
 - eureka-server(注册中心),
 - service-hello(服务提供方),
 - service-ribbon(服务调用方),
 - service-ribbon(服务调用方),
 - service-zuul(服务网关)
 
 #### service-zuul pom.xml
 
 ```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```

### service-zuul application.yml

在Eureka的帮助下，API网关服务本身就已经维护了系统中所有serviceId与实例地址的映射关系。当有外部请求到达API网关的时候，
根据请求的URL路径找到最佳匹配的path规则，API网关就可以知道要将该请求路由到哪个具体的serviceId上去。

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
server:
  port: 8769
spring:
  application:
    name: service-zuul
#首先指定服务注册中心的地址为http://localhost:8761/eureka/，服务的端口为8769，服务名为service-zuul；
#以/api-a/ 开头的请求都转发给service-ribbon服务；以/api-b/开头的请求都转发给service-feign服务
zuul:
  routes:
    api-a:
      path: /api-a/**
      serviceId: service-ribbon  
    api-b:
      path: /api-b/**
      serviceId: service-feign
```

#### service-zuul  注解启动类

@EnableZuulProxy，开启zuul的功能

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy    //@EnableZuulProxy，开启zuul的功能
public class ServiceZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceZuulApplication.class, args);
    }

}
```

#### 测试 
启动:
 - eureka-server(注册中心),
 - service-hello(服务提供方),
 - service-ribbon(服务调用方),
 - service-ribbon(服务调用方),
 - service-zuul(服务网关)
 
 分别使用 `http://localhost:8769/api-a/hello?name=zhangsan` 和 `http://localhost:8769/api-b/hello?name=zhangsan` 访问,观察service-ribbon和 service-feign的控制台输出
 
 
 
 ### 服务网关之过滤器
 服务网关的另一个核心功能就是过滤器.
 
 #### 新增过滤器 MyFilter
 MyFilter验证一下 请求中是否含有token,
 
 在Spring Cloud Zuul中实现的过滤器必须包含4个基本特征：过滤类型、执行顺序、执行条件、具体操作。实际上它就是ZuulFilter接口中定义的四个抽象方法：
```text
 String filterType();
 int filterOrder();    
 boolean shouldFilter();   
 Object run();
``` 

 ```java
public class MyFilter extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(MyFilter.class);

    /**
     * filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
     * pre：路由之前
     * routing：路由之时
     * post： 路由之后
     * error：发送错误调用
     * filterOrder：过滤的顺序,当请求在一个阶段中存在多个过滤器时，需要根据该方法返回的值来依次执行。
     * shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,因此该过滤器对所有请求都会生效。
     * run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。
     */

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
        String token = request.getParameter("token");
        if (token == null) {
            log.warn("token is empty");
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(401);
            try {
                context.getResponse().getWriter().write("token is empty");
            } catch (Exception e) {
            }
            return null;
        }
        return null;
    }
}
```
 
 #### 配置过滤器
 编写过滤器完成后需要配置过滤器,让其生效
 
```java
@Configuration
public class FilterConfig {

    //配置过滤器,否则不会生效
    @Bean
    public MyFilter myFilter() {
        return new MyFilter();
    }
}
```

#### 测试 
重启模块service-zuul:

访问 `http://localhost:8769/api-b/hello?name=zhangsan` ,提示 `token is empty`;

访问 `http://localhost:8769/api-b/hello?name=zhangsan&token=123` ,提示 `hello zhangsan ,i am from port:8762`; ok.


### 服务网关之统一异常处理

#### 自定义 ErrorFilter 
由于在请求生命周期的pre、route、post三个阶段中有异常抛出的时候都会进入error阶段的处理，所以我们可以通过创建一个error类型的过滤器来捕获这些异常信息，并根据这些异常信息在请求上下文中注入需要返回给客户端的错误描述，这里我们可以直接沿用在try-catch处理异常信息时用的那些error参数，这样就可以让这些信息被SendErrorFilter捕获并组织成消息响应返回给客户端。比如，下面的代码就实现了这里所描述的一个过滤器：

```java
public class ErrorFilter extends ZuulFilter {

    Logger log = LoggerFactory.getLogger(ErrorFilter.class);

    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Throwable throwable = ctx.getThrowable();
        log.error("this is a ErrorFilter : {}", throwable.getCause().getMessage());
        ctx.set("error.status_code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ctx.set("error.exception", throwable.getCause());
        return null;
    }

}
```

更多 请参考 :http://blog.didispace.com/spring-cloud-zuul-exception-3/
 


## Spring Cloud Stream 消息驱动
Spring Cloud Stream 在 Spring Cloud 体系内用于构建高度可扩展的基于事件驱动的微服务，其目的是为了简化消息在 Spring Cloud 应用程序中的开发。

### 概念 
group : 
组内只有1个实例消费。如果不设置group，则stream会自动为每个实例创建匿名且独立的group——于是每个实例都会消费。

partition: 
一个或多个生产者将数据发送到多个消费者，并确保有共同特征标识的数据由同一个消费者处理。默认是对消息进行hashCode，然后根据分区个数取余，所以对于相同的消息，总会落到同一个消费者上。

destination binder:
与外部消息系统通信的组件，为构造 Binding提供了 2 个方法，分别是 bindConsumer 和 bindProducer ，它们分别用于构造生产者和消费者。Binder使Spring Cloud Stream应用程序可以灵活地连接到中间件，目前spring为kafka、rabbitmq提供binder。

destination binding
Binding 是连接应用程序跟消息中间件的桥梁，用于消息的消费和生产，由binder创建。  使用@EnableBinding即可定义destination binding

新建模块 stream-hello,(也可以新增 两个模块stream-producer,stream-consumer,一个作为生产者使用,一个座位消费者使用)

涉及模块 :stream-hello,(stream-producer,stream-consumer)

### 使用官方的Sink.class Source.class 简单测试

#### stream-hello  pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
<!--spring-cloud-starter-stream-rabbit 可替换为
   <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
   </dependency>
   -->
```

#### producer application.yml
```yaml
server:
  port: 8775  # stream-hello 分别以 8775(producer) ,8776(concumer) 启动两次 
spring:
  application:
    name: stream-hello
  cloud:
    stream:
      bindings: # 外部消息传递系统和应用程序之间的桥梁，提供消息的“生产者”和“消费者”（由目标绑定器创建）
        output:
          destination: stream-exchange
          binder: localhost_rabbit  #也可以是其他中间件 如 kafka
      binders: #目标绑定器，目标指的是 kafka 还是 RabbitMQ，绑定器就是封装了目标中间件的包。
        localhost_rabbit:
          type: rabbit
          environment:
            spring:
              rabbitmq:
                host: 127.0.0.1
                port: 5672
                username: guest
                password: guest
```  

#### consumer application.yml

```yaml
server:
  port: 8776  # stream-hello 分别以 8775(producer) ,8776(concumer) 启动两次 
spring:
  application:
    name: stream-hello
  cloud:
    stream:
      bindings: # 外部消息传递系统和应用程序之间的桥梁，提供消息的“生产者”和“消费者”（由目标绑定器创建）
        input:
          destination: stream-exchange # 指 exchange 的名称
          binder: localhost_rabbit
      binders: #目标绑定器，目标指的是 kafka 还是 RabbitMQ，绑定器就是封装了目标中间件的包。
        localhost_rabbit:
          type: rabbit
          environment:
            spring:
              rabbitmq:
                host: 127.0.0.1
                port: 5672
                username: guest
                password: guest
```

#### Producer
```java
@EnableBinding(Source.class)
public class Producer {
    private static Logger logger = LoggerFactory.getLogger(Producer.class);
    @Autowired
    @Output(Source.OUTPUT)
    private MessageChannel channel;

    public void send(String message) {
        logger.info("send massage begin...............................");
        channel.send(MessageBuilder.withPayload("Producer send massage:" + message).build());
        logger.info("send massage end...............................");
    }
}
```

#### Consumer

```java
//当我们需要为@EnableBinding指定多个接口来绑定消息通道的时候，可以这样定义：@EnableBinding(value = {Sink.class, Source.class})
//注解用来指定一个或多个定义了@Input或@Output注解的接口，以此实现对消息通道（Channel）的绑定
@EnableBinding(Sink.class)
public class Consumer {
    private static Logger logger = LoggerFactory.getLogger(Consumer.class);

    @StreamListener(Sink.INPUT) //该注解主要定义在方法上，作用是将被修饰的方法注册为消息中间件上数据流的事件监听器，注解中的属性值对应了监听的消息通道名
    public void receive(Object o) {
        logger.info("receive message: " + o);
    }
}

```

#### 发送信息的类 

可以用手动发送信息的接口:
```java
@RestController
@RequestMapping
public class ProduceController {
    private static Logger logger = LoggerFactory.getLogger(ProduceController.class);
    @Resource
    private Producer producer;

    @RequestMapping("/send")
    public void sendMessage(String message) {
        producer.send("ProduceController send message:" + message);
    }
}
```
也可以自动发送信息
```java
@EnableBinding(Source.class)
public class TimerProcuer {
    private static Logger logger = LoggerFactory.getLogger(TimerProcuer.class);
    private final String format  = "yyyy-MM-dd HH:mm:ss";

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = @Poller(fixedDelay = "5000", maxMessagesPerPoll = "1"))
    public MessageSource<String> timerMessageSource() {
        logger.info("TimerProcuer sendMessage begin ..........");
        return () -> new GenericMessage<>(new SimpleDateFormat(format).format(new Date()));
    }
}
```

#### 测试 
1. 启动 rabbitMq
2. 以 8775 ,output 配置段 启动 stream-hello 作为生产者
3. 以 8776 ,input 配置段 启动 stream-hello 作为消费者
4. 手动发送消息 `http://localhost:8775/send?message=zhangsan`  ,查看控制台 
5. 8775 producer 控制台 
```text
com.ryze.sample.send.Producer            : send massage begin...............................
com.ryze.sample.send.Producer            : send massage end...............................

```
6. 8776 consumer 控制台 :
```text
com.ryze.sample.receive.Consumer         : receive message: Producer send massage:ProduceController send message:zhangsan
```





--------
## 模块及占用端口 : 
eureka-server : 8761
service-hello : 8762 8763
service-ribbon : 8764 
service-feign : 8765
config-server : 8766
config-client : 8767
hystrix-dashboard : 8768
service-zuul : 8769
turbine : 8770 8771
service-hello-consul : 8772
turbine-amqp : 8773 8774
stream-hello : 8775(producer) 8776(consumer)


---------------------
拾遗:
1.ribbon中使用断路器:

1.@EnableHystrix (启动类上加)  
2.@HystrixCommand(fallbackMethod = "失败时调用方法")(具体方法上加)

2.feign中使用断路器:
Feign是自带断路器的，在D版本的Spring Cloud之后，它没有默认打开.打开:feign.hystrix.enabled=true

Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，比如／api/user转发到到user服务，/api/shop转发到到shop服务。zuul默认和Ribbon结合实现了负载均衡的功能。


----------------------------------------------------------

**本文 参考 :**
http://www.itmuch.com/spring-cloud 作者:周立
http://blog.didispace.com/ 作者 :程序员DD