package utils;

import play.db.ebean.Model;

public class MeetUpHosting {
	public String description;
	public Long interest;
	public Long location;

	public MeetUpHosting() {
	}

	public MeetUpHosting(String description, Long interest,
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