package com.parseSLF4J.parser;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;

public class IP implements Comparable<IP> {

	private String ip;
	private int[] intIPArray;
	private LinkedHashSet<OffsetDateTime> dateTimes;
	private OffsetDateTime firstDateTime;

	public IP(OffsetDateTime dateTime, String ip) {
		this.dateTimes = new LinkedHashSet<OffsetDateTime>();
		this.dateTimes.add(dateTime);
		this.firstDateTime = dateTime;

		this.ip = ip;
		setIPArray();
	}

	private void setIPArray() {
		intIPArray = new int[4];

		String[] strIPArray = this.ip.split("\\.");
		if (strIPArray.length != 4) {
			intIPArray = null;
		}

		for (int i = 0; i < strIPArray.length; i++) {
			try {
				intIPArray[i] = Integer.parseInt(strIPArray[i]);
			} catch (Exception e) {
				intIPArray = null;
			}
		}
	}

	public int[] getIPArray() {
		return intIPArray;
	}

	@Override
	public int compareTo(IP o) {
		return getIPOrderResult(o);
	}

	private int getIPOrderResult(IP o) {
		for (int i = 0; i < this.getIPArray().length; i++) {
			if (this.getIPArray()[i] < o.getIPArray()[i]) {
				return -1;
			} else if (this.getIPArray()[i] > o.getIPArray()[i]) {
				return 1;
			}
		}
		return 0;
	}

	public int getOccurrence() {
		return this.dateTimes.size();
	}

	public OffsetDateTime getFirstDateTime() {
		return this.firstDateTime;
	}

	public boolean addDateTime(OffsetDateTime dateTime) {
		if (this.firstDateTime.isAfter(dateTime)) {
			this.firstDateTime = dateTime;
		}
		return this.dateTimes.add(dateTime);
	}

	public LinkedHashSet<OffsetDateTime> getDateTimes() {
		return this.dateTimes;
	}

	public String getIpString() {
		return intIPArray[0] + "." + intIPArray[1] + "." + intIPArray[2] + "." + intIPArray[3];
	}

	public String toString() {
		return getIpString() + " requested " + this.dateTimes.size() + " times";
	}
}
