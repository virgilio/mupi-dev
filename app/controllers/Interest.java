package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import controllers.Profile.ProfileForm;

import play.data.DynamicForm;
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
	
	public static Result checkInterest(Long id){
		System.out.println("SE");
		final User user = Mupi.getLocalUser(session());	
		User.checkInterest(user, id);
		return ok("The interest was added to your interests list!");
	}
	
	public static Result uncheckInterest(Long id){
		final User user = Mupi.getLocalUser(session());	
		User.uncheckInterest(user, id);
		return ok("The interest was removed from your interests list!");
	}
	public static Result ignoreInterest(Long id){
		return ok("The interest was ignored! You can find it by searching it by name!");
	}
	
	
	
//	public static class InterestsForm{
//		public ArrayList<Long> interests;
//	}
	
//	public static Result doUpdateInterests() {
//		final User user = Mupi.getLocalUser(session());
//		Form<InterestsForm> interestForm = form(InterestsForm.class).bindFromRequest();	
//		
//		for (Long interest : interestForm.get().interests) {
//			System.out.print("AEE : " + interest + ",  ");
//		}
//		
//		final List<models.Interest> uInterests = user.interests;
//		final List<models.Interest> allInterests = (List<models.Interest>)CollectionUtils.subtract(models.Interest.find.all(), uInterests);
//		
//		Form<models.Interest> form = INTEREST_FORM;
//		return ok(interestManager.render(uInterests, allInterests, form));
//	}
	
	private static final Form<models.Interest> INTEREST_FORM = form(models.Interest.class);
	
	public static Result addInterest() {
		final User user = Mupi.getLocalUser(session());		
		
		Form<models.Interest> form = INTEREST_FORM;

  		return ok(addInterest.render(form));
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
}