package controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	private static final Form<models.Interest> INTEREST_FORM = form(models.Interest.class);
	// TODO: Change the way we're getting this image
	static String BLANK_PIC = "/blank_interest.jpg";
	
	@Restrict(Mupi.USER_ROLE)
	public static Result interestManager() {
		final User user = Mupi.getLocalUser(session());	
		
		final List<models.Interest> uInterests = user.profile.interests;
		final List<models.Interest> allInterests = (List<models.Interest>) CollectionUtils.subtract(models.Interest.find.all(), uInterests);
				
		final Form<models.Interest> form = INTEREST_FORM;
		
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
	
	
	
	
	public static Result doAddInterest() {
		final Form<models.Interest> filledForm = INTEREST_FORM.bindFromRequest();
		final User user = Mupi.getLocalUser(session());
		
		try{
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart picture = body.getFile("picture");
			String picturePath = BLANK_PIC;
			
			System.out.println(picture);
			
			if (picture != null) {
			    String fileName = picture.getFilename();
			    File file = picture.getFile();
			    
			    String hashTime = getMD5(System.currentTimeMillis());
			    String hashInterest = getMD5(filledForm.get().name);
			    
			    File destinationFile = new File(play.Play.application().path().toString() +
			    		"//public//interest//picture//" + hashInterest +
			    		"//" + hashTime + fileName);
		    	FileUtils.copyFile(file, destinationFile);
		    	picturePath = "/" + hashInterest + "/" + hashTime + fileName;
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
			
			return interestManager();
		} catch (IOException e){
			flash(Mupi.FLASH_ERROR_KEY, Messages.get("mupi.profile.errorSendingFile"));
			e.printStackTrace();
			final List<models.Interest> uInterests = user.profile.interests;
			final List<models.Interest> allInterests = (List<models.Interest>)CollectionUtils.subtract(models.Interest.find.all(), uInterests);
			return interestManager();
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