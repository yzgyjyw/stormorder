package com.njust.stormorder;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.google.gson.Gson;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class OrderParseBolt extends BaseRichBolt {

	private OutputCollector collector;
	private JedisPool jedisPool;

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		JedisPoolConfig config = new JedisPoolConfig();
		// 控制一个JedisPool中最多有多少个空闲的Jedis实例
		config.setMaxIdle(3);
		// 设置Jedispool中最多有多少个Jedis实例
		// -1：不限制
		config.setMaxTotal(1000);
		// 表示引入一个jedis实例时，最大的等待时间，mills
		config.setMaxWaitMillis(300);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		jedisPool = new JedisPool(config, "bigdata02", 6379);
	}

	public void execute(Tuple input) {
		// 从JedisPool中获取Jedis实例
		Jedis jedis = jedisPool.getResource();
		String json = new String((byte[]) input.getValue(0));
		OrderBean order = new Gson().fromJson(json, OrderBean.class);
		// 获取订单支付金额
		long payPrice = order.getPayPrice();		
		jedis.incrBy("total_pay", payPrice);
		jedis.close();
		
		collector.ack(input);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

}
