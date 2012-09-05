package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.db.ebean.Model;

import com.avaje.ebean.ExpressionList;

@Entity
@Table(name = "interests_user")
public class InterestUser extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	@ManyToOne
	public Interest interest;
	
	@ManyToOne
	public User user;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;
	
	public static final Finder<User, Interest> find = new Finder<User, Interest>(
			User.class, Interest.class);
	
//	public static void create(){
//
//	}
//
//	public static void delete(Long id){
//
//	}
//	
//	public static void update(Long id){
//		
//	}
	
	public static ExpressionList<Interest> findInterestByUser(final Long id){		
		return find.where().eq("user_id", id);//.query().findRowCount();
	}
	
	public static int countInterestByUser(final Long id){		
		return find.where().eq("user_id", id).query().findRowCount();
	}
}
