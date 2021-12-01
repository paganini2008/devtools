package com.github.paganini2008.devtools.date;

import java.time.Instant;

import com.github.paganini2008.devtools.RandomDateUtils;
import com.github.paganini2008.devtools.collection.SortedBoundedMap;

/**
 * 
 * DailyTimeSlotTable
 *
 * @author Fred Feng
 * @since 2.0.4
 */
public class DailyTimeSlotTable extends TimeSlotTable {

	private static final long serialVersionUID = 1020741898314951406L;

	public DailyTimeSlotTable(int span, TimeSlot timeSlot) {
		super(new SortedBoundedMap<>(timeSlot.sizeOf(span, 1)), span, timeSlot);
	}

	public static void main(String[] args) {
		DailyTimeSlotTable tst = new DailyTimeSlotTable(5, TimeSlot.MINUTE);
		for (int i = 0; i < 100000; i++) {
			tst.merge(randomInstant(), 1, (o, n) -> o != null ? (Integer) o + 1 : 1);
		}
		tst.format().entrySet().forEach(e -> {
			System.out.println(e);
		});
	}

	private static Instant randomInstant() {
		return RandomDateUtils.randomDateTime(2021, 12).toInstant();
	}

}
