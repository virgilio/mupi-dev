package models;

import java.util.Date;

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
@Table(name = "pubComments")
public class PubComment extends Model {

	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	@Required
	@ManyToOne
	public Publication publication;
	
	@Required
	@ManyToOne
	public User user;
	
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
	
	
	public static final Finder<Long, PubComment> find = new Finder<Long, PubComment>(
			Long.class, PubComment.class);
	
		
	
	
	public PubComment(Publication publication, User user, String body) {
		this.publication = publication;
		this.user = user;
		this.body = body;
		this.status = 0;
		this.modified = new Date();
	}

	public static void create(Publication publication, User user, String body){
		PubComment pubComment = new PubComment(publication, user, body);
		pubComment.save();
	}
	
	public static void uncomment(Long id){
		PubComment pubComment = find.byId(id);
		pubComment.status = 1;
		pubComment.modified = new Date();
		pubComment.update();
	}
}
