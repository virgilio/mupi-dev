package models;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;

import controllers.Profile.ProfileForm;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.data.Form;
import play.data.format.Formats;

@Entity
@Table(name = "interests")
public class Interest extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	public String name;
	public String description;
	public String picture;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;
	
	@ManyToMany(cascade = CascadeType.ALL)
	public List<User> interestedUsers;
	
	
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
