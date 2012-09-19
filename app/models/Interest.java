package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name = "interests")
public class Interest extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	@Required
	public String name;
	@Required
	public String description;
	@Required
	public String picture;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;
	
	@ManyToMany(cascade = CascadeType.ALL)
	public List<User> interestedUsers;
	
	@OneToMany
	public List<Community> communities;
	
	public static final Finder<Long, Interest> find = new Finder<Long, Interest>(
			Long.class, Interest.class);
	
//	public static List<Interest> get 
	

	public static Interest create(final String name, final String picture, final String descr){
		Interest interest = new Interest();
		interest.name = name;
		interest.picture = picture;
		interest.description = descr;
		interest.save();
		return interest;
	}
	
	public static void delete(Long id){

	}
	
	public static void update(Long id){
		
	}
}
