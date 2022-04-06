package com.company.helpers;

import java.time.Duration;

public class UserSpecificPrettifyHolder {
	private Duration totalHours;
	private final String user;
	private final String date;

	public UserSpecificPrettifyHolder(Duration totalHours, String user, String date) {
		this.totalHours = totalHours;
		this.user = user.trim();
		this.date = date.trim();

	}

	public String getTotalHours() {
		return formatDurationJava8Plus(totalHours);
	}

	public String getUser() {
		return user;
	}

	public void addHours(Duration hoursToAdd) {
		totalHours = totalHours.plus(hoursToAdd);
	}

	public String getDate() {
		return date;
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 instanceof UserSpecificPrettifyHolder) {
			UserSpecificPrettifyHolder object2 = (UserSpecificPrettifyHolder) o2;
			return this.user.equals(object2.user);
			// TODO: if customer asks for specific features
			// return this.user.equals(object2.user) && this.host.equals(object2.host) && this.feature.equals(object2.feature);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.user.hashCode();
	}

	public static Duration parseStrDuration(String strDuration) {
		String[] arr = strDuration.split(":");
		String strIsoDuration = "PT" + arr[0] + "H" + arr[1] + "M" + arr[2] + "S";
		return Duration.parse(strIsoDuration);
	}

	private static String formatDurationJava8Plus(Duration duration) {
		return String.format("%d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.getSeconds() %60);

	}
}
