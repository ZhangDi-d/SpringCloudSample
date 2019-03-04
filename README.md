# SpringCloudSample
A simple project of springcloud self-learning.

## 服务消费的两种方式
1.RestTemplate+Ribbon 
2.Feign去消费服务。

## Feign(声明式服务调用)
Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。

简而言之：
1.Feign 采用的是基于接口的注解
2.Feign 整合了ribbon，具有负载均衡的能力
3.整合了Hystrix，具有熔断的能力
----

## Hystrix
1.ribbon中使用断路器:

1.@EnableHystrix (启动类上加)  
2.@HystrixCommand(fallbackMethod = "失败时调用方法")(具体方法上加)

2.feign中使用断路器:
Feign是自带断路器的，在D版本的Spring Cloud之后，它没有默认打开.打开:feign.hystrix.enabled=true


## zuul
Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，比如／api/user转发到到user服务，/api/shop转发到到shop服务。zuul默认和Ribbon结合实现了负载均衡的功能。
