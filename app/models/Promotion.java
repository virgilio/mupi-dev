package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.URL;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;

import controllers.Mupi;
import controllers.routes;

@Entity
@Table(name = "promotions")
public class Promotion extends Model {
  private static final long serialVersionUID = 1L;
  private static final int PER_PAGE = 10;
  private static final int ACTIVE = models.Publication.ACTIVE;
  private static final int INACTIVE = models.Publication.INACTIVE;

  @Id
  private Long id;

  @OneToOne
  private Publication publication;

  @Required
  private String title;

  @Required
  private String address;

  @Required
  @Formats.DateTime(pattern = "dd/MM/yyyy")
  private Date date;

  @Required
  @Formats.DateTime(pattern = "HH:mm")
  private Date time;

  @Required
  @Column(columnDefinition = "TEXT")
  private String description;


  private String picture;

  @URL
  private String link;

  private Integer status = INACTIVE;


  @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date created;

  @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date modified;

  public static final Finder<Long, Promotion> find = new Finder<Long, Promotion>(
      Long.class, Promotion.class);

  public Promotion(Publication publication, String title, String address,
      Date date, Date time, String description, String picture, String link) {
    this.publication = publication;
    this.title = title;
    this.address = address;
    this.date = date;
    this.time = time;
    this.description = description;
    this.picture = picture;
    this.status = INACTIVE;
    this.link = link;
    this.created = new Date();
    this.modified = new Date();
  }

  public static void create(Profile profile, Location location, Interest interest, 
      String title, String address, Date date, Date time, String description, String link, String image) {

    String publicationBody = "O evento " + title + " foi divulgado por ";
    
    Publication pub = Publication.create(
        profile, 
        location, 
        interest, 
        models.Publication.EVENT, 
        publicationBody);
    try {
      Promotion prom = new Promotion(pub, title, address, date, time, description, image, link);
      prom.save();
      NotificationBucket.updateBucket(pub, profile);
    }catch (Exception e) {
      if(pub!=null)
        Publication.remove(pub);
    }
  }

  public static void update(Long id, String title, String address, Date date,
      String description, String picture) {
    Promotion prom = find.byId(id);
    if (prom != null) {
      prom.setTitle(title);
      prom.setAddress(address);
      prom.setDate(date);
      prom.setDescription(description);
      prom.setPicture(picture);
      prom.setModified(new Date());
      prom.update();
    }
  }

  public static void unpublish(Long id) {
    Promotion prom = find.byId(id);
    prom.status = INACTIVE;
    prom.update();
  }

  public static Page<Promotion> findByInterests(List<Long> interest_ids, Long lastId) {
    if(lastId <= 0){
      return find.where()
          .in("publication_id",
              Publication.find.where()
                .in("interest_id", interest_ids)
                .gt("status", 0)
                .findIds()
          )
          .gt("date", new Date())
          .orderBy("date, time")
          .findPagingList(PER_PAGE)
          .getPage(0);
    }
    else{
      return find.where()
          .in("publication_id",
              Publication.find.where()
                .in("interest_id", interest_ids)
                .gt("status", 0)
                .findIds()
          )
          .gt("date",
              find.byId(lastId).getDate()
          )
          .orderBy("date, time")
          .findPagingList(PER_PAGE)
          .getPage(0);
    }
  }

  public static Page<Promotion> findByInterest(long interest, Long lastId) {
    if(lastId <= 0)
      return find.where()
        .in("publication_id",
            Publication.find.where()
              .eq("interest_id", interest)
              .gt("status", 0)
              .findIds()
        )
        .gt("date", new Date())
        .orderBy("date, time")
        .findPagingList(PER_PAGE)
        .getPage(0);
    else
      return find.where()
        .in("publication_id",
            Publication.find.where()
              .eq("interest_id", interest)
              .gt("status", 0)
              .findIds()
        )
        .gt("date",
          find.byId(lastId).getDate()
        )
        .orderBy("date, time")
        .findPagingList(PER_PAGE)
        .getPage(0);
  }

  public static Page<Promotion> findByInterestLocation(long interest_id, long location_id, Long lastId) {
    if(lastId <= 0)
      return find.where()
        .in("publication_id",
            Publication.find.where()
              .eq("interest_id", interest_id)
              .eq("location_id", location_id)
              .gt("status", 0)
              .findIds()
        )
        .gt("date", new Date())
        .orderBy("date, time")
        .findPagingList(PER_PAGE)
        .getPage(0);
    else
      return find.where()
        .in("publication_id",
            Publication.find.where()
              .eq("interest_id", interest_id)
              .eq("location_id", location_id)
              .gt("status", 0)
              .findIds()
        )
        .gt("date",
          find.byId(lastId).getDate()
        )
        .orderBy("date, time")
        .findPagingList(PER_PAGE)
        .getPage(0);
  }

  public static Page<Promotion> findByInterestsLocation(List<Long> interest_ids, long location_id, Long lastId) {
    if(lastId <= 0)
      return find.where()
        .in("publication_id",
            Publication.find.where()
              .in("interest_id", interest_ids)
              .eq("location_id", location_id)
              .gt("status", 0)
              .findIds()
        )
        .gt("date", new Date())
        .orderBy("date, time")
        .findPagingList(PER_PAGE)
        .getPage(0);
    else
      return find.where()
          .in("publication_id",
              Publication.find.where()
                .in("interest_id", interest_ids)
                .eq("location_id", location_id)
                .gt("status", 0)
                .findIds()
          )
          .gt("date",
            find.byId(lastId).getDate()
          )
          .orderBy("date, time")
          .findPagingList(PER_PAGE)
          .getPage(0);
  }

  public static Page<Promotion> findByLocation(long location_id, Long lastId) {
    if(lastId <= 0)
      return find.where()
        .in("publication_id",
            Publication.find.where()
              .eq("location_id", location_id)
              .gt("status", 0)
              .findIds()
        )
        .gt("date", new Date())
        .orderBy("date, time")
        .findPagingList(PER_PAGE)
        .getPage(0);
    else
      return find.where()
          .in("publication_id",
              Publication.find.where()
                .eq("location_id", location_id)
                .gt("status", 0)
                .findIds()
          )
          .gt("date",
            find.byId(lastId).getDate()
          )
          .orderBy("date, time")
          .findPagingList(PER_PAGE)
          .getPage(0);
  }
  
  public static Promotion getByPublicationId(Long id){
    return find.where().eq("publication_id", id).findUnique();
  }

  public Long getId() {
    return id;
  }

  public Publication getPublication() {
    return publication;
  }

  public String getTitle() {
    return title;
  }

  public String getAddress() {
    return address;
  }

  public Date getDate() {
    return date;
  }

  public Date getTime() {
    return time;
  }

  public String getDescription() {
    return description;
  }

  public String getPicture() {
    return picture;
  }

  public String getLink() {
    return link;
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

  public void setTitle(String title) {
    this.title = title;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public void setLink(String link) {
    this.link = link;
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