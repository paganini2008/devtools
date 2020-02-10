package com.github.paganini2008.springworld.transport.buffer;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.multithreads.AtomicUnsignedInteger;
import com.github.paganini2008.springworld.cluster.ClusterId;
import com.github.paganini2008.transport.Tuple;
import com.github.paganini2008.transport.serializer.Serializer;

import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * 
 * MemcachedBufferZone
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Slf4j
public class MemcachedBufferZone implements BufferZone {

	private static final int DEFAULT_STORE_EXPIRATION = 60;

	@Value("${spring.transport.memcached.address:localhost:11211}")
	private String address;

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${spring.transport.bufferzone.reused:true}")
	private boolean reused;

	@Autowired
	private ClusterId clusterId;

	private final AtomicUnsignedInteger setter = new AtomicUnsignedInteger(0);
	private final AtomicUnsignedInteger getter = new AtomicUnsignedInteger(0);

	private MemcachedClient client;

	@Autowired
	private Serializer serializer;

	@Override
	public void configure() throws Exception {
		client = new XMemcachedClientBuilder(AddrUtil.getAddresses(address)).build();
	}

	@Override
	public void destroy() {
		try {
			client.shutdown();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void set(String name, Tuple tuple) throws Exception {
		String key = getKey(name) + "-" + setter.getAndIncrement();
		byte[] data = serializer.serialize(tuple);
		client.set(key, DEFAULT_STORE_EXPIRATION, data);
	}

	@Override
	public Tuple get(String name) throws Exception {
		if (setter.get() > getter.get()) {
			String key = getKey(name) + "-" + getter.getAndIncrement();
			byte[] data;
			if ((data = (byte[]) client.getAndTouch(key, 3)) != null) {
				return serializer.deserialize(data);
			}
		}
		return null;
	}

	@Override
	public int size(String name) throws Exception {
		Map<String, String> slabs = client.stats(CollectionUtils.getFirst(client.getAvailableServers()));
		log.info(slabs.toString());
		return 0;
	}

	protected String getKey(String name) {
		return "transport:bufferzone:" + name + ":" + applicationName + (reused ? ":" + clusterId.get() : "");
	}

}
