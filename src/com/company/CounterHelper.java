package com.company;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterHelper {
	public final AtomicInteger checkoutCount;
	public final AtomicInteger deniedCount;
	public final AtomicInteger checkoutCountTotal;
	public final AtomicInteger deniedCountTotal;
	public final AtomicInteger weekendCheckOuts;
	public final AtomicInteger weekendDenies;
	public final AtomicInteger weekendDaysOnLog;
	public final AtomicInteger weekDaysOnLog;
	public final AtomicInteger weekDaysLicenseChecked;
	public final AtomicInteger weekendDaysLicenseChecked;

	CounterHelper() {
		checkoutCount = new AtomicInteger(0);
		deniedCount = new AtomicInteger(0);
		checkoutCountTotal = new AtomicInteger(0);
		deniedCountTotal = new AtomicInteger(0);
		weekendCheckOuts = new AtomicInteger(0);
		weekendDenies = new AtomicInteger(0);
		weekendDaysOnLog = new AtomicInteger(0);
		weekDaysOnLog = new AtomicInteger(0);
		weekDaysLicenseChecked = new AtomicInteger(0);
		weekendDaysLicenseChecked = new AtomicInteger(0);
	}
}
