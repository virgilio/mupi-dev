package controllers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import models.User;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.profile;

public class Profile extends Controller {
	static SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
	static String BLANK_PIC = "/blank_profile.jpg";
	
	public static class ProfileForm{
		public String firstName;
		public String lastName;
		public String birthDate;
		public String picture;
		public String about;
		public Integer gender;
		
		public ProfileForm(){}
		
		public ProfileForm(models.Profile profile){
			this.firstName = profile.firstName;
			this.lastName = profile.lastName;
			this.picture = profile.picture;
			this.about = profile.about;
			this.gender = profile.gender;
			
			if(profile.birthDate != null)
				this.birthDate= format.format(profile.birthDate);
			
		}
	}
	
	private static final Form<ProfileForm> PROFILE_FORM = form(ProfileForm.class);
	
	public static Result profile() {
		final User user = Mupi.getLocalUser(session());		

		Form<ProfileForm> form = PROFILE_FORM;

			models.Profile p = models.Profile.create(user);
			if(p == null)
				return Controller.badRequest();
			else
				form = PROFILE_FORM.fill(new ProfileForm(p));
				return ok(profile.render(form));
//		}
	}
	
	public static Result doProfile() {
		final Form<ProfileForm> filledForm = PROFILE_FORM.bindFromRequest();
		final User user = Mupi.getLocalUser(session());	
		
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
				if(filledForm.get().picture == null){
					picturePath = models.Profile.findByUserId(user.id).picture;
				}else if(filledForm.get().picture.compareTo(BLANK_PIC) == 0){
					picturePath = BLANK_PIC;
				}
			}
			
			Date parsedDate = null;
			if(filledForm.get().birthDate != null && filledForm.get().birthDate.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")){
				parsedDate = format.parse(filledForm.get().birthDate);
			}
		    	
			models.Profile.update(
					Mupi.getLocalUser(session()),
					filledForm.get().firstName,
					filledForm.get().lastName,
					filledForm.get().about,
					parsedDate,
					picturePath,
					filledForm.get().gender,
					new Date()
			);
				
			
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.profile.updated"));

			return redirect(routes.Profile.profile());
			
		} catch (ParseException e) {
			flash(Mupi.FLASH_ERROR_KEY, Messages.get("mupi.errorParsingDate"));
			e.printStackTrace();
			return redirect(routes.Profile.profile());
		} catch (IOException e){
			flash(Mupi.FLASH_ERROR_KEY, Messages.get("mupi.errorSendingFile"));
			e.printStackTrace();
			return redirect(routes.Profile.profile());
		}
	}
}