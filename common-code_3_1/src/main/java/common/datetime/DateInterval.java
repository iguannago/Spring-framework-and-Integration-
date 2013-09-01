package common.datetime;

/**
 * Represents an interval i.e. start and end date
 * 
 * @author djnorth
 */
public class DateInterval {

	private final SimpleDate start;

	private final SimpleDate end;

	/**
	 * Constructor taking values to be used
	 * @param start
	 * @param end
	 */
	public DateInterval(SimpleDate start, SimpleDate end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Get our start date
	 * 
	 * @return start date
	 */
	public SimpleDate getStart() {
		return start;
	}


	/**
	 * Get our end date
	 * 
	 * @return end date
	 */
	public SimpleDate getEnd() {
		return end;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DateInterval)) {
			return false;
		}
		DateInterval other = (DateInterval) obj;
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DateInterval [start=");
		builder.append(start);
		builder.append(", end=");
		builder.append(end);
		builder.append("]");
		return builder.toString();
	}
	
}
