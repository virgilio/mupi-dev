package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.Page;

@Entity
@Table(name = "publications")
public class Publication extends Model {
	private static final long serialVersionUID = 1L;
	static final int PER_PAGE = 10;
	public static final int ACTIVE = 1;
	public static final int INACTIVE = 0;
	public static final int PUBLICATION = 0;
	public static final int EVENT = 1;
	
	@Id
	public Long id;
	
//	@Required
//	@ManyToOne
//	public Community community;

	@Required
	@ManyToOne
	private Interest interest;
	
	@Required
	@ManyToOne
	private Location location;
	
	@Required
	@ManyToOne
	private Profile profile;
	
	@OneToMany
	private List<PubComment> comments = new ArrayList<PubComment>();
	
	@Required
	private Integer pub_typ;
	
	@Required
	@Column(columnDefinition = "TEXT")
	private String body;

	@Required
	private Integer status;	
	
	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
	
	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modified;
	
	public static final Finder<Long, Publication> find = new Finder<Long, Publication>(
			Long.class, Publication.class);
	
	public Publication(Profile profile, Location location, Interest interest, Integer pub_typ, String body){
		this.profile   	= profile;
		this.location	= location;
		this.interest	= interest;
		this.body	 	= body;
		this.pub_typ	= pub_typ;
		if(pub_typ == EVENT) {this.status = INACTIVE;}
		else this.status = ACTIVE;
		this.created 	= new Date();
		this.modified 	= new Date();
	}
	
	public static Publication create(Profile profile, Location location, Interest interest, Integer pType, String body){
		Publication pub = new Publication(profile, location, interest, pType, body);
		pub.save();
		NotificationBucket.updateBucket(pub, profile);
		return pub;
	}
	
	public static void update(Long id, String body, Integer status){
		Publication pub = find.byId(id);
		if(pub!= null){
			if(body != null)    pub.setBody(body);
			if(status != null)  pub.setStatus(status);			
			if(body!=null || status!= null)pub.setModified(new Date());
			pub.update();
		}
	}
	
	public static void remove(Publication p){
	    p.delete();
	}
    
	public static void unpublish(Long id){
		Publication pub = find.byId(id);
		pub.setStatus(INACTIVE);
		pub.update();
	}
	
	public static Page<Publication> findByInterests(List<Long> interest_ids, Long lastId){
	  if(lastId <= 0) 
	    return find.where()
	        .in("interest_id", interest_ids)
	        .gt("status", 0)
	        .orderBy("created desc")
	        .findPagingList(PER_PAGE)
	         .getPage(0);
	  else
  		return find.where()
  		    .in("interest_id", interest_ids)
  		    .lt("created",
  		        find.byId(lastId).getCreated()
  		     )
  		    .gt("status", 0)
  				.orderBy("created desc")
  				.findPagingList(PER_PAGE)
  				.getPage(0);
	}
	
	public static Page<Publication> findByInterest(long interest, Long lastId){
	  if(lastId <= 0)
	    return find.where()
	        .eq("interest_id", interest)
	        .gt("status", 0)
	        .orderBy("created desc")
	        .findPagingList(PER_PAGE)
	        .getPage(0);
	  else
  		return find.where()
  				.eq("interest_id", interest)
  				.lt("created",
              find.byId(lastId).getCreated()
           )
           .gt("status", 0)
  				.orderBy("created desc")
  				.findPagingList(PER_PAGE)
  				.getPage(0);
	}
	
	public static Page<Publication> findByInterestLocation(long interest_id, long location_id, Long lastId){
	  if(lastId <= 0)
	    return find.where()
          .eq("interest_id", interest_id)
          .eq("location_id", location_id)
          .gt("status", 0)
          .orderBy("created desc")
          .findPagingList(PER_PAGE)
          .getPage(0);
	  else
  		return find.where()
  				.eq("interest_id", interest_id)
  				.eq("location_id", location_id)
  				.lt("created",
              find.byId(lastId).getCreated()
           )
           .gt("status", 0)
  				.orderBy("created desc")
  				.findPagingList(PER_PAGE)
  				.getPage(0);
	}
	
	public static Page<Publication> findByInterestsLocation(List<Long> interest_ids, long location_id, Long lastId){
	  if(lastId <= 0)
	    return find.where()
	        .in("interest_id", interest_ids)
	        .eq("location_id", location_id)
	        .gt("status", 0)
	        .orderBy("created desc")
	        .findPagingList(PER_PAGE)
	        .getPage(0);
	  else 
	    return find.where()
				.in("interest_id", interest_ids)
				.eq("location_id", location_id)
				.lt("created",
            find.byId(lastId).getCreated()
         )
         .gt("status", 0)
				.orderBy("created desc")
				.findPagingList(PER_PAGE)
				.getPage(0);
	}
	
