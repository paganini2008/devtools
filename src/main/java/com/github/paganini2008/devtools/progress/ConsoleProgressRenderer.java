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

import java.io.PrintStream;

/**
 *
 * Renders a progress bar on a {@link PrintStream}. On an interactive terminal the line is refreshed in place with
 * {@code \r}; otherwise (for example when redirected to a file) a new line is printed only at start, at every crossed
 * percent step (10% by default) or time interval (unknown total), and at the end.
 *
 * <pre>
 * Dump users [=================&gt;                      ]  45% 450/1000 rows  90.0 rows/s  ETA 00:00:06
 * </pre>
 *
 * <p>
 * One instance keeps the state of one bar at a time; do not share an instance between bars running concurrently.
 * </p>
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ConsoleProgressRenderer implements ProgressRenderer {

	private final PrintStream out;
	private final boolean interactive;
	private volatile char fill = '=';
	private volatile char head = '>';
	private volatile char blank = ' ';
	private volatile boolean showCount = true;
	private volatile boolean showRate = true;
	private LineGate gate = new LineGate(10, 10000L);
	private int lastLength;

	/**
	 * Renders on {@code System.out}; interactive if {@code System.console()} is not null.
	 */
	public ConsoleProgressRenderer() {
		this(System.out);
	}

	/**
	 * Renders on the stream ({@code System.out} if null); interactive if {@code System.console()} is not null.
	 */
	public ConsoleProgressRenderer(PrintStream out) {
		this(out, System.console() != null);
	}

	/**
	 * Renders on the stream ({@code System.out} if null) in the given mode.
	 */
	public ConsoleProgressRenderer(PrintStream out, boolean interactive) {
		this.out = out != null ? out : System.out;
		this.interactive = interactive;
	}

	public boolean isInteractive() {
		return interactive;
	}

	/**
	 * Sets the characters of the filled part, the head and the empty part of the bar ('=', '&gt;' and ' ' by default).
	 */
	public ConsoleProgressRenderer chars(char fill, char head, char blank) {
		this.fill = fill;
		this.head = head;
		this.blank = blank;
		return this;
	}

	/**
	 * Sets when a line is printed in non-interactive mode: every {@code percentStep} percent (10 by default) when the
	 * total is known, every {@code intervalMillis} (10 seconds by default) otherwise.
	 *
	 * @throws IllegalArgumentException if the step is not in [1, 100] or the interval is negative
	 */
	public synchronized ConsoleProgressRenderer lineStep(int percentStep, long intervalMillis) {
		this.gate = new LineGate(percentStep, intervalMillis);
		return this;
	}

	/**
	 * Shows or hides the "completed/total unit" part (shown by default).
	 */
	public ConsoleProgressRenderer showCount(boolean showCount) {
		this.showCount = showCount;
		return this;
	}

	/**
	 * Shows or hides the rate part (shown by default).
	 */
	public ConsoleProgressRenderer showRate(boolean showRate) {
		this.showRate = showRate;
		return this;
	}

	@Override
	public synchronized void render(ProgressSnapshot snapshot) {
		String line = ProgressFormats.formatLine(snapshot, true, fill, head, blank, showCount, showRate);
		if (interactive) {
			StringBuilder str = new StringBuilder(line.length() + 8).append('\r').append(line);
			for (int i = line.length(); i < lastLength; i++) {
				str.append(' ');
			}
			lastLength = line.length();
			out.print(str);
			out.flush();
		} else if (gate.accept(snapshot)) {
			out.println(line);
			out.flush();
		}
	}

	@Override
	public synchronized void onFinish(ProgressSnapshot snapshot) {
		if (interactive) {
			out.println();
			out.flush();
		}
		lastLength = 0;
		gate.reset();
	}

}
