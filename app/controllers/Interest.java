package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.AjaxResponse;
import views.html.interestManager;
import be.objectify.deadbolt.actions.Restrict;

public class Interest extends Controller {
	// TODO: Change the way we're getting this image
	static String BLANK_PIC = "interestPictures/blank_interest.gif";
	
	@Restrict(Mupi.USER_ROLE)
	public static Result interestManager() {
		final User user = Mupi.getLocalUser(session());	
		
		final List<models.Interest> uInterests = user.profile.interests;
		final List<models.Interest> allInterests = (List<models.Interest>) CollectionUtils.subtract(models.Interest.find.all(), uInterests);
		
		Form<models.Interest> form = INTEREST_FORM;
		
		return ok(interestManager.render(uInterests, allInterests, form));
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result checkInterest(Long id){
		final User user = Mupi.getLocalUser(session());
				
		if(models.Profile.checkInterest(user, id))
			return AjaxResponse.build(0,"The interest was added to your interests list!");
		else
			return AjaxResponse.build(1,"A problem occured and the interest was NOT added to your interests list!");
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result uncheckInterest(Long id){
		final User user = Mupi.getLocalUser(session());	
		if(models.Profile.uncheckInterest(user, id))
			return AjaxResponse.build(0,"The interest was removed from your interests list!");
		else
			return AjaxResponse.build(1, "A problem occured and the interest was NOT removed from your interests list!");
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result ignoreInterest(Long id){
		// TODO: IMPLEMENT
		return AjaxResponse.build(0,"The interest was ignored! You can find it by searching it by name!");
	}
	
	
	private static final Form<models.Interest> INTEREST_FORM = form(models.Interest.class);
	
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
			
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.interestManager.added"));
			
			final List<models.Interest> uInterests = user.profile.interests;
			final List<models.Interest> allInterests = (List<models.Interest>)CollectionUtils.subtract(models.Interest.find.all(), uInterests);
			
			return ok(interestManager.render(uInterests, allInterests, form));
		} catch (IOException e){
			flash(Mupi.FLASH_ERROR_KEY, Messages.get("mupi.profile.errorSendingFile"));
			e.printStackTrace();
			final List<models.Interest> uInterests = user.profile.interests;
			final List<models.Interest> allInterests = (List<models.Interest>)CollectionUtils.subtract(models.Interest.find.all(), uInterests);
			return ok(interestManager.render(uInterests, allInterests, form));
		}
	}
}