package com.cts.onlineShopping.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

	@KafkaListener(topics = "product_activities",groupId="onlineShopping")
	public void productListener(ConsumerRecord<Integer, String> consRec) {
		System.out.println("Received msg is" + consRec.toString());
	}
	
}
