package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.URL;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name = "promotions")
public class Promotion extends Model {
	private static final long serialVersionUID = 1L;
//	private static final int PER_PAGE = 10;

	@Id
	public Long id;

	public Publication publication;

	@Required
	public String title;

	@Required
	public String address;

	@Required
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date date;

	@Required
	@Formats.DateTime(pattern = "HH:mm")
	public Date time;
	
	@Required
	public String description;


	public String picture;

	@URL
	public String link;
	
	public Integer status;
	
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;

	public static final Finder<Long, Promotion> find = new Finder<Long, Promotion>(
			Long.class, Promotion.class);

	public Promotion(Publication publication, String title, String address,
			Date date, Date time, String description, String picture, String link) {
		this.publication = publication;
		this.title = title;
		this.address = address;
		this.date = date;
		this.time = time;
		this.description = description;
		this.picture = picture;
		this.status = 0;
		this.link = link;
		this.created = new Date();
		this.modified = new Date();
	}

	public static void create(Profile profile, Location location, Interest interest, 
			String title, String address, Date date, Date time, String description, String link, String image) {
		
		
		String publicationBody = "O evento " + title + " foi divulgado por " + profile.firstName;
		if(profile.lastName != null) publicationBody = publicationBody + " " + profile.lastName;
		
		Publication pub = Publication.create(profile, location, interest, 1, publicationBody);
		
		Promotion prom = new Promotion(pub, title, address, date, time, description, image, link);
		prom.save();
	}

	public static void update(Long id, String title, String address, Date date,
			String description, String picture) {
		Promotion prom = find.byId(id);
		if (prom != null) {
			prom.title = title;
			prom.address = address;
			prom.date = date;
			prom.description = description;
			prom.picture = picture;
			prom.modified = new Date();
			prom.update();
		}
	}

	public static void unpublish(Long id) {
		Promotion prom = find.byId(id);
		prom.status = 1;
		prom.update();
	}

//	public static Page<Promotion> findByInterests(List<Long> interest_ids,
//			Integer page) {
//		return find.where().in("interest_id", interest_ids)
//				.orderBy("created desc").findPagingList(PER_PAGE).getPage(page);
//	}
//
//	public static Page<Promotion> findByInterest(long interest, Integer page) {
//		return find.where().eq("interest_id", interest).orderBy("created desc")
//				.findPagingList(PER_PAGE).getPage(page);
//	}
//
//	public static Page<Promotion> findByInterestLocation(long interest_id,
//			long location_id, Integer page) {
//		return find.where().eq("interest_id", interest_id)
//				.eq("location_id", location_id).orderBy("created desc")
//				.findPagingList(PER_PAGE).getPage(page);
//	}
//
//	public static Page<Promotion> findByInterestsLocation(
//			List<Long> interest_ids, long location_id, Integer page) {
//		return find.where().in("interest_id", interest_ids)
//				.eq("location_id", location_id).orderBy("created desc")
//				.findPagingList(PER_PAGE).getPage(page);
//	}
//
//	public static Page<Promotion> findByLocation(long location_id, Integer page) {
//		return find.where().eq("location_id", location_id)
//				.orderBy("created desc").findPagingList(PER_PAGE).getPage(page);
//	}

}