package com.github.paganini2008.springworld.socketbird;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.transport.NioClient;
import com.github.paganini2008.transport.Partitioner;
import com.github.paganini2008.transport.Tuple;

/**
 * 
 * MessageController
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@RequestMapping("/socketbird")
@RestController
public class MessageController {

	@Autowired
	private NioClient nioClient;

	@Autowired
	private Partitioner partitioner;

	@GetMapping("/send")
	public Map<String, Object> sendMsg(@RequestParam("q") String content) {
		Tuple data = Tuple.by(content);
		nioClient.send(data, partitioner);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("q", content);
		result.put("success", true);
		return result;
	}

	@GetMapping("/test")
	public Map<String, Object> test() {
		int N = 10000;

		ThreadUtils.runAsThread(() -> {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < N; i++) {

				int n = ThreadLocalRandom.current().nextInt(1, 20);

				for (int j = 0; j < n; j++) {
					str.append(UUID.randomUUID().toString());
				}
				Tuple data = Tuple.by(str.toString());
				nioClient.send(data, partitioner);
				str.delete(0, str.length() - 1);

			}
		});

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		return result;
	}

}
