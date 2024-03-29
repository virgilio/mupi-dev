//package models;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//import com.avaje.ebean.ExpressionList;
//import com.avaje.ebean.Page;
//
//import play.data.format.Formats;
//import play.data.validation.Constraints.Required;
//import play.db.ebean.Model;
//
///**
// * Initial version based on work by Steve Chaloner (steve@objectify.be) for
// * Deadbolt2
// */
//@Entity
//@Table(name = "comunities")
//public class Community extends Model {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	@Id
//	public Long id;
//	
//	@Required
//	@ManyToOne
//	public Interest interest;
//	
//	@Required
//	@ManyToOne
//	public Location location;
//	
//	@ManyToOne
//	public List<Publication> publications;
//	
//	@Required
//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
//	public Date created;
//	
//	@Required
//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
//	public Date modified;
//	
//	public static final Finder<Long, Community> find = new Finder<Long, Community>(
//			Long.class, Community.class);
//
//	public Community(Interest interest, Location location) {
//		this.interest = interest;
//		this.location = location;
//		this.created  = new Date();
//		this.modified = new Date();
//	}
//	
//	public static void create(Interest interest, Location location){
//		Community community = new Community(interest, location);
//		community.save();
//	}
//	
//	public static List<Community> findByInterests(List<Interest> interests){
//		ArrayList<Long> interestIds = new ArrayList<Long>();
//		for (Interest interest : interests) {
//			interestIds.add(interest.id);
//		}
//		return find.where().in("interest_id", interestIds).findList();
//	}
//	
//	public static List<Community> findByInterest(Interest interest){
//		return find.where().eq("interest_id", interest.id).findList();
//	}
//	
//	public static Community findByInterestLocation(Long interest, Long location){
//		ExpressionList<Community> comm = find.where().eq("interest_id", interest).eq("location_id", location);
//		if(comm != null)
//			return comm.findUnique();
//		else
//			return null;
//	}
//	
//	public static List<Community> findByLocation(Location location){
//		return find.where().eq("location_id", location.id).findList();
//	}
//	
////	public static void update(Long id, Integer type, String body, Integer status){
////		Community community = find.byId(id);
////		if(community!= null){
////			if(type != null) community.type = type;
////			if(body != null) pub.body = body;
////			if(status != null) pub.status = status;
////			pub.update();
////		}
////	}
//	
////	public static void delete(Long id){
////		Publication pub = find.byId(id);
////		if(pub!=null)
////			pub.delete();
////	}
//	
//
//}
