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
	private static final int PER_PAGE = 10;
	private static final int ACTIVE = 1;
	private static final int INACTIVE = 0;
	
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
	private Integer status = INACTIVE;	
	
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
		this.pub_typ   	= pub_typ;
		this.body	 	= body;
		this.status  	= INACTIVE;
		this.created 	= new Date();
		this.modified 	= new Date();
	}
	
	public static Publication create(Profile profile, Location location, Interest interest, Integer pType, String body){
		Publication pub = new Publication(profile, location, interest, pType, body);
		pub.save();
		return pub;
	}
	
	public static void update(Long id, Integer pub_typ, String body, Integer status){
		Publication pub = find.byId(id);
		if(pub!= null){
			if(pub_typ != null) pub.pub_typ = pub_typ;
			if(body != null) pub.body = body;
			if(status != null) pub.status = status;
			pub.modified = new Date();
			pub.update();
		}
	}
	
	public static void unpublish(Long id){
		Publication pub = find.byId(id);
		pub.status = INACTIVE;
		pub.update();
	}
	
	public static Page<Publication> findByInterests(List<Long> interest_ids, Integer page){
		return find.where()
				.in("interest_id", interest_ids)
				.orderBy("created desc")
				.findPagingList(PER_PAGE)
				.getPage(page);
	}
	
	public static Page<Publication> findByInterest(long interest, Integer page){
		return find.where()
				.eq("interest_id", interest)
				.orderBy("created desc")
				.findPagingList(PER_PAGE)
				.getPage(page);
	}
	
	public static Page<Publication> findByInterestLocation(long interest_id, long location_id, Integer page){
		return find.where()
				.eq("interest_id", interest_id)
				.eq("location_id", location_id)
				.orderBy("created desc")
				.findPagingList(PER_PAGE)
				.getPage(page);
	}
	
	public static Page<Publication> findByInterestsLocation(List<Long> interest_ids, long location_id, Integer page){
		return find.where()
				.in("interest_id", interest_ids)
				.eq("location_id", location_id)
				.orderBy("created desc")
				.findPagingList(PER_PAGE)
				.getPage(page);
	}
	
	public static Page<Publication> findByLocation(long location_id, Integer page){
		return find.where()
				.eq("location_id", location_id)
				.orderBy("created desc")
				.findPagingList(PER_PAGE)
				.getPage(page);
	}
	
	public static Profile getProfilePicById(Long id){
		System.out.println(id);
		System.out.println(find.byId(id).profile.getId());
		
		return Profile.find.byId(find.byId(id).profile.getId());
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
	
	
	
}