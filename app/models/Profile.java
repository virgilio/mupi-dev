package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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

	@Id
	public Long id;
	
	@OneToOne
	public User user;
	
	public String firstName;
	public String lastName;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd")
	public Date birthDate;
	public String picture;
	public String about;
	public Integer gender;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;


	public static final Finder<Long, Profile> find = new Finder<Long, Profile>(
			Long.class, Profile.class);

	public Profile(User user, String firstName, String lastName, Date birthDate, String picture, 
			String about, Integer gender, Date created, Date modified) {
		this.user		= user;
		this.firstName 	= firstName;
        this.lastName 	= lastName;
        this.birthDate 	= birthDate;
        this.picture 	= picture;
        this.about 		= about;
        this.gender 	= gender;
        this.created	= created;
        this.modified	= modified;
    }
	
//	public Profile(User user) {
//		Profile profile = findByUser(user.id);
//    }
	
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
			final Date created,
			final Date modified) {
		
				
		final Profile p = findByUserId(user.id);
		p.user = user;
		p.firstName = firstName;
		p.lastName = lastName;
		p.birthDate = birthDate;
		p.picture = picture;
		p.about = about;
		p.gender = gender;
		p.created = created;
		p.modified = modified;
		
		p.update();
		User.updateName(user, firstName);
		
		return p;
	}
	
	public static Profile create(final User user) {
		Profile p = findByUserId(user.id);
		if(p == null){
			p = new Profile();
			p.user = user;
			p.firstName = user.name;
			p.created = new Date();
			p.modified = new Date();
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
}
