package com.company;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterHelper {
	private final AtomicInteger checkoutCount;
	private final AtomicInteger deniedCount;
	private final AtomicInteger checkoutCountTotal;
	private final AtomicInteger deniedCountTotal;
	private final AtomicInteger weekendCheckOuts;
	private final AtomicInteger weekendDenies;
	private final AtomicInteger weekendDaysOnLog;
	private final AtomicInteger weekDaysOnLog;

	CounterHelper() {
		checkoutCount = new AtomicInteger(0);
		deniedCount = new AtomicInteger(0);
		checkoutCountTotal = new AtomicInteger(0);
		deniedCountTotal = new AtomicInteger(0);
		weekendCheckOuts = new AtomicInteger(0);
		weekendDenies = new AtomicInteger(0);
		weekendDaysOnLog = new AtomicInteger(0);
		weekDaysOnLog = new AtomicInteger(0);
	}

	void incrementCheckoutCount() {
		checkoutCount.addAndGet(1);
	}

	int getCheckoutCount() {
		return checkoutCount.get();
	}

	void resetCheckoutCount() {
		checkoutCount.set(0);
	}

	void incrementDeniedCount() {
		deniedCount.addAndGet(1);
	}

	int getDeniedCount() {
		return deniedCount.get();
	}

	void resetDeniedCount() {
		deniedCount.set(0);
	}

	void incrementCheckoutCountTotal() {
		checkoutCountTotal.addAndGet(1);
	}

	int getCheckoutCountTotal() {
		return checkoutCountTotal.get();
	}

	void incrementDeniedCountTotal() {
		deniedCountTotal.addAndGet(1);
	}

	int getDeniedCountTotal() {
		return deniedCountTotal.get();
	}

	void incrementWeekendCheckOuts() {
		weekendCheckOuts.addAndGet(1);
	}

	int getWeekendCheckOuts() {
		return weekendCheckOuts.get();
	}

	void incrementWeekendDenies() {
		weekendDenies.addAndGet(1);
	}

	int getWeekendDenies() {
		return weekendDenies.get();
	}

	void incrementWeekendDaysOnLog() {
		weekendDaysOnLog.addAndGet(1);
	}

	int getWeekendDaysOnLog() {
		return weekendDaysOnLog.get();
	}

	void incrementWeekDaysOnLog() {
		weekDaysOnLog.addAndGet(1);
	}

	int getWeekDaysOnLog() {
		return weekDaysOnLog.get();
	}
}
