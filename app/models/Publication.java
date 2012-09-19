package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name = "publications")
public class Publication extends Model {

	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	@Required
	@ManyToOne
	public Community community;
	
	@Required
	@ManyToOne
	public User user;
	
	@OneToMany
	public List<PubComment> comments;
	
	@Required
	public Integer type	;
	
	@Required
	public String body;

	@Required
	public Integer status;	
	
	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	
	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;
	
	public static final Finder<Long, Publication> find = new Finder<Long, Publication>(
			Long.class, Publication.class);
	
	public Publication(User user, Community community, Integer type, String body){
		this.user   	= user;
		this.community	= community;
		this.type   	= type;
		this.body	 	= body;
		this.status  	= 0;
		this.created 	= new Date();
		this.modified 	= new Date();
	}
	
	public static void create(User user, Community community, Integer type, String body){
		Publication pub = new Publication(user, community, type, body);
		pub.save();
	}
	
	public static void update(Long id, Integer type, String body, Integer status){
		Publication pub = find.byId(id);
		if(pub!= null){
			if(type != null) pub.type = type;
			if(body != null) pub.body = body;
			if(status != null) pub.status = status;
			pub.modified = new Date();
			pub.update();
		}
	}
	
	public static void unpublish(Long id){
		Publication pub = find.byId(id);
		pub.status = 0;
		pub.update();
	}
}
