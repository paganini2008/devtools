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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * An input stream stepping a {@link ProgressBar} by the count of bytes read or skipped. The bar completes at end of
 * stream or on close, and fails if reading throws an {@code IOException}.
 *
 * <pre>
 * ProgressBar bar = ProgressBar.builder().taskName("Upload").total(file.length()).unit("B").build();
 * try (InputStream in = bar.wrap(new FileInputStream(file))) {
 * 	IOUtils.copy(in, out);
 * }
 * </pre>
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ProgressInputStream extends FilterInputStream {

	private final ProgressBar progressBar;

	/**
	 * @throws IllegalArgumentException if the stream or the bar is null
	 */
	public ProgressInputStream(InputStream in, ProgressBar progressBar) {
		super(in);
		if (in == null || progressBar == null) {
			throw new IllegalArgumentException("Input stream or progress bar is null");
		}
		this.progressBar = progressBar;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	public int read() throws IOException {
		int b;
		try {
			b = super.read();
		} catch (IOException e) {
			progressBar.fail(e);
			throw e;
		}
		if (b >= 0) {
			progressBar.step();
		} else {
			progressBar.complete();
		}
		return b;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int n;
		try {
			n = super.read(b, off, len);
		} catch (IOException e) {
			progressBar.fail(e);
			throw e;
		}
		if (n > 0) {
			progressBar.stepBy(n);
		} else if (n < 0) {
			progressBar.complete();
		}
		return n;
	}

	@Override
	public long skip(long n) throws IOException {
		long skipped;
		try {
			skipped = super.skip(n);
		} catch (IOException e) {
			progressBar.fail(e);
			throw e;
		}
		if (skipped > 0) {
			progressBar.stepBy(skipped);
		}
		return skipped;
	}

	/**
	 * Closes the underlying stream and completes the bar if not finished.
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
