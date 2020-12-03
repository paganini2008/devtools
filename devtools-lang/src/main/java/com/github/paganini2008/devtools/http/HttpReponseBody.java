package com.github.paganini2008.devtools.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;

/**
 * HttpReponseBody
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface HttpReponseBody {

	void refresh(HttpURLConnection connection) throws IOException;

	void fillData(ByteBuffer bb);

}
