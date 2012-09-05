package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.ebean.Model;
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

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;
	
	@OneToMany(cascade = CascadeType.ALL)
	public List<InterestUser> interestedUsers;
	

	public static void create(){

	}

	public static void delete(Long id){

	}
	
	public static void update(Long id){
		
	}
}
