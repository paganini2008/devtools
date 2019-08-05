package com.allyes.mec.cloud.config.other;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class RedisHealthIndicator implements HealthIndicator{

	@Override
	public Health health() {
		return Health.up().build();
	}

}
