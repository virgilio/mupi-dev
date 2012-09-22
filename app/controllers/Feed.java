package controllers;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import models.PubComment;
import models.Publication;
import models.User;

import org.apache.commons.io.FileUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.AjaxResponse;
import views.html.feedHelpers.feedContent;
import be.objectify.deadbolt.actions.Restrict;

public class Feed extends Controller {

	@Restrict(Mupi.USER_ROLE)
	public static Result feed(){
		final User user = Mupi.getLocalUser(session());
		
		if(!user.profile.interests.isEmpty())
			return ok(views.html.feed.render(
				user, 
				user.profile.interests, 
				user.profile.locations, 
				null, 
				null,
				Publication.findByInterests(getInterestIds(user.profile.interests), 0).getList(),
				form(models.Promotion.class)
			));
		else 
			return Interest.interestManager();
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result comment(Long i, Long l,String body, Long id){
		final models.Profile p = Mupi.getLocalUser(session()).profile;
		final models.Publication pub = models.Publication.find.byId(id);
		
		if(pub != null){
			PubComment.create(pub, p, body);
			return selectFeed(i, l);
		}
		else{
			return badRequest();
		}
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result publish(String body, Long i, Long l){
		final models.Profile p = Mupi.getLocalUser(session()).profile;
		final models.Interest iObj = models.Interest.find.byId(i);
		final models.Location lObj = models.Location.find.byId(l);
		
		Publication.create(p, lObj, iObj, 0, body);
		return selectFeed(i,l);
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result renderFeedContent(
			int status, 
			List<models.Publication> l_pubs,
			models.Interest selectedInterest,
			models.Location selectedLocation){
		return AjaxResponse.build(status, feedContent.render(l_pubs, selectedInterest, selectedLocation, form(models.Promotion.class)).body());
	}
		
	@Restrict(Mupi.USER_ROLE)
	public static Result selectFeed(Long interest, Long location){
		final models.Profile p = Mupi.getLocalUser(session()).profile;
		if(interest == null || interest == -1){
			if(location == null || location == -1){
				return renderFeedContent(
						0, 
						Publication.findByInterests(getInterestIds(p.interests), 0).getList(), 
						null, 
						null
				);
			} else {
				final models.Location l = models.Location.find.byId(location);
				return renderFeedContent(
						0, 
						Publication.findByInterestsLocation(getInterestIds(p.interests), location, 0).getList(), 
						null, 
						l
				);
			}
		} else {
			final models.Interest i = models.Interest.find.byId(interest);
			
			if(location == null || location == -1){
				return renderFeedContent(
						0, 
						Publication.findByInterest(interest, 0).getList(), 
						i, 
						null
				);
			} else {
				final models.Location l = models.Location.find.byId(location);
				return renderFeedContent(
						0, 
						Publication.findByInterestLocation(interest, location, 0).getList(), 
						i, 
						l
				);
			}
		}
	}	
	
	
	static String BLANK_EVT = "/blank_event.jpg";
//	TODO: , String img
	@Restrict(Mupi.USER_ROLE)
	public static Result promote(){
		
		try {
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart picture = body.getFile("picture");
			String picturePath = BLANK_EVT;

			final Form<models.Promotion> filledForm = form(models.Promotion.class).bindFromRequest();
			final models.Profile p = Mupi.getLocalUser(session()).profile;
			
			DynamicForm bindedForm = form().bindFromRequest();
			final models.Interest iObj = models.Interest.find.byId(Long.valueOf(bindedForm.get("interest")));
			final models.Location lObj = models.Location.find.byId(Long.valueOf(bindedForm.get("location")));
			
			if (picture != null) {
			    String fileName = picture.getFilename();
			    File file = picture.getFile();
			    
			    String hashTime = getMD5(System.currentTimeMillis());
			    String hashCommunity = getMD5(iObj.toString() + lObj.toString());
			    
			    File destinationFile = new File(play.Play.application().path().toString() +
			    		"//public//event//picture//" + hashCommunity +
			    		"//" + hashTime + fileName);
		    	FileUtils.copyFile(file, destinationFile);
		    	picturePath = "/" + hashCommunity + "/" + hashTime + fileName;
		    	
		    	
			}else{picturePath = BLANK_EVT;}
			
			
			models.Promotion.create(
					p,
					models.Location.find.byId(lObj.id),
					models.Interest.find.byId(iObj.id),
					filledForm.get().title, 
					filledForm.get().address, 
					filledForm.get().date, 
					filledForm.get().time, 
					filledForm.get().description, 
					picturePath);
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.promotion.created"));

			return redirect(routes.Feed.feed());
			
		}catch (Exception e) {
			// TODO: handle exception
			return badRequest();
		}
	}
	
	private static List<Long> getInterestIds(List<models.Interest> i){
		ArrayList<Long> ids = new ArrayList<Long>();
		for (models.Interest interest : i) {
			ids.add(interest.id);
		}
		return ids;
	}
	
	private static String getMD5(Object input){
	    try {
			return new BigInteger(1, MessageDigest.getInstance("MD5")
					.digest(String.valueOf(input).getBytes())).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	    
	}
	

}