package com.njust.stormorder;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

public class OrderTopology {
	public static void main(String[] args) throws Exception {
		String zks = "bigdata02:2181,bigdata03:2181,bigdata04:2181,bigdata05:2181";
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		topologyBuilder.setSpout("kafkaSpout", new KafkaSpout(new SpoutConfig(new ZkHosts(zks), "orders", "/orderSpout", "1")), 1);
		topologyBuilder.setBolt("orderParseBolt", new OrderParseBolt(), 2).shuffleGrouping("kafkaSpout");
		
		Config config = new Config();
		config.setNumWorkers(2);
		
		if(args.length>0){
			StormSubmitter.submitTopologyWithProgressBar(args[0], config, topologyBuilder.createTopology());
		}else{
			LocalCluster localCluster = new LocalCluster();
			localCluster.submitTopology("orderAnalysis", config, topologyBuilder.createTopology());
		}
		
	}
}
