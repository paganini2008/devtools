/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.jdbc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.collection.ListUtils;

/**
 * 
 * PageBean
 * <ul>
 * <li>-1: total records</li>
 * <li>0: current page</li>
 * <li>1: first page</li>
 * <li>2: previous page</li>
 * <li>3: page number</li>
 * <li>4: last page</li>
 * <li>5: next page</li>
 * </ul>
 * 
 * @author Fred Feng
 * 
 * @since 2.0.1
 */
public class PageBean<T> implements Serializable, Cloneable {

	private static final long serialVersionUID = 8934378634483462296L;
	private static final int PAGE_SIZE = 20;

	private int rows;
	private int size = PAGE_SIZE;
	private int page;

	private int totalPages;
	private int showPages;

	private int start;
	private int end;

	private int[][] showArray;
	private int firstPage;
	private int previousPage;
	private int nextPage;
	private int endPage;

	private List<Integer> pageNos = new ArrayList<Integer>();

	private List<T> results;

	public PageBean() {
		rows = 0;
		page = 0;
		totalPages = 0;
		showPages = 5;
	}

	public void refresh() {
		if (this.page == 0) {
			this.page = 1;
		}
		if (this.size == 0) {
			this.totalPages = 0;
		} else {
			this.totalPages = (rows + size - 1) / size;
		}
		this.start = (this.page - 1) * this.size;
		this.end = (this.page) * this.size;
		if (this.rows < this.size) {
			this.end = this.rows;
		}
		int first = 0;
		int previous = 0;
		int commonBegin = 0;
		int commonEnd = 0;
		int last = 0;
		int next = 0;

		if (this.page < 1) {
			this.page = 1;
		} else if (this.page > this.totalPages) {
			this.page = this.totalPages;
		}

		if (this.totalPages < this.showPages) {
			commonBegin = 1;
			commonEnd = this.totalPages;
		} else {
			commonBegin = ((this.page - 2) < 1) ? 1 : (this.page - 2);
			commonEnd = (commonBegin + this.showPages - 1) > this.totalPages ? this.totalPages : (commonBegin + this.showPages - 1);

			if ((commonEnd - commonBegin) < (this.showPages - 1)) {
				commonBegin = commonEnd - this.showPages + 1;
			}
		}

		if (this.totalPages > 1) {
			if (this.page == 1) {
				next = 1;
				if ((this.totalPages - commonEnd) > 0)
					last = 1;
			} else if (this.page == this.totalPages) {
				previous = 1;
				if ((commonBegin - 1) > 0)
					first = 1;
			} else {
				next = 1;
				previous = 1;
				if ((this.totalPages - commonEnd) > 0)
					last = 1;
				if ((commonBegin - 1) > 0)
					first = 1;
			}
		}

		int len = first + previous + (commonEnd - commonBegin + 1) + last + next;
		if (this.rows > 0) {
			len = len + 1;
		}
		showArray = new int[len][2];
		int arrayindex = 0;

		if (this.rows > 0) {
			showArray[arrayindex][0] = this.rows;
			showArray[arrayindex][1] = -1;
			arrayindex++;
		}

		if (first == 1) {
			showArray[arrayindex][0] = 1;
			showArray[arrayindex][1] = 1;
			arrayindex++;
		}
		if (previous == 1) {
			showArray[arrayindex][0] = this.page - 1;
			showArray[arrayindex][1] = 2;
			arrayindex++;
		}
		if (last == 1) {
			showArray[showArray.length - 2][0] = this.totalPages;
			showArray[showArray.length - 2][1] = 4;
		}
		if (next == 1) {
			showArray[showArray.length - 1][0] = this.page + 1;
			showArray[showArray.length - 1][1] = 5;
		}

		for (int i = commonBegin; i <= commonEnd; i++, arrayindex++) {
			showArray[arrayindex][0] = i;
			showArray[arrayindex][1] = 3;
			if (this.page == i) {
				showArray[arrayindex][1] = 0;
			}
		}

		setProperties();

	}

	private void setProperties() {
		for (int[] array : showArray) {
			int value = array[0];
			switch (array[1]) {
			case 1:
				firstPage = value;
				break;
			case 2:
				previousPage = value;
				break;
			case 3:
				pageNos.add(value);
				break;
			case 4:
				endPage = value;
				break;
			case 5:
				nextPage = value;
				break;
			}
		}
		if (!pageNos.isEmpty()) {
			int index = pageNos.indexOf(page - 1);
			if (index != -1) {
				pageNos.add(index + 1, page);
			}
		}
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getShowPages() {
		return showPages;
	}

	public void setShowPages(int showPages) {
		this.showPages = showPages;
	}

	public int[][] getShowArray() {
		return showArray;
	}

	public void setShowArray(int[][] showArray) {
		this.showArray = showArray;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public int getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(int previousPage) {
		this.previousPage = previousPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public List<Integer> getPageNos() {
		return pageNos;
	}

	public void setPageNos(List<Integer> pageNos) {
		this.pageNos = pageNos;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getShowSize() {
		return results != null ? results.size() : 0;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public static boolean compareStartAndLimit(Integer start, Integer limit) {
		if (start == null || limit == null) {
			return false;
		}
		return (start >= 0) && (limit > 0) && (start < start + limit);
	}

	public boolean hasNextPage() {
		return (page + 1) <= getTotalPages();
	}

	public boolean hasPreviousPage() {
		return (page - 1 >= 1);
	}

	@Override
	public Object clone() {
		try {
			PageBean<?> pageBean = (PageBean<?>) super.clone();
			pageBean.setResults(ListUtils.emptyList());
			return pageBean;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	public static <T> PageBean<T> wrap(PageResponse<T> pageResponse) {
		PageBean<T> pageBean = new PageBean<T>();
		pageBean.setPage(pageResponse.getPageNumber());
		pageBean.setSize(pageResponse.getPageSize());
		pageBean.setRows(pageResponse.getTotalRecords());
		pageBean.refresh();
		pageBean.setResults(pageResponse.getContent());
		return pageBean;
	}
}
