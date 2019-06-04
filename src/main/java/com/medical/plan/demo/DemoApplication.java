package com.medical.plan.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.Filter;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	RedisTemplate<String,Map> redisPlanTemplate() {
		RedisTemplate<String,Map> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		return redisTemplate;
	}

	@Bean
	public Filter shallowEtagFilter() {
		return new ShallowEtagHeaderFilter();
	}
	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);
	}
}
