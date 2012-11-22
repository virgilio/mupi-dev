package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name = "interests")
public class Interest extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Required
	private String name;
	
	@ManyToOne
	private Profile profile;
	
	private String description;

	private String picture;
	
	private Integer status = 0;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modified;
	
	public static final Finder<Long, Interest> find = new Finder<Long, Interest>(
			Long.class, Interest.class);
	
//	public static List<Interest> get 
	
	public Interest (final Profile profile, final String name, final String picture, final String descr){
		this.name = name;
		this.profile = profile;
		this.picture = picture;
		this.description = descr;
		this.status = 0;
		this.created = new Date();
		this.modified = new Date();
	}

	public static Interest create(final Profile profile, final String name, final String picture, final String descr){
		Interest interest = new Interest(profile, name, picture, descr);
		interest.save();
		return interest;
	}
	
	 
  public static List<Long> getIds(List<Interest> i){
    ArrayList<Long> ids = new ArrayList<Long>();
    for (Interest interest : i) {
      ids.add(interest.getId());
    }
    return ids;
  }
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Profile getProfile() {
		return profile;
	}

	public String getDescription() {
		return description;
	}

	public String getPicture() {
		return picture;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPicture(String picture) {
		this.picture = picture;
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
