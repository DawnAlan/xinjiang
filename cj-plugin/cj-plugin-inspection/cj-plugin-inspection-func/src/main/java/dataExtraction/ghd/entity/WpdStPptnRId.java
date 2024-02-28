package dataExtraction.ghd.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

/**
 * WpdStPptnRId entity. @author Li Qi by MyEclipse Persistence Tools
 */
@Embeddable

public class WpdStPptnRId implements java.io.Serializable {

	// Fields

	private LocalDateTime tm;
	private String stcd;

	// Constructors

	/** default constructor */
	public WpdStPptnRId() {
	}

	/** full constructor */
	public WpdStPptnRId(LocalDateTime tm, String stcd) {
		this.tm = tm;
		this.stcd = stcd;
	}

	// Property accessors

	@Column(name = "TM", nullable = false, length = 19)

	public LocalDateTime getTm() {
		return this.tm;
	}

	public void setTm(LocalDateTime tm) {
		this.tm = tm;
	}

	@Column(name = "STCD", nullable = false, length = 12)

	public String getStcd() {
		return this.stcd;
	}

	public void setStcd(String stcd) {
		this.stcd = stcd;
	}

	/**
	 * toString
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("tm").append("='").append(getTm()).append("' ");
		buffer.append("stcd").append("='").append(getStcd()).append("' ");
		buffer.append("]");

		return buffer.toString();
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof WpdStPptnRId))
			return false;
		WpdStPptnRId castOther = (WpdStPptnRId) other;

		return ((this.getTm() == castOther.getTm())
				|| (this.getTm() != null && castOther.getTm() != null && this.getTm().equals(castOther.getTm())))
				&& ((this.getStcd() == castOther.getStcd()) || (this.getStcd() != null && castOther.getStcd() != null
						&& this.getStcd().equals(castOther.getStcd())));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + (getTm() == null ? 0 : this.getTm().hashCode());
		result = 37 * result + (getStcd() == null ? 0 : this.getStcd().hashCode());
		return result;
	}

}