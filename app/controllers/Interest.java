package controllers;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import controllers.Profile.ProfileForm;

import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.interestManager;
import views.html.addInterest;

public class Interest extends Controller {
	// TODO: Change the way we're getting this image
	static String BLANK_PIC = "interestPictures/blank_interest.gif";
	
	public static Result interestManager() {
		final User user = Mupi.getLocalUser(session());		
		final List<models.Interest> uInterests = user.interests;
		final List<models.Interest> allInterests = (List<models.Interest>)CollectionUtils.subtract(models.Interest.find.all(), uInterests);
		
		Form<models.Interest> form = INTEREST_FORM;
		
		return ok(interestManager.render(uInterests, allInterests, form));
	}
	
	
	public static Result addInterest() {
		final User user = Mupi.getLocalUser(session());		
		
		Form<models.Interest> form = INTEREST_FORM;

  		return ok(addInterest.render(form));
	}
	
	private static final Form<models.Interest> INTEREST_FORM = form(models.Interest.class);
	
	public static Result doUpdateInterests() {
		final User user = Mupi.getLocalUser(session());
		Form<models.Interest> form = INTEREST_FORM;
		final List<models.Interest> uInterests = user.interests;
		final List<models.Interest> allInterests = (List<models.Interest>)CollectionUtils.subtract(models.Interest.find.all(), uInterests);
		
		
		
		return ok(interestManager.render(uInterests, allInterests, form));
	}
	
	public static Result doAddInterest() {
		final User user = Mupi.getLocalUser(session());
		Form<models.Interest> form = INTEREST_FORM;
		
		try{
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart picture = body.getFile("picture");
			String picturePath = BLANK_PIC;
			
			final Form<models.Interest> filledForm = INTEREST_FORM.bindFromRequest();
			
			if (picture != null) {
			    String fileName = picture.getFilename();
			    File file = picture.getFile();
			    
			    //TODO: If we allow the user to change e-mail, we need to take care of it!!
			    File destinationFile = new File(play.Play.application().path().toString() + "//public//interestPictures//"
			        + filledForm.get().name.hashCode() + "//" + fileName);
	
		    	FileUtils.copyFile(file, destinationFile);
		    	
		    	picturePath = "interestPictures/" + filledForm.get().name.hashCode() + "/" + fileName;
			}else{
				picturePath = BLANK_PIC;
			}
			
			
			models.Interest.create(
				filledForm.get().name,
				picturePath,
				filledForm.get().description
			);
				
			
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.profile.updated"));
			
			final List<models.Interest> uInterests = user.interests;
			final List<models.Interest> allInterests = (List<models.Interest>)CollectionUtils.subtract(models.Interest.find.all(), uInterests);
			
			return ok(interestManager.render(uInterests, allInterests, form));
		} catch (IOException e){
			flash(Mupi.FLASH_ERROR_KEY, Messages.get("mupi.profile.errorParsingDate"));
			e.printStackTrace();
			final List<models.Interest> uInterests = user.interests;
			final List<models.Interest> allInterests = (List<models.Interest>)CollectionUtils.subtract(models.Interest.find.all(), uInterests);
			return ok(interestManager.render(uInterests, allInterests, form));
		}
	}
//	public static Result doProfile() {
//		final Form<ProfileForm> filledForm = PROFILE_FORM.bindFromRequest();
//		final User user = Mupi.getLocalUser(session());	
//		
//		try {
//			MultipartFormData body = request().body().asMultipartFormData();
//			FilePart picture = body.getFile("picture");
//			String picturePath = BLANK_PIC;
//			
//			if (picture != null) {
//			    String fileName = picture.getFilename();
//			    File file = picture.getFile();
//			    
//			    //TODO: If we allow the user to change e-mail, we need to take care of it!!
//			    File destinationFile = new File(play.Play.application().path().toString() + "//public//profilePictures//"
//			        + user.email.hashCode() + "//" + fileName);
//	
//		    	FileUtils.copyFile(file, destinationFile);
//		    	
//		    	picturePath = "/" + user.email.hashCode() + "/" + fileName;
//			}else{
//				if(filledForm.get().picture == null){
//					picturePath = models.Profile.findByUserId(user.id).picture;
//				}else if(filledForm.get().picture.compareTo(BLANK_PIC) == 0){
//					picturePath = BLANK_PIC;
//				}
//			}
//			
//			Date parsedDate = null;
//			if(filledForm.get().birthDate != null && filledForm.get().birthDate.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")){
//				parsedDate = format.parse(filledForm.get().birthDate);
//			}
//		    	
//			models.Profile.update(
//					Mupi.getLocalUser(session()),
//					filledForm.get().firstName,
//					filledForm.get().lastName,
//					filledForm.get().about,
//					parsedDate,
//					picturePath,
//					filledForm.get().gender,
//					new Date()
//			);
//				
//			
//			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.profile.updated"));
//
//			return redirect(routes.Profile.profile());
//			
//		} catch (ParseException e) {
//			flash(Mupi.FLASH_ERROR_KEY, Messages.get("mupi.profile.errorParsingDate"));
//			e.printStackTrace();
//			return redirect(routes.Profile.profile());
//		} catch (IOException e){
//			flash(Mupi.FLASH_ERROR_KEY, Messages.get("mupi.profile.errorParsingDate"));
//			e.printStackTrace();
//			return redirect(routes.Profile.profile());
//		}
//	}
}