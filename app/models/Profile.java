package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.format.Formats;
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
	private static final Integer FIRST_LOGIN = 0;
	private static final Integer REGULAR = 1;

	@Id
	public Long id;

	public String firstName;
	public String lastName;
	
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date birthDate;
	
	public String picture = "/blank_profile.jpg";
	
	@Column(columnDefinition = "TEXT")
	public String about;
	
	public Integer gender;
	
	public Integer status;
	
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Location> locations = new ArrayList<Location>();
	
	@ManyToMany(cascade = CascadeType.ALL)
	public List<Interest> interests = new ArrayList<Interest>();
	
	@OneToMany
	public List<Publication> publications = new ArrayList<Publication>();
	
	@OneToMany
	public List<PubComment> pubComments = new ArrayList<PubComment>();
		
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;


	public static final Finder<Long, Profile> find = new Finder<Long, Profile>(
			Long.class, Profile.class);
	
	public Profile(User user, String firstName, String lastName, Date birthDate, String picture, 
			String about, Integer gender, List<Location> locations) {
		this.firstName 	= firstName;
        this.lastName 	= lastName;
        this.birthDate 	= birthDate;
        this.picture 	= picture;
        this.about 		= about;
        this.gender 	= gender;
        this.locations  = locations;
        this.status 	= FIRST_LOGIN;
        this.created	= new Date();
        this.modified	= new Date();
    }
	
	public Profile() {}
	
	public Profile(String name) {
		this.firstName = name;
		this.status = FIRST_LOGIN;
		// TODO: Define a constant or let it hardcoded?
		this.picture = "/blank_profile.jpg";
		this.created = new Date();
		this.modified = new Date();
	}
	

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
			final List<Location> locations) {
		
				
		final Profile p = user.profile;

		p.firstName = firstName;
		p.lastName 	= lastName;
		p.birthDate = birthDate;
		p.picture 	= picture;
		p.about 	= about;
		p.gender 	= gender;
		p.locations = locations;
		p.modified 	= new Date();
		
		p.update();
		
		User.updateName(user, firstName);
		
		return p;
	}
	
	public Profile changeStatus(final Integer newStatus){
		this.status = newStatus;
		this.save();
		return this;
	}
	
	public static Profile create(final User user) {
		Profile p = new Profile();
		p.firstName = user.name;
		p.created = new Date();
		p.modified = new Date();
		p.locations = new ArrayList<Location>();
		p.status = FIRST_LOGIN;
		
		// TODO: Define a constant or let it hardcoded?
		p.picture = "/blank_profile.jpg";
		
		p.save();
		
		return p;
	}
	
	public static Profile updateFirstName(final User user, final String name) {
		final Profile p = user.profile;
		p.firstName = name;
		p.modified = new Date();
		p.update();
		return p;
	}

	public static boolean setLocations(User user, List<Long> locations){
		Profile profile = user.profile;
		if(profile != null){
			profile.locations = Location.getLocationsByIds(locations);
			profile.modified = new Date();
			return true;
		}
		return false;
	}
	
	public String getLocationJsonArray(){
		String json = "[";
		for (Location location : this.locations) {
			json = json.concat("{'id':" + location.id + ", name:" + location.name + "}");
		}
		return json.replace("}{", "},{").concat("]");
	}
	
	public static boolean checkInterest(final User user, final Long interest) {
		final Profile p = User.findByEmail(user.email).profile;
		if (p != null && p.interests.add(models.Interest.find.byId(interest))){
			p.update();
			return true;
		}
		return false;
	}
	
	public static boolean uncheckInterest(final User user, final Long interest) {
		final Profile p = User.findByEmail(user.email).profile;
		final Interest i = models.Interest.find.byId(interest);
		if (p != null && i != null && p.interests.remove(i)){
			p.update();
			return true;
		}
		return false;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
//
//	public Date getBirthDate() {
//		return birthDate;
//	}
//
	public String getPicture() {
		return picture;
	}
//
//	public String getAbout() {
//		return about;
//	}
//
//	public Integer getGender() {
//		return gender;
//	}
//
//	public List<Location> getLocations() {
//		return locations;
//	}
//
//	public List<Interest> getInterests() {
//		return interests;
//	}
//
//	public List<Publication> getPublications() {
//		return publications;
//	}
//
//	public List<PubComment> getPubComments() {
//		return pubComments;
//	}
//
//	public Date getCreated() {
//		return created;
//	}
//
//	public Date getModified() {
//		return modified;
//	}	
}
