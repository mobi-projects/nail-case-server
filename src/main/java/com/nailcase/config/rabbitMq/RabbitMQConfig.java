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

	@Bean
	public Queue queue() {
		return new Queue(CHAT_QUEUE_NAME, true);
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