package com.itheima.alidayu;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.HashMap;
import java.util.Map;


public class Producer {

    /**
     * 实现普通消息发送
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        //创建一个消息发送入口对象,主要用于消息发送,指定生产者组
        DefaultMQProducer producer =new DefaultMQProducer("ProducerGroup");
        //设置NameServer地址，如果是集群环境，则用分号隔开
        producer.setNamesrvAddr("127.0.0.1:9876");
        //启动并创建消息发送组件
        producer.start();

        //封装数据
        Map<String,String> dataMap = new HashMap<String,String>();
        dataMap.put("mobile","电话号码");
        dataMap.put("signName","自己在阿里大于上面定义的签名");
        dataMap.put("templateCode","SMS_127156456");

        //模板数据
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("code",String.valueOf((int)(Math.random()*10000)));
        dataMap.put("param", JSON.toJSONString(paramMap));

        //创建消息对象
        String content = JSON.toJSONString(dataMap);
        Message message = new Message(
                "Topic-Mobile-Message",     //指定主题名称
                "TagMobileMessage",                  //指定标签名字
                "Mobile",                 //指定key的名字
                content.getBytes()); //指定要发送的数据的字节数组
        //执行消息发送
        producer.send(message);
        //关闭消息发送对象
        producer.shutdown();
    }
}