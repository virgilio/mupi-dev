package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
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
	private Long id;

	@Required
	private String name;

	@Required
	private String geohash;


	public Long getId() {
		return id;
	}

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

	public static String getAllAsJsonArray(){
		String json = "[";
		for (Location location : find.all()) {
			json = json.concat("{'id':" + location.id + ", text:" + location.name + "}");
		}
		return json.replace("}{", "},{").concat("]");
	}
	
	public static List<Long> getIds(List<Location> l){
    ArrayList<Long> ids = new ArrayList<Long>();
    for (Location location : l) {
      ids.add(location.getId());
    }
    return ids;
  }

	public String getName() {
		return name;
	}

	public String getGeohash() {
		return geohash;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGeohash(String geohash) {
		this.geohash = geohash;
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
