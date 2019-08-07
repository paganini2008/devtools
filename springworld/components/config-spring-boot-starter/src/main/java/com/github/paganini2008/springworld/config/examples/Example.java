package com.github.paganini2008.springworld.config.examples;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.github.paganini2008.springworld.config.BeanChangeEvent;
import com.github.paganini2008.springworld.config.Keeping;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * Example
 * 
 * @author Fred Feng
 * @created 2019-08
 * @revised 2019-08
 * @version 1.0
 */
@Slf4j
@Getter
@Setter
@ToString
@Component("test-2")
@ConfigurationProperties(prefix = "example", ignoreUnknownFields = true)
public class Example implements ApplicationListener<BeanChangeEvent> {

	@Keeping
	private byte byteValue;

	@Keeping
	private int intValue;

	@Keeping
	private Long longValue;

	@Keeping
	private BigDecimal bigValue;

	@Keeping
	private boolean booleanValue;

	@Keeping
	private String stringValue;

	@Keeping
	private char charValue;

	@Override
	public void onApplicationEvent(BeanChangeEvent event) {
		log.info("[BeanChange] name: " + event.getBeanName() + ", class: " + event.getBeanClass());
	}

}
