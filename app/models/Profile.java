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

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.annotation.Sql;

import conf.MupiParams;

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
    private static final String NO_PIC = "/blank.jpg";
    
    @Id
    private Long id;
    
    private String firstName = "";
    
    private String lastName;
    
    @Formats.DateTime(pattern = "dd/MM/yyyy")
    private Date birthDate;
    
    private String picture = NO_PIC;

    @Column(columnDefinition = "TEXT")
    private String about;

    private Integer gender;

    private Integer status = MupiParams.FIRST_LOGIN;

    private Integer notificationLevel = MupiParams.NotificationLevel.NORMAL.ordinal();

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<Location>();

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Interest> interests = new ArrayList<Interest>();

    @OneToMany
    private List<Publication> publications = new ArrayList<Publication>();

    @OneToMany
    private List<PubComment> pubComments = new ArrayList<PubComment>();

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;
    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modified;


    public static final Finder<Long, Profile> find = new Finder<Long, Profile>(Long.class, Profile.class);
    
    public Profile(User user, String firstName, String lastName, Date birthDate, String picture,
                   String about, Integer gender, Integer notificationLevel, List<Location> locations) {
        this.firstName 	= firstName;
        this.lastName 	= lastName;
        this.birthDate 	= birthDate;
        this.picture 	= picture;
        this.about 		= about;
        this.gender 	= gender;
        this.notificationLevel = notificationLevel;
        this.locations  = locations;
        this.status 	= MupiParams.FIRST_LOGIN;
        this.created	= new Date();
        this.modified	= new Date();
    }
    
    public Profile() {
        this.status = MupiParams.FIRST_LOGIN;
        this.firstName = "";
        this.picture = NO_PIC;
        this.created = new Date();
        this.modified = new Date();
    }
    
    public Profile(String name) {
        this.firstName = name;
        this.status = MupiParams.FIRST_LOGIN;
        this.notificationLevel = MupiParams.NotificationLevel.NORMAL.ordinal();
        this.picture = NO_PIC;
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
                                 final Integer notificationLevel,
                                 final List<Location> locations) {


        final Profile p = user.profile;
        System.out.println(notificationLevel + ": " + p.getNotificationLevel());
        

        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setBirthDate(birthDate);
        p.setPicture(picture);
        p.setAbout(about);
        p.setGender(gender);
        p.setNotificationLevel(notificationLevel);
        p.setLocations(locations);
        p.setModified(new Date());

        p.update();

        User.updateName(user, firstName);

        return p;
    }

    public static Profile changeStatus(final Profile profile, final Integer newStatus){
        final Profile p = find.byId(profile.getId());
        p.setStatus(newStatus);
        p.save();
        return profile;
    }

    public static Profile create(final User user) {
        Profile p = new Profile();
        p.setFirstName(user.name);
        p.setStatus(MupiParams.FIRST_LOGIN);
        p.setPicture(NO_PIC);
        p.setLocations(new ArrayList<Location>());
        p.setCreated(new Date());
        p.setModified(new Date());


        p.save();

        return p;
    }

    public static Profile updateFirstName(final User user, final String name) {
        final Profile p = user.profile;
        p.setFirstName(name);
        p.setModified(new Date());
        p.update();
        return p;
    }

    public static boolean setLocations(User user, List<Long> locations){
        Profile profile = user.profile;
        if(profile != null){
            profile.setLocations(Location.getLocationsByIds(locations));
            profile.setModified(new Date());
            return true;
        }
        return false;
    }

    public String getLocationJsonArray(){
        String json = "[";
        for (Location location : this.locations) {
            json = json.concat("{'id':" + location.getId() + ", text:" + location.getName() + "}");
        }
        return json.replace("}{", "},{").concat("]");
    }

    public static boolean checkInterest(final User user, final Long interest) {
        final Profile p = User.findByEmail(user.email).getProfile();
        if (p != null && p.getInterests().add(models.Interest.find.byId(interest))){
            p.update();
            return true;
        }
        return false;
    }

    public static boolean uncheckInterest(final User user, final Long interest) {
        final Profile p = User.findByEmail(user.email).profile;
        final Interest i = models.Interest.find.byId(interest);
        if (p != null && i != null && p.getInterests().remove(i)){
            p.update();
            return true;
        }
        return false;
    }


	@Entity  
	@Sql  
	public class UserEmail {
		String email;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	/**
	 * Return a list of e-mails of users given a publication
	 * @param id
	 * @return
	 */
	public static List<UserEmail> emailsFromPublication(Publication p){
		String sql = "" +
				"select 												" +
				"	u.email as email									" +
				"from notification_buckets nb							" +
				"	left join profiles pr on nb.profile_id = pr.id 		" +
				"	left join users u on u.profile_id = nb.profile_id 	";
		RawSql rs = RawSqlBuilder.parse(sql).create();

		Query<UserEmail> q = Ebean.find(UserEmail.class);
		q.setRawSql(rs)
		.where().eq("nb.publication_id", p.getId());

		return q.findList();
	}
	
	public static List<UserEmail> emailsFromInterestAndLocation(Interest i, Location l){
		String sql = "" +
				"select 												" +
				"	u.email as email									" +
				"from notification_buckets nb							" +
				"	left join profiles pr on nb.profile_id = pr.id 		" +
				"	left join users u on u.profile_id = nb.profile_id 	";
		RawSql rs = RawSqlBuilder.parse(sql).create();

		Query<UserEmail> q = Ebean.find(UserEmail.class);
		q.setRawSql(rs);
//		.where().eq("nb.publication_id", p.getId());

		return q.findList();
	}

	public Long getId() {
		return id;
	}

	public Integer getStatus() {
		return status;
	}

	public Integer getNotificationLevel() {
		return notificationLevel;
	}


	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public String getPicture() {
		return picture;
	}

	public String getAbout() {
		return about;
	}

	public Integer getGender() {
		return gender;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public List<Interest> getInterests() {
		return interests;
	}

	public List<Publication> getPublications() {
		return publications;
	}

	public List<PubComment> getPubComments() {
		return pubComments;
	}

	public Date getCreated() {
		return created;
	}

	public Date getModified() {
		return modified;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setNotificationLevel(Integer notificationLevel) {
		this.notificationLevel = notificationLevel;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public void setInterests(List<Interest> interests) {
		this.interests = interests;
	}

	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}

	public void setPubComments(List<PubComment> pubComments) {
		this.pubComments = pubComments;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
}
