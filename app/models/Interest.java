package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
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
	
	public String description;

	public String picture;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;
	
	public static final Finder<Long, Interest> find = new Finder<Long, Interest>(
			Long.class, Interest.class);
	
//	public static List<Interest> get 
	
	public Interest (final String name, final String picture, final String descr){
		this.name = name;
		this.picture = picture;
		this.description = descr;
		this.created = new Date();
		this.modified = new Date();
	}

	public static Interest create(final String name, final String picture, final String descr){
		Interest interest = new Interest(name, picture, descr);
		interest.save();
		return interest;
	}
	
	public static void delete(Long id){

	}
	
	public static void update(Long id){
		
	}
}
