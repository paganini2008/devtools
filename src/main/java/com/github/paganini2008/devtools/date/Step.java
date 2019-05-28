package com.github.paganini2008.devtools.date;

public enum Step {

	YEARS {
		void touch(DateTime dt, int amount, boolean asc) {
			dt.addYears(asc ? amount : -amount);
		}

	},
	MONTHS {
		void touch(DateTime dt, int amount, boolean asc) {
			dt.addMonths(asc ? amount : -amount);
		}

	},
	DAYS {
		void touch(DateTime dt, int amount, boolean asc) {
			dt.addDays(asc ? amount : -amount);
		}

	},
	HOURS {
		void touch(DateTime dt, int amount, boolean asc) {
			dt.addHours(asc ? amount : -amount);
		}

	},
	MINUTES {
		void touch(DateTime dt, int amount, boolean asc) {
			dt.addMinutes(asc ? amount : -amount);
		}

	},
	SECONDS {
		void touch(DateTime dt, int amount, boolean asc) {
			dt.addSeconds(asc ? amount : -amount);
		}

	};

	abstract void touch(DateTime dt, int amount, boolean asc);

}
