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

import java.util.Locale;

/**
 *
 * Package private formatting helpers for durations, sizes, rates and progress lines.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
abstract class ProgressFormats {

	private static final String[] SIZE_UNITS = { "KB", "MB", "GB", "TB", "PB", "EB" };

	/**
	 * Formats milliseconds as HH:mm:ss (hours are not wrapped); a negative value gives "--:--:--".
	 */
	static String formatDuration(long millis) {
		if (millis < 0) {
			return "--:--:--";
		}
		long seconds = millis / 1000;
		return String.format(Locale.ROOT, "%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60);
	}

	/**
	 * Returns true if the unit means bytes ("B", "byte" or "bytes", ignoring case).
	 */
	static boolean isByteUnit(String unit) {
		return "B".equalsIgnoreCase(unit) || "byte".equalsIgnoreCase(unit) || "bytes".equalsIgnoreCase(unit);
	}

	/**
	 * Formats a byte size, e.g. 512 -> "512 B", 1536 -> "1.5 KB".
	 */
	static String formatSize(double bytes) {
		if (bytes < 1024) {
			return String.format(Locale.ROOT, "%d B", (long) bytes);
		}
		double value = bytes;
		int index = -1;
		while (value >= 1024 && index < SIZE_UNITS.length - 1) {
			value /= 1024;
			index++;
		}
		return String.format(Locale.ROOT, "%.1f %s", value, SIZE_UNITS[index]);
	}

	static String formatRate(double rate, String unit) {
		if (isByteUnit(unit)) {
			return formatSize(rate) + "/s";
		}
		return String.format(Locale.ROOT, "%.1f", rate) + (unit.isEmpty() ? "/s" : " " + unit + "/s");
	}

	static String formatCount(ProgressSnapshot snapshot) {
		String unit = snapshot.getUnit();
		if (isByteUnit(unit)) {
			String str = formatSize(snapshot.getCompleted());
			return snapshot.isTotalKnown() ? str + "/" + formatSize(snapshot.getTotal()) : str;
		}
		String str = snapshot.isTotalKnown() ? snapshot.getCompleted() + "/" + snapshot.getTotal()
				: String.valueOf(snapshot.getCompleted());
		return unit.isEmpty() ? str : str + " " + unit;
	}

	static String formatTime(ProgressSnapshot snapshot) {
		switch (snapshot.getState()) {
		case COMPLETED:
			return "Done in " + formatDuration(snapshot.getElapsedMillis());
		case FAILED:
			String str = "Failed after " + formatDuration(snapshot.getElapsedMillis());
			Throwable e = snapshot.getFailure();
			if (e != null) {
				str += " (" + e.getClass().getSimpleName() + (e.getMessage() != null ? ": " + e.getMessage() : "") + ")";
			}
			return str;
		default:
			return snapshot.isTotalKnown() ? "ETA " + formatDuration(snapshot.getEtaMillis())
					: "Elapsed " + formatDuration(snapshot.getElapsedMillis());
		}
	}

	/**
	 * Draws the bar body (without brackets) of the given width.
	 */
	static String formatBar(double percent, int width, char fill, char head, char blank) {
		int filled = (int) (width * percent / 100d);
		StringBuilder str = new StringBuilder(width);
		if (filled >= width) {
			for (int i = 0; i < width; i++) {
				str.append(fill);
			}
			return str.toString();
		}
		for (int i = 0; i < filled; i++) {
			str.append(fill);
		}
		str.append(head);
		for (int i = filled + 1; i < width; i++) {
			str.append(blank);
		}
		return str.toString();
	}

	/**
	 * Formats a progress line, for example
	 * {@code Dump users [=====>    ]  56% 560/1000 rows  123.4 rows/s  ETA 00:00:03}.
	 */
	static String formatLine(ProgressSnapshot snapshot, boolean bar, char fill, char head, char blank, boolean showCount,
			boolean showRate) {
		StringBuilder line = new StringBuilder(128);
		line.append(snapshot.getTaskName());
		if (snapshot.isTotalKnown()) {
			if (bar && snapshot.getWidth() > 0) {
				append(line, " ", "[" + formatBar(snapshot.getPercent(), snapshot.getWidth(), fill, head, blank) + "]");
			}
			append(line, " ", String.format(Locale.ROOT, "%3d%%", (int) snapshot.getPercent()));
			if (showCount) {
				append(line, " ", formatCount(snapshot));
			}
		} else if (showCount) {
			append(line, " ", formatCount(snapshot));
		}
		if (showRate) {
			append(line, "  ", formatRate(snapshot.getRatePerSecond(), snapshot.getUnit()));
		}
		append(line, "  ", formatTime(snapshot));
		String extra = snapshot.getExtraMessage();
		if (extra != null && extra.trim().length() > 0) {
			append(line, "  ", extra);
		}
		return line.toString();
	}

	private static void append(StringBuilder line, String separator, String token) {
		if (line.length() > 0) {
			line.append(separator);
		}
		line.append(token);
	}

}
