//package models;
//
//import java.util.Date;
//
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import play.data.format.Formats;
//import play.db.ebean.Model;
//
//@Entity
//@Table(name = "publication_comments")
//public class PublicationComment extends Model {
//
//	private static final long serialVersionUID = 1L;
//	
//	@Id
//	public Long id;
//	public Long publication_id;
//	public Long user_id; 
//	public String body;
//	
//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
//	public Date created;
//	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
//	public Date modified;
//	
//	public static void create(){
//		
//	}
//	
//	public static void update(Long publication_id){
//		
//	}
//	
//	public static void delete(Long publication_id){
//		
//	}
//}
