package com.github.paganini2008.springworld.amber.test;

import java.util.List;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class TestRedisExEvent {
	public TestRedisExEvent() {

	}

	static class MyPubSub extends JedisPubSub {

		@Override
		public void onMessage(String channel, String message) {
			System.out.println("onMessage: " + channel + ", " + message);
		}

		@Override
		public void onPMessage(String pattern, String channel, String message) {
			System.out.println("onPMessage: " + pattern + ", " + channel + ", " + message);
		}

		@Override
		public void onSubscribe(String channel, int subscribedChannels) {
			System.out.println("onSubscribe: " + channel + ", " + subscribedChannels);
		}

		@Override
		public void onUnsubscribe(String channel, int subscribedChannels) {
			System.out.println("onUnsubscribe: " + channel + ", " + subscribedChannels);
		}

		@Override
		public void onPUnsubscribe(String pattern, int subscribedChannels) {
			System.out.println("onPUnsubscribe: " + pattern + ", " + subscribedChannels);
		}

		@Override
		public void onPSubscribe(String pattern, int subscribedChannels) {
			System.out.println("onPSubscribe: " + pattern + ", " + subscribedChannels);
		}

	}

	private JedisPool jedisPool;

	public void configure() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMinIdle(1);
		jedisPoolConfig.setMaxIdle(5);
		jedisPoolConfig.setMaxTotal(10);
		jedisPoolConfig.setMaxWaitMillis(-1);
		jedisPool = new JedisPool(jedisPoolConfig, "10.200.28.42", 6379, 60000, "123456");
	}

	private static void config(Jedis jedis) {
		String parameter = "notify-keyspace-events";
		List<String> notify = jedis.configGet(parameter);
		if (notify.get(1).equals("")) {
			jedis.configSet(parameter, "Ex"); // 过期事件
		}
	}

	public Jedis getConnection() {
		Jedis jedis = jedisPool.getResource();
		config(jedis);
		return jedis;
	}

	public static void sub() throws Exception {
		TestRedisExEvent tm = new TestRedisExEvent();
		tm.configure();
		Jedis jedis = tm.getConnection();
		jedis.subscribe(new MyPubSub(), "__keyevent@0__:expired");
		jedis.close();
		System.out.println("TestMain.sub()");
	}

	public static void pub() throws Exception {
		final String key = "key_a";
		TestRedisExEvent tm = new TestRedisExEvent();
		tm.configure();
		Jedis jedis = tm.getConnection();
		jedis.set(key, "222");
		for (int i = 0; i < 3; i++) {
			System.out.println("续命");
			jedis.expire(key, 3);
			ThreadUtils.randomSleep(3000);
		}
		// jedis.del("key_a");
		System.out.println("删除key");
		System.in.read();

		jedis.close();
		System.out.println("TestMain.pub()");

	}

	public static void main(String[] args) throws Exception {
		pub();

	}

}
