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
```java
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




----

## Hystrix
1.ribbon中使用断路器:

1.@EnableHystrix (启动类上加)  
2.@HystrixCommand(fallbackMethod = "失败时调用方法")(具体方法上加)

2.feign中使用断路器:
Feign是自带断路器的，在D版本的Spring Cloud之后，它没有默认打开.打开:feign.hystrix.enabled=true


## zuul
Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，比如／api/user转发到到user服务，/api/shop转发到到shop服务。zuul默认和Ribbon结合实现了负载均衡的功能。
