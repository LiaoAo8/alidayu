package com.itheima.alidayu;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("messageListener")
class MessageListener implements MessageListenerConcurrently {

    @Autowired
    private MessageSender messageSender;

    /***
     * 消息监听
     * @param msgs
     * @param context
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            //这里可以循环读取消息集合,读取第1条记录
            MessageExt messageExt = msgs.get(0);

            //获取消息内容体
            String content = new String(messageExt.getBody(),"UTF-8");

            //将消息转换成Map
            Map<String,String> map = JSON.parseObject(content, Map.class);

            //获取要发送短信的手机号
            String mobile = map.get("mobile");
            //获取要发送短信的签名
            String signName = map.get("signName");
            //获取要发送短信的模板编号
            String templateCode = map.get("templateCode");
            //获取需要发送的参数数据
            String param = map.get("param");

            //发送短信
            SendSmsResponse response = messageSender.sendSms(mobile, signName, templateCode, param);

            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
        } catch (Exception e) {
            e.printStackTrace();
            //消费失败
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        //消费成功
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}