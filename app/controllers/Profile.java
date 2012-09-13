package controllers;

import java.io.File;
import java.io.IOException;
import java.util.Date;
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
import views.html.profile;
import be.objectify.deadbolt.actions.Restrict;

public class Profile extends Controller {
	static String BLANK_PIC = "/blank_profile.jpg";
	
	private static final Form<models.Profile> PROFILE_FORM = form(models.Profile.class);
	
	@Restrict(Mupi.USER_ROLE)
	public static Result profile() {
		final User user = Mupi.getLocalUser(session());		

		models.Profile p = models.Profile.create(user);
		
		if(p == null)
			return Controller.badRequest();
		else{
			
			final Form<models.Profile> form = PROFILE_FORM.fill(p);
			final List<Location> notSelected = Location.find.all();
//			final String allLocationsJson = Location.getAllAsJsonArray();
			final List<Location> selected = p.locations;
			
			return ok(profile.render(form, selected, notSelected));
		}
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result doProfile() {
		final Form<models.Profile> filledForm = PROFILE_FORM.bindFromRequest();
		final User user = Mupi.getLocalUser(session());	
		
		System.out.println(filledForm);
		
		try {
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart picture = body.getFile("picture");
			String picturePath = BLANK_PIC;
			
			if (picture != null) {
			    String fileName = picture.getFilename();
			    File file = picture.getFile();
			    
			    //TODO: If we allow the user to change e-mail, we need to take care of it!!
			    File destinationFile = new File(play.Play.application().path().toString() + "//public//profilePictures//"
			        + user.email.hashCode() + "//" + fileName);
	
		    	FileUtils.copyFile(file, destinationFile);
		    	
		    	picturePath = "/" + user.email.hashCode() + "/" + fileName;
			}else{
				if(filledForm.field("picture").value() == null){
					picturePath = models.Profile.findByUserId(user.id).picture;
				}else if(filledForm.get().picture.compareTo(BLANK_PIC) == 0){
					picturePath = BLANK_PIC;
				}
			}
			
					
			
			models.Profile.update(
					Mupi.getLocalUser(session()),
					filledForm.get().firstName,
					filledForm.get().lastName,
					filledForm.get().about,
					filledForm.get().birthDate,
					picturePath,
					filledForm.get().gender,
					new Date(),
					filledForm.get().locations
			);
				
			
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.profile.updated"));

			return redirect(routes.Profile.profile());
			
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
		return badRequest();
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result addLocation(Long id){
		final User user = Mupi.getLocalUser(session());
		final models.Profile profile = models.Profile.findByUserId(user.id);
		final Location location = Location.find.byId(id);
		
		if(location != null){
			if(profile.locations != null && profile.locations.contains(location)){
				return ok("You already has this location registered!");
			}else{
				profile.locations.add(location);
				profile.update();
				return ok("Location successfully registered!");
			}
		}else{
			return badRequest("This location dos not exist in our database. If you want this location to be ther click in 'Suggest Location'!");
		}
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result removeLocation(Long id){
		final User user = Mupi.getLocalUser(session());
		final models.Profile profile = models.Profile.findByUserId(user.id);
		final Location location = Location.find.byId(id);
				
		if(location != null){
			if(profile.locations != null){
				profile.locations.remove(location);
				profile.update();
			}
			return ok("You have successfully removed this location!");
		}else{
			return play.mvc.Results.badRequest("This location dos not exist in our database. If you want this location to be ther click in 'Suggest Location'!");
		}
	}
	
}