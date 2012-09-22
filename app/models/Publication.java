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
	
	@Id
	public Long id;
	
//	@Required
//	@ManyToOne
//	public Community community;

	@Required
	@ManyToOne
	public Interest interest;
	
	@Required
	@ManyToOne
	public Location location;
	
	@Required
	@ManyToOne
	public Profile profile;
	
	@OneToMany
	public List<PubComment> comments = new ArrayList<PubComment>();
	
	@Required
	public Integer pub_typ;
	
	@Required
	@Column(columnDefinition = "TEXT")
	public String body;

	@Required
	public Integer status;	
	
	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	
	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;
	
	public static final Finder<Long, Publication> find = new Finder<Long, Publication>(
			Long.class, Publication.class);
	
	public Publication(Profile profile, Location location, Interest interest, Integer pub_typ, String body){
		this.profile   	= profile;
		this.location	= location;
		this.interest	= interest;
		this.pub_typ   	= pub_typ;
		this.body	 	= body;
		this.status  	= 0;
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
		pub.status = 1;
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
		System.out.println(find.byId(id).profile.id);
		
		return Profile.find.byId(find.byId(id).profile.id);
	}
}