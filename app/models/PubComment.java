package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name = "pub_comments")
public class PubComment extends Model {

	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id;
	
	@Required
	@ManyToOne
	private Publication publication;
	
	@Required
	@ManyToOne
	private Profile profile;
	
	@Required
	@Column(columnDefinition = "TEXT")
	private String body;

	@Required
	private Integer status;	
		
	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
	
	@Required
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modified;
	
	
	public static final Finder<Long, PubComment> find = new Finder<Long, PubComment>(
			Long.class, PubComment.class);
	
		
	
	
	public PubComment(Publication publication, Profile profile, String body) {
		this.publication = publication;
		this.profile = profile;
		this.body = body;
		this.status = 0;
		this.created = new Date();
		this.modified = new Date();
	}

	public static void create(Publication publication, Profile profile, String body){
		PubComment pubComment = new PubComment(publication, profile, body);
		pubComment.save();
	}
	
	public static void uncomment(Long id){
		PubComment pubComment = find.byId(id);
		pubComment.status = 1;
		pubComment.modified = new Date();
		pubComment.update();
	}

	public Long getId() {
		return id;
	}
		
	public Publication getPublication() {
		return publication;
	}

	public Profile getProfile() {
		return profile;
	}

	public String getBody() {
		return body;
	}

	public Integer getStatus() {
		return status;
	}

	public Date getCreated() {
		return created;
	}

	public Date getModified() {
		return modified;
	}
	
	
}
