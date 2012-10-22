package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jsoup.Jsoup;

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
    String body  = "";
    int size;
    boolean notify = false;
    if(publication.getPub_typ() == 1){
      Promotion p = Promotion.getByPublicationId(publication.getId());
      if(p != null) {
	String text = Jsoup.parse(p.getTitle()).text();
	size = text.length() > 50 ? 50 : text.length();
	body = "O evento '" + Jsoup.parse(p.getTitle()).text().substring(0, size);
	notify = true;
	//System.out.println("Não sou nulo");
      }
      //else 
      //System.out.println("Sou nulo");
    } else {
      String text = Jsoup.parse(publication.getBody()).text();
      size = text.length() > 50 ? 50 : text.length();
      body = "A publicação '" + Jsoup.parse(publication.getBody()).text().substring(0, size);
      notify = true;
    }
    //System.out.println("Salva? " + (notify ? "sim" : "não"));
    if(notify == true){
      body += "...' no interesse " 
	+ publication.getInterest().getName() 
	+ " recebeu comentários!";
      //System.out.println("Vou salvar!");
      NotificationBucket notificationBucket = new NotificationBucket(publication, profile, body);
      notificationBucket.save();
    }
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
    //notifyMail(publication, profile);
  }

  public static void notifyMail(Publication publication, Profile profile){
    
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

  public static void setAllNotified(Profile profile){
    List<NotificationBucket> nb_l = find.where()
      .eq("profile_id", profile.getId())
      .findList();

    if(nb_l != null) {
      for(NotificationBucket nb : nb_l){
	nb.setStatus(0);
	nb.update();
      }
    }
  }

  public static void removeFromBucket(Profile profile, Publication publication){
    NotificationBucket nb = NotificationBucket.find.where()
      .eq("profile_id", profile.getId())
      .eq("publication_id", publication.getId()).findUnique();
    nb.delete();
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
