package com.nailcase.config.rabbitMq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RabbitMQConfig {
	public static final String CHAT_EXCHANGE_NAME = "chat.exchange";
	public static final String CHAT_QUEUE_NAME = "chat.queue";
	public static final String CHAT_ROUTING_KEY = "shop.*.room.#";

	@Value("${spring.rabbitmq.host}")
	private String rabbitHost;

	@Value("${spring.rabbitmq.port}")
	private int rabbitPort;

	@Value("${spring.rabbitmq.username}")
	private String rabbitUsername;

	@Value("${spring.rabbitmq.password}")
	private String rabbitPassword;

	@Value("${spring.rabbitmq.stomp.port}")
	private int rabbitStompPort;

	@Bean
	public Queue queue() {
		return new Queue(CHAT_QUEUE_NAME, true, false, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(CHAT_EXCHANGE_NAME);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(CHAT_ROUTING_KEY);
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());

		// Publisher Confirms 활성화
		rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
			if (!ack) {
				log.error("Failed to send message to RabbitMQ. Cause: " + cause);
			}
		});

		// Mandatory 설정 및 Returns Callback 설정
		rabbitTemplate.setMandatory(true);
		rabbitTemplate.setReturnsCallback(returned -> {
			log.error("Message returned from RabbitMQ. Reply: {} Exchange: {} RoutingKey: {}",
				returned.getReplyText(),
				returned.getExchange(),
				returned.getRoutingKey());
		});

		return rabbitTemplate;
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(CHAT_QUEUE_NAME);
		return container;
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost(rabbitHost);
		factory.setPort(rabbitPort);
		factory.setUsername(rabbitUsername);
		factory.setPassword(rabbitPassword);

		factory.setPublisherReturns(true);

		return factory;
	}

	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		objectMapper.registerModule(new JavaTimeModule());
		return new Jackson2JsonMessageConverter(objectMapper);
	}
}