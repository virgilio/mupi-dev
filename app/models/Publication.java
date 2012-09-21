package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.util.CollectionUtils;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

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
	public List<PubComment> comments;
	
	@Required
	public Integer type	;
	
	@Required
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
	
	public Publication(Profile profile, Location location, Interest interest, Integer type, String body){
		this.profile   	= profile;
		this.location	= location;
		this.interest	= interest;
		this.type   	= type;
		this.body	 	= body;
		this.status  	= 0;
		this.created 	= new Date();
		this.modified 	= new Date();
	}
	
	public static void create(Profile profile, Location location, Interest interest, Integer type, String body){
		Publication pub = new Publication(profile, location, interest, type, body);
		pub.save();
	}
	
	public static void update(Long id, Integer type, String body, Integer status){
		Publication pub = find.byId(id);
		if(pub!= null){
			if(type != null) pub.type = type;
			if(body != null) pub.body = body;
			if(status != null) pub.status = status;
			pub.modified = new Date();
			pub.update();
		}
	}
	
	public static void unpublish(Long id){
		Publication pub = find.byId(id);
		pub.status = 0;
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
	
	
}