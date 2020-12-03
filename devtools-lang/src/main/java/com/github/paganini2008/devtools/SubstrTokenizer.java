package com.github.paganini2008.devtools;

import java.util.Enumeration;

/**
 * The SubstrTokenizer can break a string into tokens by some words.
 * 
 * @author Jimmy Hoff
 * 
 * 
 * @version 1.0
 */
public class SubstrTokenizer implements Enumeration<String> {

	public SubstrTokenizer(CharSequence content, String[] delimeters, boolean returnDelims) {
		Assert.hasNoText(content, "Content must not be null or empty.");
		this.value = content.toString();
		this.maxPosition = content.length();
		this.delimeters = delimeters;
		this.returnDelims = returnDelims;
	}

	private final String value;
	private final int maxPosition;
	private final String[] delimeters;
	private final boolean returnDelims;
	private int startPosition = 0;
	private int endPosition = -1;
	private int offset;
	private String last;
	private String delim;
	private boolean empty;
	private boolean even;

	private int locate(int fromIndex) {
		int i;
		int j = -1;
		for (String delim : delimeters) {
			if ((i = value.indexOf(delim, fromIndex)) != -1) {
				if (j == -1 || i < j) {
					this.delim = delim;
					j = i;
				}
			}
		}
		offset = this.delim != null ? this.delim.length() : 0;
		if (j == -1) {
			j = maxPosition;
		}
		return j;
	}

	private boolean test() {
		endPosition = locate(startPosition);
		empty = startPosition == endPosition;
		if (empty && returnDelims) {
			even = true;
		} else {
			even = false;
		}
		return empty;
	}

	public boolean hasMoreElements() {
		return hasMoreTokens();
	}

	public boolean hasMoreTokens() {
		if (startPosition >= maxPosition) {
			if (returnDelims) {
				return even && offset > 0;
			} else {
				return false;
			}
		}
		if (returnDelims && even) {
			return true;
		}
		while (test() && !even) {
			startPosition = endPosition + offset;
		}
		return startPosition < maxPosition;
	}

	public String nextToken() {
		return nextElement();
	}

	public String nextElement() {
		if (returnDelims && even) {
			last = delim;
			delim = null;
			even = false;
			if (empty) {
				startPosition = endPosition + offset;
			}
		} else {
			last = value.substring(startPosition, endPosition);
			startPosition = endPosition + offset;
			even = true;
		}
		return last;
	}

	public String getLastElement() {
		return last;
	}

}
