package com.ryze.sample.receive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * Created by xueLai on 2020/3/2.
 */
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
