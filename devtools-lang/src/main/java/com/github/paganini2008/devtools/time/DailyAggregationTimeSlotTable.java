package com.github.paganini2008.devtools.time;

import java.time.Instant;

import com.github.paganini2008.devtools.RandomDateUtils;
import com.github.paganini2008.devtools.collection.ConcurrentSortedBoundedMap;

/**
 * 
 * DailyAggregationTimeSlotTable
 *
 * @author Fred Feng
 * @since 2.0.4
 */
public class DailyAggregationTimeSlotTable<V> extends AggregationTimeSlotMap<V> {

	private static final long serialVersionUID = 1020741898314951406L;

	public DailyAggregationTimeSlotTable(int span, TimeSlot timeSlot) {
		this(span, timeSlot, 1);
	}

	public DailyAggregationTimeSlotTable(int span, TimeSlot timeSlot, int days) {
		super(new ConcurrentSortedBoundedMap<>(timeSlot.sizeOf(span, days)), span, timeSlot);
	}

	public static void main(String[] args) {
		DailyAggregationTimeSlotTable<Integer> tst = new DailyAggregationTimeSlotTable<Integer>(5, TimeSlot.MINUTE, 1);
		for (int i = 0; i < 100000; i++) {
			tst.merge(randomInstant(), 1, (o, n) -> o != null ? (Integer) o + 1 : 1);
		}
		tst.output().entrySet().forEach(e -> {
			System.out.println(e);
		});
	}

	private static Instant randomInstant() {
		return RandomDateUtils.randomDateTime(2021, 12).toInstant();
	}

}
