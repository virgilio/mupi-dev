package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
@Table(name = "publications")
public class Publication extends Model {

	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	public Long interest_id;
	public Long user_id;
	/**
	 * FIXME Definir como será a abordagem de grupos
	 */
	public Long localization_id;
	public Long type_id;
	/**
	 * FIXME Revisar se haverá campo título
	 */
	public String title; 
	public String body;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date created;
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date modified;
	
	public static void create(){
		
	}
	
	public static void update(Long publication_id){
		
	}
	
	public static void delete(Long publication_id){
		
	}
}
