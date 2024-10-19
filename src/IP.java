public class IP implements Comparable<IP> {

	private String ip;
	private int[] intIPArray;
	private int occurrence;

	public IP(String ip) {
		this.ip = ip;
		this.occurrence = 1;
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
		if (this.occurrence == o.getOccurrence())
			return 0;
		else
			return this.occurrence > o.getOccurrence() ? -1 : 1;
		// return getIPOrderResult(o);
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
		return occurrence;
	}

	public void setOccurrence(int occurrence) {
		this.occurrence = occurrence;
	}

	public String toString() {
		return intIPArray[0] + "." + intIPArray[1] + "." + intIPArray[2] + "." + intIPArray[3] + " requested "
				+ occurrence + " times.";
	}
}
