package com.company;

import java.util.Date;
import java.util.Objects;

public class LicenseRegistrar {
	private final Date checkOutTime;
	private final String user;
	private final String host;

	public LicenseRegistrar(Date checkInTime, String user, String host) {
		this.checkOutTime = checkInTime;
		this.user = user;
		this.host = host;
	}

	public Date getCheckOutTime() {
		return checkOutTime;
	}

	public String getUser() {
		return user;
	}

	public String getHost() {
		return host;
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 != null && o2 instanceof LicenseRegistrar) {
			LicenseRegistrar object2 = (LicenseRegistrar) o2;
			return this.user.equals(object2.user) && this.host.equals(object2.host);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}
}
