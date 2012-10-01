package utils;

import play.db.ebean.Model;

public class MeetUpPromotion {
	public String description;
	public Long interest;
	public Long location;

	public MeetUpPromotion() {
	}

	public MeetUpPromotion(String description, Long interest,
			Long location) {
		this.description = description;
		this.interest = interest;
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public Long getInterest() {
		return interest;
	}

	public Long getLocation() {
		return location;
	}
}