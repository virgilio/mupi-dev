package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
    @Table(name = "notification_buckets")
    public class NotificationBucket extends Model {
	
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
	
	
	public static final Finder<Long, NotificationBucket> find = new Finder<Long, NotificationBucket>(
													 Long.class, NotificationBucket.class);
	
		
	public NotificationBucket(Publication publication, Profile profile, String body) {
	    this.publication = publication;
	    this.profile = profile;
	    this.body = body;
	    this.status = 0;
	    this.created = new Date();
	    this.modified = new Date();
	}
	

	public static void create(Publication publication, Profile profile){
	    String body = "A publicação '" 
		+ publication.getBody().substring(0, 10) 
		+ "...' no interesse " 
		+ publication.getInterest().getName() 
		+ " foi atualiza!";
	    NotificationBucket notificationBucket = new NotificationBucket(publication, profile, body);
	    notificationBucket.save();
	}
	
	
	// public static void updateBucket(Long publication, Long profile, String body){
	public static void updateBucket(Publication publication, Profile profile){
	    NotificationBucket nb = find.where()
                .eq("publication_id", publication.getId())
		.eq("profile_id", profile.getId()).findUnique();
	    if(nb == null) { // The user is not following that publication yet
		NotificationBucket.create(publication, profile);
	    } 
	    
	    // Update Status, the user is already following! Notify everybody else =D
	    notifyBuckets(publication, profile);
	}
	
	public static void notifyBuckets(Publication publication, Profile profile){
	    // Get everyone that have this publication in its bucket! =D
	    List<NotificationBucket> nb_l = find.where()
		.eq("publication_id", publication.getId()).findList();
	    for(NotificationBucket nb : nb_l){
		if(nb.getProfile().getId() != profile.getId()){
		    nb.setStatus(nb.getStatus() + 1);
		    nb.update();
		}
		else {
		    setNotified(publication, profile);
		}
	    }
	}
	
	public static void setNotified(Publication publication, Profile profile){
	    NotificationBucket nb = find.where()
                .eq("publication_id", publication.getId())
                .eq("profile_id", profile.getId()).findUnique();
            if(nb != null) { // The user is not following that publication yet
		nb.setStatus(0);
		nb.update();
            }
	}

	public static List<NotificationBucket> getBucket(Profile profile){
	    return find.where()
                .eq("profile_id", profile.getId())
		.gt("status", 0)
		.findList();
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

	public void setId(Long id) {
	    this.id = id;
	}

	public void setPublication(Publication publication) {
	    this.publication = publication;
	}

	public void setProfile(Profile profile) {
	    this.profile = profile;
	}

	public void setBody(String body) {
	    this.body = body;
	}

	public void setStatus(Integer status) {
	    this.status = status;
	}

	public void setCreated(Date created) {
	    this.created = created;
	}

	public void setModified(Date modified) {
	    this.modified = modified;
	}
	
	
    }