/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * TableView
 * 
 * @author Fred Feng
 *
 * @since 1.0
 */
public class TableView {

	private static final String NEWLINE = System.getProperty("line.separator");
	private final String[][] array;

	public TableView(int rowNumber, int columnNumber) {
		this(rowNumber, columnNumber, 1);
	}

	public TableView(int rowNumber, int columnNumber, int width) {
		this.array = ArrayUtils.newArray(String.class, rowNumber, columnNumber, StringUtils.repeat(' ', width));
	}

	public TableView setValues(int rowIndex, String[] values) {
		for (int i = 0; i < values.length; i++) {
			setValue(rowIndex, i, values[i]);
		}
		return this;
	}

	public TableView setValue(int rowIndex, int columnIndex, String value) {
		int width = array[rowIndex][columnIndex].length();
		array[rowIndex][columnIndex] = StringUtils.textMiddle(value, width);
		return this;
	}

	public TableView setValueOnLeft(int rowIndex, int columnIndex, String value, int padding) {
		int width = array[rowIndex][columnIndex].length();
		array[rowIndex][columnIndex] = StringUtils.textLeft(value, padding, width);
		return this;
	}

	public TableView setValueOnRight(int rowIndex, int columnIndex, String value, int padding) {
		int width = array[rowIndex][columnIndex].length();
		array[rowIndex][columnIndex] = StringUtils.textRight(value, padding, width);
		return this;
	}

	public TableView setWidth(int columnIndex, int width) {
		for (int row = 0; row < array.length; row++) {
			array[row][columnIndex] = StringUtils.repeat(' ', width);
		}
		return this;
	}

	public String[] toStringArray(boolean rowBorder, boolean columnBorder) {
		String[][] copy = toArrayWithBorder(rowBorder, columnBorder);
		String[] data = new String[copy.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = ArrayUtils.join(copy[i], null, false);
		}
		return data;
	}

	public String[][] toArray() {
		return array.clone();
	}

	public String[][] toArrayWithBorder(boolean rowBorder, boolean columnBorder) {
		String[][] copy = array.clone();
		List<String[]> rowList = new ArrayList<String[]>();
		List<String> row = new ArrayList<String>();

		for (int rowIndex = 0, rows = copy.length; rowIndex < rows; rowIndex++) {
			for (int columnIndex = 0, l = copy[rowIndex].length; columnIndex < l; columnIndex++) {
				row.add(copy[rowIndex][columnIndex]);
				if (columnBorder && columnIndex != l - 1) {
					row.add("|");
				}
			}
			row.add(0, "|");
			row.add("|");
			String[] rowArray = row.toArray(new String[0]);
			rowList.add(rowArray);
			if (rowBorder && rowIndex != rows - 1) {
				String[] border = getBorder(rowArray);
				rowList.add(border);
			}
			row.clear();
		}
		if (rowList.size() > 0) {
			String[] border = getBorder(rowList.get(0));
			rowList.add(0, border);
			rowList.add(border.clone());
		}
		return rowList.toArray(new String[0][0]);
	}

	private String[] getBorder(String[] rowArray) {
		int length = rowArray.length;
		String[] border = ArrayUtils.newArray(String.class, length);
		for (int i = 1; i < length - 1; i++) {
			border[i] = StringUtils.repeat('-', rowArray[i].length());
		}
		border[0] = "+";
		border[length - 1] = "+";
		return border;
	}

	public String toString() {
		return toString(true, true);
	}

	public String toString(boolean rowBorder, boolean columnBorder) {
		String[] data = toStringArray(rowBorder, columnBorder);
		StringBuilder tableContent = new StringBuilder();
		for (int i = 0, l = data.length; i < l; i++) {
			tableContent.append(data[i]);
			if (i != l - 1) {
				tableContent.append(NEWLINE);
			}
		}
		return tableContent.toString();
	}

	public static void main(String[] args) {
		TableView table = new TableView(10, 8, 10);
		System.out.println(table.toString());
	}

}