	public static Page<Publication> findByLocation(long location_id, Long lastId){
	  if(lastId <= 0)
	    return find.where()
	        .eq("location_id", location_id)
	        .gt("status", 0)
	        .orderBy("created desc")
	        .findPagingList(PER_PAGE)
	        .getPage(0);
	  else
	    return find.where()
				.eq("location_id", location_id)
				.lt("created",
            find.byId(lastId).getCreated()
         )
        .gt("status", 0)
				.orderBy("created desc")
				.findPagingList(PER_PAGE)
				.getPage(0);
	}
	
	
	
	public static Page<Publication> findNewerByInterests(List<Long> interest_ids, Long firstId){
    if(firstId <= 0) 
      return find.where()
          .in("interest_id", interest_ids)
          .gt("status", 0)
          .orderBy("created desc")
          .findPagingList(PER_PAGE)
           .getPage(0);
    else
      return find.where()
          .in("interest_id", interest_ids)
          .gt("created",
              find.byId(firstId).getCreated()
           )
          .gt("status", 0)
          .orderBy("created desc")
          .findPagingList(PER_PAGE)
          .getPage(0);
  }
  
  public static Page<Publication> findNewerByInterest(long interest, Long firstId){
    if(firstId <= 0)
      return find.where()
          .eq("interest_id", interest)
          .gt("status", 0)
          .orderBy("created desc")
          .findPagingList(PER_PAGE)
          .getPage(0);
    else
      return find.where()
          .eq("interest_id", interest)
          .gt("created",
              find.byId(firstId).getCreated()
           )
           .gt("status", 0)
          .orderBy("created desc")
          .findPagingList(PER_PAGE)
          .getPage(0);
  }
  
  public static Page<Publication> findNewerByInterestLocation(long interest_id, long location_id, Long firstId){
    if(firstId <= 0)
      return find.where()
          .eq("interest_id", interest_id)
          .eq("location_id", location_id)
          .gt("status", 0)
          .orderBy("created desc")
          .findPagingList(PER_PAGE)
          .getPage(0);
    else
      return find.where()
          .eq("interest_id", interest_id)
          .eq("location_id", location_id)
          .gt("created",
              find.byId(firstId).getCreated()
           )
           .gt("status", 0)
          .orderBy("created desc")
          .findPagingList(PER_PAGE)
          .getPage(0);
  }
  
  public static Page<Publication> findNewerByInterestsLocation(List<Long> interest_ids, long location_id, Long firstId){
    if(firstId <= 0)
      return find.where()
          .in("interest_id", interest_ids)
          .eq("location_id", location_id)
          .gt("status", 0)
          .orderBy("created desc")
          .findPagingList(PER_PAGE)
          .getPage(0);
    else 
      return find.where()
        .in("interest_id", interest_ids)
        .eq("location_id", location_id)
        .gt("created",
            find.byId(firstId).getCreated()
         )
         .gt("status", 0)
        .orderBy("created desc")
        .findPagingList(PER_PAGE)
        .getPage(0);
  }
  
  public static Page<Publication> findNewerByLocation(long location_id, Long firstId){
    if(firstId <= 0)
      return find.where()
          .eq("location_id", location_id)
          .gt("status", 0)
          .orderBy("created desc")
          .findPagingList(PER_PAGE)
          .getPage(0);
    else
      return find.where()
        .eq("location_id", location_id)
        .gt("created",
            find.byId(firstId).getCreated()
         )
        .gt("status", 0)
        .orderBy("created desc")
        .findPagingList(PER_PAGE)
        .getPage(0);
  }
	
	
	
	public static Profile getProfilePicById(Long id){		
		return Profile.find.byId(find.byId(id).getProfile().getId());
	}

	public Long getId() {
		return id;
	}
	
	public Interest getInterest() {
		return interest;
	}

	public Location getLocation() {
		return location;
	}

	public Profile getProfile() {
		return profile;
	}

	public List<PubComment> getComments() {
		return comments;
	}

	public Integer getPub_typ() {
		return pub_typ;
	}

	public String getBody() {
		return body;
	}

	public Integer getStatus() {
		return status;
	}

	public Date getCreated() {
		return created;
	}

	public Date getModified() {
		return modified;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setInterest(Interest interest) {
		this.interest = interest;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public void setComments(List<PubComment> comments) {
		this.comments = comments;
	}

	public void setPub_typ(Integer pub_typ) {
		this.pub_typ = pub_typ;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
}