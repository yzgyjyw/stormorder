package com.njust.stormorder;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class OrderProducer {
	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("metadata.broker.list", "bigdata02:9092,bigdata03:9092,bigdata04:9092,bigdata05:9092");
		/**
		 * 0表示producer毋须等待leader的确认，
		 * 1代表需要leader partition确认写入它的本地log并立即确认，
		 * -1代表所有的备份都完成后确认。 仅仅for sync
		 */
		//注意，这里的第二参数最好为string类型，而不是int类型
		props.put("request.required.acks","1");
		
		Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(props));
		
		for(int i=0;i<1000;i++){
			//第一个参数：topic
			//第二个是参数：消息的key
			//第三个参数：消息内容
			producer.send(new KeyedMessage<String, String>("orders", i+"",new OrderBean().getOrder()));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
