package utils;

import conf.MupiParams;
import play.data.validation.Constraints.Required;

public class Forms{
  
  
  public static class Community {
    public Long interest = new Long(0);
    public Long location = new Long(0);
    
    public Community(){}
    
    public Community(Long interest, Long location) {
      if(interest != null) this.interest = interest;
      else                 this.interest = new Long(0);
      if(location!= null)  this.location = location;
      else                 this.location = new Long(0);
    }
    public Long getInterest() {
      return interest;
    }
    public void setInterest(Long interest) {
      this.interest = interest;
    }
    public Long getLocation() {
      return location;
    }
    public void setLocation(Long location) {
      this.location = location;
    }    
  }
  
  public static class MeetUpHosting {
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
  
  public static class MeetUpPromotion {
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
  
  public static class PublicationBinder {
    @Required public String body;
    @Required public Long interest;
    @Required public Long location;
    @Required public Integer pub_typ;
    
    public PublicationBinder(){}
    
    public PublicationBinder(String body, Long interest, Long location, Integer pub_typ) {
      this.body = body;
      this.interest = interest;
      this.location = location;
      this.pub_typ = pub_typ;
    }
    
    public String getBody() {
      return body;
    }
    public Long getInterest() {
      return interest;
    }
    public Long getLocation() {
      return location;
    }
    public Integer getPub_typ() {
      return pub_typ;
    }

  }
}