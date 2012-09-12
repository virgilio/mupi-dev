package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "locations")
public class Location extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	@Required
	public String name;
	
	@Required
	public String geohash;
	
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Profile> profiles;
	
	public static final Finder<Long, Location> find = new Finder<Long, Location>(
			Long.class, Location.class);

	public Location(String name, String geohash) {
		this.name = name;
		this.geohash = geohash;
	}

	public static boolean create(String name, String geohash){
		int exist = find.where().eq("name", name).findRowCount();
		if(exist > 0)
			return false;
		else{
			new Location(name, geohash).save();
			return true;
		}
	}
	
	public static List<Location> getLocationsByIds(List<Long> locationIds) {
		// TODO: Is there a different query to optimize it?
		final List<Location> locations = new ArrayList<Location>();
		for (Long locationId : locationIds) {
			Location loc = find.byId(locationId);
			if(loc != null){
				locations.add(loc);
			}
		}
		return locations;		
	}
	
//	public static List<Location> getNameIdJson() {
//		// TODO: Is there a different query to optimize it?
//		final List<Location> locations = new ArrayList<Location>();
//		for (Long locationId : locationIds) {
//			Location loc = find.byId(locationId);
//			if(loc != null){
//				locations.add(loc);
//			}
//		}
//		return locations;		
//	}
}
