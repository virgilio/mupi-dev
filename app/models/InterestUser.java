package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;
import play.data.format.Formats;

@Entity
@Table(name = "interests_user")
public class InterestUser extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	public Long interest_id;
	public Long user_id;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;
	
	public static void create(){

	}

	public static void delete(Long id){

	}
	
	public static void update(Long id){
		
	}
	
}
