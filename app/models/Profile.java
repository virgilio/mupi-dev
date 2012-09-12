package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "profiles")
public class Profile extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	@OneToOne
	public User user;
	
	public String firstName;
	public String lastName;
	
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date birthDate;
	public String picture;
	public String about;
	public Integer gender;
	
//	@Required
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Location> locations = new ArrayList<Location>();
		
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;


	public static final Finder<Long, Profile> find = new Finder<Long, Profile>(
			Long.class, Profile.class);

	public Profile(User user, String firstName, String lastName, Date birthDate, String picture, 
			String about, Integer gender, Date created, Date modified, List<Location> locations) {
		this.user		= user;
		this.firstName 	= firstName;
        this.lastName 	= lastName;
        this.birthDate 	= birthDate;
        this.picture 	= picture;
        this.about 		= about;
        this.gender 	= gender;
        this.created	= created;
        this.modified	= modified;
        this.locations = locations;
    }
	
	public Profile() {}
	

	/**	
	 * Método que cria profile. 
	 * @param authUser
	 * @return
	 */
	public static Profile update(
			final User user,
			final String firstName,
			final String lastName,
			final String about,
			final Date birthDate,
			final String picture,
			final Integer gender,
			final Date modified,
			final List<Location> locations) {
		
				
		final Profile p = findByUserId(user.id);
		p.user = user;
		p.firstName = firstName;
		p.lastName = lastName;
		p.birthDate = birthDate;
		p.picture = picture;
		p.about = about;
		p.gender = gender;
		p.locations = locations;
		p.modified = modified;
		
		p.update();
		User.updateName(user, firstName);
		
		return p;
	}
	
	public static Profile create(final User user) {
		Profile p = null;
		if(user != null)
			p = findByUserId(user.id);
		if(p == null){
			p = new Profile();
			p.user = user;
			p.firstName = user.name;
			p.created = new Date();
			p.modified = new Date();
			p.locations = new ArrayList<Location>();
			
			// TODO: Define a constant or let it hardcoded?
			p.picture = "/blank_profile.jpg";
			
			p.save();
		}		
		return p;
	}
	
	public static Profile updateFirstName(final User user, final String name) {
		final Profile p = findByUserId(user.id);
		p.firstName = name;
		p.update();
		return p;
	}

	
	
	public static Profile findByUserId(final Long user_id) {
		return find.where().eq("user_id", user_id).findUnique();
	}
	
	public static boolean setLocations(User user, List<Long> locations){
		Profile profile = user.profile;
		if(profile != null){
			profile.locations = Location.getLocationsByIds(locations);
			return true;
		}
		return false;
		
		
	}
}
