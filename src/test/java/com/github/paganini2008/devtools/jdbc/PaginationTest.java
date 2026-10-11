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
package com.github.paganini2008.devtools.jdbc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * PaginationTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class PaginationTest {

	/**
	 * In-memory slice of integers 1..total
	 */
	static class IntSlice implements ResultSetSlice<Integer> {

		final int total;
		final AtomicInteger countCalls = new AtomicInteger();

		IntSlice(int total) {
			this.total = total;
		}

		@Override
		public int rowCount() {
			countCalls.incrementAndGet();
			return total;
		}

		@Override
		public List<Integer> list(int maxResults, int firstResult) {
			List<Integer> list = new ArrayList<Integer>();
			for (int i = firstResult + 1; i <= total && (maxResults < 0 || list.size() < maxResults); i++) {
				list.add(i);
			}
			return list;
		}
	}

	@Test
	public void pageRequest_navigation() {
		PageRequest request = PageRequest.of(10);
		assertEquals(1, request.getPageNumber());
		assertEquals(10, request.getPageSize());
		assertEquals(0, request.getOffset());
		PageRequest next = request.next();
		assertEquals(2, next.getPageNumber());
		assertEquals(10, next.getOffset());
		assertEquals(1, next.previous().getPageNumber());
		assertSame(request, request.previous());
		assertEquals(1, next.first().getPageNumber());
		assertEquals(5, request.set(5).getPageNumber());
		assertEquals(40, request.set(5).getOffset());
		assertEquals("Page: 2, Size: 10", next.toString());
		assertThrows(IllegalArgumentException.class, () -> PageRequest.of(0, 10));
		assertThrows(IllegalArgumentException.class, () -> PageRequest.of(1, 0));
		assertThrows(IllegalArgumentException.class, () -> request.set(0));
	}

	@Test
	public void pageResponse_basicProperties() {
		IntSlice slice = new IntSlice(25);
		PageResponse<Integer> page = slice.list(PageRequest.of(2, 10));
		assertEquals(25, page.getTotalRecords());
		assertEquals(3, page.getTotalPages());
		assertEquals(2, page.getPageNumber());
		assertEquals(10, page.getPageSize());
		assertEquals(10, page.getOffset());
		assertFalse(page.isEmpty());
		assertFalse(page.isFirstPage());
		assertFalse(page.isLastPage());
		assertTrue(page.hasNextPage());
		assertTrue(page.hasPreviousPage());
		assertEquals(Arrays.asList(11, 12, 13, 14, 15, 16, 17, 18, 19, 20), page.getContent());
		assertEquals(Arrays.asList(21, 22, 23, 24, 25), page.nextPage().getContent());
		assertEquals(1, page.previousPage().getPageNumber());
		assertEquals(1, page.firstPage().getPageNumber());
		assertEquals(3, page.lastPage().getPageNumber());
		assertEquals(3, page.setPage(3).getPageNumber());
		PageResponse<Integer> last = page.lastPage();
		assertSame(last, last.lastPage());
		assertSame(last, last.nextPage());
		PageResponse<Integer> first = page.firstPage();
		assertSame(first, first.firstPage());
		assertSame(first, first.previousPage());
	}

	@Test
	public void pageResponse_iteratesAllPagesThenStops() {
		IntSlice slice = new IntSlice(25);
		List<Integer> all = new ArrayList<Integer>();
		List<Integer> pageNumbers = new ArrayList<Integer>();
		for (PageResponse<Integer> page : slice.list(PageRequest.of(1, 10))) {
			pageNumbers.add(page.getPageNumber());
			all.addAll(page.getContent());
		}
		assertEquals(Arrays.asList(1, 2, 3), pageNumbers);
		assertEquals(25, all.size());
		Iterator<PageResponse<Integer>> iterator = slice.list(PageRequest.of(1, 30)).iterator();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertFalse(iterator.hasNext());
		assertThrows(NoSuchElementException.class, () -> iterator.next());
	}

	@Test
	public void pageResponse_emptyResult() {
		IntSlice slice = new IntSlice(0);
		PageResponse<Integer> page = slice.list(PageRequest.of(1, 10));
		assertTrue(page.isEmpty());
		assertEquals(0, page.getTotalPages());
		assertFalse(page.hasNextPage());
		assertFalse(page.hasPreviousPage());
		assertTrue(page.getContent().isEmpty());
		int pages = 0;
		for (PageResponse<Integer> p : page) {
			pages++;
			assertTrue(p.getContent().isEmpty());
		}
		assertEquals(1, pages);
		assertSame(page, page.lastPage(), "lastPage of an empty result must not fail");
		assertSame(page, page.firstPage());
		assertSame(page, page.nextPage());
	}

	@Test
	public void pageResponse_customCountableIsKeptWhenNavigating() {
		IntSlice slice = new IntSlice(100);
		PageResponse<Integer> page = slice.list(PageRequest.of(1, 10), 25);
		assertEquals(25, page.getTotalRecords());
		assertEquals(3, page.getTotalPages());
		PageResponse<Integer> last = page.lastPage();
		assertEquals(3, last.getPageNumber());
		assertEquals(25, last.getTotalRecords());
		assertEquals(25, page.nextPage().getTotalRecords());
		assertEquals(25, page.setPage(2).previousPage().getTotalRecords());
		assertEquals(25, last.firstPage().getTotalRecords());
		assertEquals(0, slice.countCalls.get(), "the slice must not be recounted");
		PageResponse<Integer> counted = slice.list(PageRequest.of(1, 10), () -> 5);
		assertEquals(1, counted.getTotalPages());
	}

	@Test
	public void listable_defaults() {
		IntSlice slice = new IntSlice(3);
		assertEquals(Arrays.asList(1, 2, 3), slice.list());
		assertEquals(Arrays.asList(1, 2), slice.list(2));
		assertEquals(Integer.valueOf(1), slice.first());
		assertEquals(null, new IntSlice(0).first());
	}

	@Test
	public void pageableResultSetSlice_remembersPageNumber() {
		PageableResultSetSlice<Integer> slice = new PageableResultSetSlice<Integer>() {

			@Override
			public int rowCount() {
				return 30;
			}

			@Override
			public List<Integer> list(int maxResults, int firstResult) {
				return Collections.singletonList(firstResult);
			}
		};
		assertEquals(0, slice.getPageNumber());
		PageResponse<Integer> page = slice.list(PageRequest.of(3, 10));
		assertEquals(3, slice.getPageNumber());
		assertEquals(Collections.singletonList(20), page.getContent());
	}

	@Test
	public void defaultPageableSql_buildsCountAndLimitClauses() {
		DefaultPageableSql sql = new DefaultPageableSql("select * from t");
		assertEquals("select count(1) as rowCount from (select * from t) as this", sql.countableSql());
		assertEquals("select * from t limit 10 offset 20", sql.pageableSql(10, 20));
		assertEquals("select * from t limit 10 offset 0", sql.pageableSql(10, 0));
		assertEquals("select * from t limit 10", sql.pageableSql(10, -1));
		assertEquals("select * from t", sql.pageableSql(0, 5));
		assertEquals("select * from t", sql.pageableSql(-1, -1));
		assertEquals("select * from t", sql.getSql());
	}

	@Test
	public void pageBean_middlePage() {
		PageBean<String> bean = new PageBean<String>();
		bean.setRows(95);
		bean.setSize(10);
		bean.setPage(5);
		bean.refresh();
		assertEquals(10, bean.getTotalPages());
		assertEquals(5, bean.getPage());
		assertEquals(40, bean.getStart());
		assertEquals(50, bean.getEnd());
		assertEquals(Arrays.asList(3, 4, 5, 6, 7), bean.getPageNos());
		assertEquals(1, bean.getFirstPage());
		assertEquals(4, bean.getPreviousPage());
		assertEquals(6, bean.getNextPage());
		assertEquals(10, bean.getEndPage());
		assertTrue(bean.hasNextPage());
		assertTrue(bean.hasPreviousPage());
		int[][] show = bean.getShowArray();
		assertArrayEquals(new int[] { 95, -1 }, show[0]);
		assertArrayEquals(new int[] { 1, 1 }, show[1]);
		assertArrayEquals(new int[] { 4, 2 }, show[2]);
		assertArrayEquals(new int[] { 5, 0 }, show[5]);
		assertArrayEquals(new int[] { 10, 4 }, show[show.length - 2]);
		assertArrayEquals(new int[] { 6, 5 }, show[show.length - 1]);
	}

	@Test
	public void pageBean_firstPageIncludesCurrentPageNumber() {
		PageBean<String> bean = new PageBean<String>();
		bean.setRows(95);
		bean.setSize(10);
		bean.refresh();
		assertEquals(1, bean.getPage());
		assertEquals(Arrays.asList(1, 2, 3, 4, 5), bean.getPageNos());
		assertEquals(0, bean.getStart());
		assertEquals(10, bean.getEnd());
		assertEquals(0, bean.getFirstPage());
		assertEquals(0, bean.getPreviousPage());
		assertEquals(2, bean.getNextPage());
		assertEquals(10, bean.getEndPage());
		assertFalse(bean.hasPreviousPage());
		bean.refresh();
		assertEquals(Arrays.asList(1, 2, 3, 4, 5), bean.getPageNos(), "refresh must not accumulate page numbers");
	}

	@Test
	public void pageBean_lastPageAndClamping() {
		PageBean<String> bean = new PageBean<String>();
		bean.setRows(25);
		bean.setSize(10);
		bean.setPage(3);
		bean.refresh();
		assertEquals(20, bean.getStart());
		assertEquals(25, bean.getEnd());
		assertEquals(Arrays.asList(1, 2, 3), bean.getPageNos());
		assertEquals(2, bean.getPreviousPage());
		assertEquals(0, bean.getNextPage());
		assertFalse(bean.hasNextPage());

		PageBean<String> beyond = new PageBean<String>();
		beyond.setRows(25);
		beyond.setSize(10);
		beyond.setPage(9);
		beyond.refresh();
		assertEquals(3, beyond.getPage());
		assertEquals(20, beyond.getStart());
		assertEquals(25, beyond.getEnd());

		PageBean<String> negative = new PageBean<String>();
		negative.setRows(25);
		negative.setPage(-3);
		negative.refresh();
		assertEquals(1, negative.getPage());
		assertEquals(0, negative.getStart());
		assertEquals(20, negative.getEnd());

		PageBean<String> last = new PageBean<String>();
		last.setRows(95);
		last.setSize(10);
		last.setPage(10);
		last.refresh();
		assertEquals(Arrays.asList(6, 7, 8, 9, 10), last.getPageNos());
		assertEquals(1, last.getFirstPage());
		assertEquals(9, last.getPreviousPage());
	}

	@Test
	public void pageBean_emptyAndZeroSize() {
		PageBean<String> empty = new PageBean<String>();
		empty.refresh();
		assertEquals(1, empty.getPage());
		assertEquals(0, empty.getTotalPages());
		assertEquals(0, empty.getStart());
		assertEquals(0, empty.getEnd());
		assertEquals(0, empty.getShowArray().length);
		assertTrue(empty.getPageNos().isEmpty());

		PageBean<String> zero = new PageBean<String>();
		zero.setRows(10);
		zero.setSize(0);
		zero.refresh();
		assertEquals(0, zero.getTotalPages());
		assertEquals(0, zero.getEnd());
	}

	@Test
	public void pageBean_accessorsCloneAndWrap() {
		PageBean<String> bean = new PageBean<String>();
		bean.setRows(30);
		bean.setShowPages(3);
		assertEquals(3, bean.getShowPages());
		bean.refresh();
		bean.setResults(Arrays.asList("a", "b"));
		assertEquals(2, bean.getShowSize());
		assertEquals(20, bean.getSize());
		assertEquals(30, bean.getRows());
		@SuppressWarnings("unchecked")
		PageBean<String> copy = (PageBean<String>) bean.clone();
		assertEquals(0, copy.getShowSize());
		assertEquals(bean.getPageNos(), copy.getPageNos());
		assertNotSame(bean.getPageNos(), copy.getPageNos());
		assertNotSame(bean.getShowArray(), copy.getShowArray());
		copy.getPageNos().add(99);
		assertFalse(bean.getPageNos().contains(99));
		assertEquals(0, new PageBean<String>().getShowSize());
		assertEquals(0, ((PageBean<?>) new PageBean<String>().clone()).getShowSize());

		bean.setTotalPages(7);
		bean.setFirstPage(1);
		bean.setPreviousPage(2);
		bean.setNextPage(3);
		bean.setEndPage(4);
		bean.setStart(5);
		bean.setEnd(6);
		bean.setPageNos(new ArrayList<Integer>());
		bean.setShowArray(new int[0][]);
		assertEquals(7, bean.getTotalPages());
		assertEquals(1, bean.getFirstPage());
		assertEquals(2, bean.getPreviousPage());
		assertEquals(3, bean.getNextPage());
		assertEquals(4, bean.getEndPage());
		assertEquals(5, bean.getStart());
		assertEquals(6, bean.getEnd());

		PageBean<Integer> wrapped = PageBean.wrap(new IntSlice(25).list(PageRequest.of(2, 10)));
		assertEquals(2, wrapped.getPage());
		assertEquals(25, wrapped.getRows());
		assertEquals(10, wrapped.getResults().size());
		assertEquals(Integer.valueOf(11), wrapped.getResults().get(0));
	}

	@Test
	public void pageBean_compareStartAndLimit() {
		assertTrue(PageBean.compareStartAndLimit(0, 10));
		assertFalse(PageBean.compareStartAndLimit(null, 10));
		assertFalse(PageBean.compareStartAndLimit(0, null));
		assertFalse(PageBean.compareStartAndLimit(-1, 10));
		assertFalse(PageBean.compareStartAndLimit(0, 0));
		assertFalse(PageBean.compareStartAndLimit(Integer.MAX_VALUE, 1));
	}
}
