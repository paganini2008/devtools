/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.progress;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * An output stream stepping a {@link ProgressBar} by the count of bytes written. The bar completes on close and fails
 * if writing throws an {@code IOException}.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ProgressOutputStream extends FilterOutputStream {

	private final ProgressBar progressBar;

	/**
	 * @throws IllegalArgumentException if the stream or the bar is null
	 */
	public ProgressOutputStream(OutputStream out, ProgressBar progressBar) {
		super(out);
		if (out == null || progressBar == null) {
			throw new IllegalArgumentException("Output stream or progress bar is null");
		}
		this.progressBar = progressBar;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	public void write(int b) throws IOException {
		try {
			out.write(b);
		} catch (IOException e) {
			progressBar.fail(e);
			throw e;
		}
		progressBar.step();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		try {
			out.write(b, off, len);
		} catch (IOException e) {
			progressBar.fail(e);
			throw e;
		}
		if (len > 0) {
			progressBar.stepBy(len);
		}
	}

	/**
	 * Flushes and closes the underlying stream and completes the bar if not finished.
	 */
	@Override
	public void close() throws IOException {
		try {
			super.close();
		} finally {
			progressBar.close();
		}
	}

}
