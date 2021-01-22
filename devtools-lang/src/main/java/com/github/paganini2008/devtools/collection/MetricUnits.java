package com.github.paganini2008.devtools.collection;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.paganini2008.devtools.beans.ToStringBuilder;
import com.github.paganini2008.devtools.primitives.Doubles;

/**
 * 
 * MetricUnits
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class MetricUnits {

	public static MetricUnit valueOf(long value) {
		return new LongMetricUnit(value);
	}

	public static MetricUnit valueOf(double value) {
		return new DoubleMetricUnit(value);
	}

	public static MetricUnit valueOf(BigDecimal value) {
		return new BigDecimalMetricUnit(value);
	}

	public static class DoubleMetricUnit implements MetricUnit {

		DoubleMetricUnit(double value) {
			this.highestValue = value;
			this.lowestValue = value;
			this.totalValue = value;
			this.count = 1;
			this.timestamp = System.currentTimeMillis();
		}

		private double highestValue;
		private double lowestValue;
		private double totalValue;
		private int count;
		private long timestamp;

		@Override
		public Double getHighestValue() {
			return highestValue;
		}

		@Override
		public Double getLowestValue() {
			return lowestValue;
		}

		@Override
		public Double getTotalValue() {
			return totalValue;
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Double getMiddleValue(int scale) {
			return count > 0 ? scale > 0 ? Doubles.toFixed(totalValue / count, scale) : totalValue / count : 0;
		}

		public long getTimestamp() {
			return timestamp;
		}

		@Override
		public void update(MetricUnit anotherUnit) {
			this.highestValue = Double.max(this.highestValue, anotherUnit.getHighestValue().doubleValue());
			this.lowestValue = Double.min(this.lowestValue, anotherUnit.getLowestValue().doubleValue());
			this.totalValue += anotherUnit.getTotalValue().doubleValue();
			this.count += anotherUnit.getCount();
			this.timestamp = anotherUnit.getTimestamp();
		}

		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

	}

	public static class LongMetricUnit implements MetricUnit {

		LongMetricUnit(long value) {
			this.highestValue = value;
			this.lowestValue = value;
			this.totalValue = value;
			this.count = 1;
			this.timestamp = System.currentTimeMillis();
		}

		private long highestValue;
		private long lowestValue;
		private long totalValue;
		private int count;
		private long timestamp;

		@Override
		public Long getHighestValue() {
			return highestValue;
		}

		@Override
		public Long getLowestValue() {
			return lowestValue;
		}

		@Override
		public Long getTotalValue() {
			return totalValue;
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Double getMiddleValue(int scale) {
			return count > 0 ? Doubles.toFixed((double) totalValue / count, scale) : 0;
		}

		public long getTimestamp() {
			return timestamp;
		}

		@Override
		public void update(MetricUnit anotherUnit) {
			this.highestValue = Long.max(this.highestValue, anotherUnit.getHighestValue().longValue());
			this.lowestValue = Long.min(this.lowestValue, anotherUnit.getLowestValue().longValue());
			this.totalValue += anotherUnit.getTotalValue().longValue();
			this.count += anotherUnit.getCount();
			this.timestamp = anotherUnit.getTimestamp();
		}

		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

	}

	public static class BigDecimalMetricUnit implements MetricUnit {

		BigDecimalMetricUnit(BigDecimal value) {
			this.highestValue = value;
			this.lowestValue = value;
			this.totalValue = value;
			this.count = 1;
			this.timestamp = System.currentTimeMillis();
		}

		private BigDecimal highestValue;
		private BigDecimal lowestValue;
		private BigDecimal totalValue;
		private int count;
		private long timestamp;

		@Override
		public BigDecimal getHighestValue() {
			return highestValue;
		}

		@Override
		public BigDecimal getLowestValue() {
			return lowestValue;
		}

		@Override
		public BigDecimal getTotalValue() {
			return totalValue;
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public BigDecimal getMiddleValue(int scale) {
			return count > 0 ? totalValue.divide(BigDecimal.valueOf(count), scale, RoundingMode.HALF_UP) : BigDecimal.ZERO;
		}

		@Override
		public long getTimestamp() {
			return timestamp;
		}

		@Override
		public void update(MetricUnit anotherUnit) {
			this.highestValue = this.highestValue.max((BigDecimal) anotherUnit.getHighestValue());
			this.lowestValue = this.lowestValue.min((BigDecimal) anotherUnit.getLowestValue());
			this.totalValue = this.totalValue.add((BigDecimal) anotherUnit.getTotalValue());
			this.count += anotherUnit.getCount();
			this.timestamp = anotherUnit.getTimestamp();
		}

		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

	}

}
