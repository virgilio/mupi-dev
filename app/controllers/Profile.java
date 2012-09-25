package controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import models.Location;
import models.User;

import org.apache.commons.io.FileUtils;

import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.AjaxResponse;
import views.html.profile;
import be.objectify.deadbolt.actions.Restrict;

public class Profile extends Controller {
	static String BLANK_PIC = "/blank_profile.jpg";
	
	private static final Form<models.Profile> PROFILE_FORM = form(models.Profile.class);
	
	@Restrict(Mupi.USER_ROLE)
	public static Result profile() {
		final User user = Mupi.getLocalUser(session());	

		models.Profile p = user.getProfile();
		final Form<models.Profile> form = PROFILE_FORM.fill(p);
		
		final List<Location> notSelected = Location.find.fetch("id", "name").findList();

		return ok(profile.render(form, p, notSelected));
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result doProfile() {
		final Form<models.Profile> filledForm = PROFILE_FORM.bindFromRequest();
		final User user = Mupi.getLocalUser(session());
		
		
		try {
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart picture = body.getFile("picture");
			
			String picturePath = BLANK_PIC;
			
			if (picture != null) {
			    String fileName = picture.getFilename();
			    File file = picture.getFile();
			    
			    String hashTime = getMD5(System.currentTimeMillis());
			    String hashUser = getMD5(user.email);
			    
			    File destinationFile = new File(play.Play.application().path().toString() +
			    		"//public//profile//picture//" + hashUser +
			    		"//" + hashTime + fileName);
		    	FileUtils.copyFile(file, destinationFile);
		    	picturePath = "/" + hashUser + "/" + hashTime + fileName;
			}else{
				if(filledForm.field("picture").value() == null){
					picturePath = user.getProfile().getPicture();
				}else if(filledForm.get().getPicture().compareTo(BLANK_PIC) == 0){
					picturePath = BLANK_PIC;
				}
			}
			
			models.Profile.update(
					Mupi.getLocalUser(session()),
					filledForm.get().getFirstName(),
					filledForm.get().getLastName(),
					filledForm.get().getAbout(),
					filledForm.get().getBirthDate(),
					picturePath,
					filledForm.get().getGender(),
					filledForm.get().getLocations()
			);
				
			
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.profile.updated"));

			return redirect(routes.Feed.feed());
			
		} catch (IOException e){
			flash(Mupi.FLASH_ERROR_KEY, Messages.get("mupi.errorSendingFile"));
			e.printStackTrace();
			return redirect(routes.Profile.profile());
		}
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result changeLocation(Integer op, Long id){
		if(op == 0) return addLocation(id) ;
		else if(op == 1) return removeLocation(id);
		return AjaxResponse.build(1, "Server Error!");
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result addLocation(Long id){
		
		
		final User user = Mupi.getLocalUser(session());
		final models.Profile profile = user.profile;
		final Location location = Location.find.byId(id);
		
		if(location != null){
			if(profile.getLocations() != null && profile.getLocations().contains(location)){
				return AjaxResponse.build(2, "You already has this location registered!");
			}else{
				profile.getLocations().add(location);
				profile.update();
				return AjaxResponse.build(0, "Location successfully registered!");
			}
		}else{
			return AjaxResponse.build(1, "This location dos not exist in our database. If you want this location to be ther click in 'Suggest Location'!");
		}
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result removeLocation(Long id){
		final User user = Mupi.getLocalUser(session());
		final models.Profile profile = user.profile;
		final Location location = Location.find.byId(id);
				
		if(location != null){
			if(profile.getLocations() != null){
				profile.getLocations().remove(location);
				profile.update();
			}
			return AjaxResponse.build(0, "You have successfully removed this location!");
		}else{
			return AjaxResponse.build(2, "This location does not exist in our database!");
		}
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