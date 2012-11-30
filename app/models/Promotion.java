package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.URL;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;

import conf.MupiParams.PubType;
import controllers.Mupi;
import controllers.routes;

@Entity
@Table(name = "promotions")
public class Promotion extends Model {
  private static final long serialVersionUID = 1L;
  public static final int PER_PAGE = 10;
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

  /* CHANGED */
    private Integer quorum;
    
    private Integer subscriptionsLimit;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> subscribers = new ArrayList<User>();

    private Double cost;
  /* CHANGED */

  @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date created;

  @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date modified;

  public static final Finder<Long, Promotion> find = new Finder<Long, Promotion>(
      Long.class, Promotion.class);

  public Promotion(Publication publication, String title, String address, Date date, Date time, String description,
       String picture, String link, Integer quorum, Integer subscriptionsLimit, Double cost) {
    this.publication = publication;
    this.title = title;
    this.address = address;
    this.date = date;
    this.time = time;
    this.description = description;
    this.picture = picture;
    this.status = INACTIVE;
    this.link = link;
    this.quorum = quorum;
    this.subscriptionsLimit = subscriptionsLimit;
    this.cost = cost;
    this.created = new Date();
    this.modified = new Date();
  }
  
  public static void subscribeToEvent(User user, Promotion prom){
    prom.addSubscriber(user);
    prom.update();
  }

  public static void unsubscribeFromEvent(User user, Promotion prom){
    prom.removeSubscriber(user);
    prom.update();
  }
  
  public static void create(Profile profile, Location location, Interest interest, String title, String address, Date date,
       Date time, String description, String link, String image, PubType pub_typ, Integer quorum, Integer subscriptionsLimit, Double cost) {
    String publicationBody = "O evento " + title + " foi divulgado por ";
    if(pub_typ.compareTo(PubType.MUPI_EVENT) == 0){
      publicationBody = "O evento mupi" + title + " foi divulgado por ";
    }
    Publication pub = Publication.create(
        profile, 
        location, 
        interest, 
        pub_typ, 
        publicationBody);
    try {
      Promotion prom = new Promotion(pub, title, address, date, time, description, image, link, quorum, subscriptionsLimit, cost);
      prom.save();
      NotificationBucket.updateBucket(pub, profile);
    }catch (Exception e) {
      if(pub!=null)
        Publication.remove(pub);
    }
  }



  public static void unpublish(Long id) {
    Promotion prom = find.byId(id);
    prom.status = INACTIVE;
    prom.update();
  }
  
  /**
   * Find Events for a given pair (interest, location)
   * @param i interest
   * @param l location
   * @param p page
   * @param pp per page
   * @param ob order by (example: "date, time"
   * @return A page with the containing promotions
   */
  public static Page<Promotion> findByInterestLocation(Long i, Long l, Integer p, Integer pp, String ob) {
    //Getting selected interest, if it's null, get all interests
    List<Object> interests = new ArrayList<Object>();
    if(i == null || i <= 0l) interests = Interest.find.findIds();
    else interests.add(i);
    
    //Getting selected location, if it's null, get all locations
    List<Object> locations = new ArrayList<Object>();
    if(i == null || l <= 0l) locations = Location.find.findIds();
    else locations.add(l);
    
    // Setting orderBy, page and perPage parameters
    if(ob == null || ob.trim().isEmpty()) ob = "date, time";
    if(p < 0) p = 0;
    if(pp <= 0) pp = 1;
    
    return find.where()
      .in("publication_id",
          Publication.find.where()
            .in("interest_id", interests)
            .in("location_id", locations)
            .gt("status", 0)
            .findIds()
      )
      .gt("date", new Date())
      .orderBy(ob)
      .findPagingList(pp)
      .getPage(p);
  }
  
  /**
   * Find Events for a given pair (interest, location)
   * @param i interest
   * @param l location
   * @param p page
   * @param pp per page
   * @param ob order by (example: "date, time"
   * @return A page with the containing promotions
   */
  public static Page<Promotion> findByInterests(List<Object> interests, Long lastId) {
    if(lastId <= 0) 
      return find.where()
        .in("publication_id",
            Publication.find.where()
              .in("interest_id", interests)
              .gt("status", 0)
              .findIds()
        )
        .gt("date", new Date())
        .orderBy("date, time")
        .findPagingList(5)
        .getPage(0);
    else
      return find.where()
          .in("publication_id",
              Publication.find.where()
                .in("interest_id", interests)
                .gt("status", 0)
                .findIds()
          )
          .gt("date",
              find.byId(lastId).getCreated()
          )
          .gt("date", new Date())
          .orderBy("date, time")
          .findPagingList(5)
          .getPage(0);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////
  // TODO: USE THE METHOD ABOVE TO REPLACE THESE ONES BELOW
  //////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public static Page<Promotion> findByInterestLocation(Long i, Long l, Integer p, Integer pp, String ob, Long lastId) {
    //Getting selected interest, if it's null, get all interests
    List<Object> interests = new ArrayList<Object>();
    if(i == null || i <= 0l) interests = Interest.find.findIds();
    else interests.add(i);
    
    //Getting selected location, if it's null, get all locations
    List<Object> locations = new ArrayList<Object>();
    if(i == null || l <= 0l) locations = Location.find.findIds();
    else locations.add(i);
    
    // Setting orderBy, page and perPage parameters
    if(ob == null || ob.trim().isEmpty()) ob = "date, time";
    if(p < 0) p = 0;
    if(pp <= 0) pp = 1;
    
    Date lastDate = new Date(0l);
    if(lastId > 0) lastDate = find.byId(lastId).getDate(); 
    
    
    return find.where()
      .in("publication_id",
          Publication.find.where()
            .in("interest_id", interests)
            .in("location_id", locations)
            .gt("status", 0)
            .findIds()
      )
      .gt("date", lastDate)
      .orderBy(ob)
      .findPagingList(pp)
      .getPage(p);
  }
  //////////////////////////////////////////////////////////////////////////////////////////////////////
  // TODO: REPLACE THESE METHODS ABOVE
  //////////////////////////////////////////////////////////////////////////////////////////////////////
  
//public static void update(Long id, String title, String address, Date date,
//String description, String picture) {
//Promotion prom = find.byId(id);
//if (prom != null) {
//prom.setTitle(title);
//prom.setAddress(address);
//prom.setDate(date);
//prom.setDescription(description);
//prom.setPicture(picture);
//prom.setModified(new Date());
//prom.update();
//}
//}
  
  
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

  public Integer getQuorum() {
    return quorum;
  }

  public void setQuorum(Integer quorum) {
    this.quorum = quorum;
  }

  public Integer getSubscriptionsLimit() {
    return subscriptionsLimit;
  }

  public void setSubscriptionsLimit(Integer subscriptionsLimit) {
    this.subscriptionsLimit = subscriptionsLimit;
  }

  public List<User> getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(List<User> subscribers) {
    this.subscribers = subscribers;
  }

  public Double getCost() {
    return cost;
  }

  public void setCost(Double cost) {
    this.cost = cost;
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

  public void addSubscriber(User user) {
    this.subscribers.add(user);
  }
  
  public void removeSubscriber(User user) {
    this.subscribers.remove(user);
  }

}