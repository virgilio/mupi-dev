package controllers;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Promotion;
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
import providers.MyUsernamePasswordAuthProvider;
import utils.AjaxResponse;
import views.html.index;
import views.html.feedHelpers.feedContent;
import views.html.mupiHelpers.*;
import be.objectify.deadbolt.actions.Restrict;
import com.typesafe.plugin.*;


public class Feed extends Controller {
	
	private static final Form<utils.MeetUpPromotion> PROMOTE_MEETUP_FORM = form(utils.MeetUpPromotion.class);
	private static final Form<utils.MeetUpHosting> HOST_MEETUP_FORM = form(utils.MeetUpHosting.class);
	
	@Restrict(Mupi.USER_ROLE)
	public static Result hostMeetUp(){
		final Form<utils.MeetUpPromotion> filledForm = PROMOTE_MEETUP_FORM.bindFromRequest();
		final User u = Mupi.getLocalUser(session());
		final models.Profile p = u.getProfile();
		String lastName = p.getLastName();
		if(lastName == null) lastName = "";
		
		final String subject = p.getFirstName() + " " + lastName + " quer organizar um encontro amiguinhos!  Yayyy!!";
		
		final String body = "O usuário " + p.getFirstName() + " " + lastName + " " +
						   "quer organizar um encontro na seguinte comunidade:\n" + 
						   "\n    Localidade - " + models.Location.find.byId(filledForm.get().location).getName() +
						   "\n\n Ele redigiu a seguinte descrição para o encontro:\n" +
						   filledForm.get().description;
		
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		mail.setSubject( subject );
		mail.addRecipient("banduk@gmail.com");
		mail.addFrom("noreply@mupi.me");
		mail.setReplyTo("noreply@mupi.me");
		mail.send( body );
		
		return feed(filledForm.get().interest,null);
	}
	
	
	@Restrict(Mupi.USER_ROLE)
	public static Result promoteMeetUp(){
		final Form<utils.MeetUpHosting> filledForm = HOST_MEETUP_FORM.bindFromRequest();
		final User u = Mupi.getLocalUser(session());
		final models.Profile p = u.getProfile();
		String lastName = p.getLastName();
		if(lastName == null) lastName = "";
		
		final String subject = p.getFirstName() + " " + lastName + " quer receber encontros amiguinhos!  Yayyy!!";
		
		final String body = "O usuário " + p.getFirstName() + " " + lastName + " " +
						   "quer receber encontros na seguinte localidade:\n" + 
						   "\n    Interesse  - " + models.Interest.find.byId(filledForm.get().interest).getName() +
						   "\n    Localidade - " + models.Location.find.byId(filledForm.get().location).getName() +
						   "\n\n Ele redigiu a seguinte descrição de seu local:\n" +
						   filledForm.get().description;
		
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		mail.setSubject( subject );
		mail.addRecipient("banduk@gmail.com");
		mail.addFrom("noreply@mupi.me");
		mail.setReplyTo("noreply@mupi.me");
		mail.send( body );
		
		return feed(filledForm.get().interest,filledForm.get().location);
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result feed(){
		final User user = Mupi.getLocalUser(session());
		
		if(user == null || user.profile == null){
			return ok(index.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, MyUsernamePasswordAuthProvider.SIGNUP_FORM));
		}else if(user.getProfile().getInterests().isEmpty()){
			flash(Messages.get("mupi.profile.noInterests"));
			return redirect(routes.Interest.interestManager());
		}
		else{
			return ok(views.html.feed.render(
				user, 
				user.getProfile().getInterests(), 
				user.getProfile().getLocations(), 
				null, 
				null,
				Publication.findByInterests(getInterestIds(user.getProfile().getInterests()), 0).getList(),
				Promotion.findByInterests(getInterestIds(user.getProfile().getInterests()), 0).getList(),
				form(models.Promotion.class),
				PROMOTE_MEETUP_FORM,
				HOST_MEETUP_FORM
			));
		}
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result feed(Long interest, Long location){
		final User user = Mupi.getLocalUser(session());
		
		models.Interest i; if(interest == null) i = null; else i=models.Interest.find.byId(interest);
		models.Location l; if(location == null) l = null; else l=models.Location.find.byId(location);
		
		if(user == null || user.profile == null){
			return ok(index.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, MyUsernamePasswordAuthProvider.SIGNUP_FORM));
		}else if(user.getProfile().getInterests().isEmpty()){
			flash(Messages.get("mupi.profile.noInterests"));
			return redirect(routes.Interest.interestManager());
		}
		else{
			return ok(views.html.feed.render(
				user, 
				user.getProfile().getInterests(), 
				user.getProfile().getLocations(), 
				i, l,
				Publication.findByInterests(getInterestIds(user.getProfile().getInterests()), 0).getList(),
				Promotion.findByInterests(getInterestIds(user.getProfile().getInterests()), 0).getList(),
				form(models.Promotion.class),
				PROMOTE_MEETUP_FORM,
				HOST_MEETUP_FORM
			));
		}
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result comment(Long i, Long l,String body, Long id){
		final User u = Mupi.getLocalUser(session());
		final models.Profile p = u.profile;
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
	public static Result commentPromotion(Long i, Long l,String body, Long id){
		final models.Profile p = Mupi.getLocalUser(session()).profile;
		final models.Publication pub = models.Publication.find.byId(id);
		
		if(pub != null){
			PubComment.create(pub, p, body);
			List<PubComment> reverseComments = pub.getComments();
			
			Collections.reverse(reverseComments);
			return AjaxResponse.build(
					0, 
					comments.render(reverseComments).body()
			);
		}
		else{
			return AjaxResponse.build(
					1, 
					null
			);
		}
	}
	
	
	@Restrict(Mupi.USER_ROLE)
	public static Result publish(String body, Long i, Long l){
		final User u = Mupi.getLocalUser(session());
		final models.Profile p = u.profile;
		final models.Interest iObj = models.Interest.find.byId(i);
		final models.Location lObj = models.Location.find.byId(l);
		
		Publication.create(p, lObj, iObj, 0, body);
		return selectFeed(i,l);
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result renderFeedContent(
			int status, 
			List<models.Publication> l_pubs,
			List<models.Promotion> l_prom,
			models.Interest selectedInterest,
			models.Location selectedLocation){
		return AjaxResponse.build(status, feedContent.render(l_pubs, l_prom, selectedInterest, selectedLocation, form(models.Promotion.class), PROMOTE_MEETUP_FORM,HOST_MEETUP_FORM).body());
	}
		
	@Restrict(Mupi.USER_ROLE)
	public static Result selectFeed(Long interest, Long location){
		final models.Profile p = Mupi.getLocalUser(session()).profile;
		if(interest == null || interest == -1){
			if(location == null || location == -1){
				return renderFeedContent(
						0, 
						Publication.findByInterests(getInterestIds(p.getInterests()), 0).getList(),
						Promotion.findByInterests(getInterestIds(p.getInterests()), 0).getList(),
						null, 
						null
				);
			} else {
				final models.Location l = models.Location.find.byId(location);
				return renderFeedContent(
						0, 
						Publication.findByInterestsLocation(getInterestIds(p.getInterests()), location, 0).getList(),
						Promotion.findByInterestsLocation(getInterestIds(p.getInterests()), location, 0).getList(),
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
						Promotion.findByInterest(interest, 0).getList(),
						i, 
						null
				);
			} else {
				final models.Location l = models.Location.find.byId(location);
				return renderFeedContent(
						0,
						Publication.findByInterestLocation(interest, location, 0).getList(),
						Promotion.findByInterestLocation(interest, location, 0).getList(),
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
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart picture = body.getFile("picture");
		String picturePath = BLANK_EVT;
		
		DynamicForm bindedForm = form().bindFromRequest();
		final models.Interest iObj = models.Interest.find.byId(Long.valueOf(bindedForm.get("int")));
		final models.Location lObj = models.Location.find.byId(Long.valueOf(bindedForm.get("loc")));

		try {
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
			
			final Form<models.Promotion> filledForm = form(models.Promotion.class).bindFromRequest();
			final models.Profile p = Mupi.getLocalUser(session()).profile;
			
			models.Promotion.create(
					p,
					lObj,
					iObj,
					filledForm.get().getTitle(), 
					filledForm.get().getAddress(), 
					filledForm.get().getDate(), 
					filledForm.get().getTime(), 
					filledForm.get().getDescription(), 
					filledForm.get().getLink(),
					picturePath);
			
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.promotion.created"));

			return feed();
			
		}catch (Exception e) {
			flash(Mupi.FLASH_ERROR_KEY, "Erro ao divulgar evento, por favor contate-nos para que possamos resolver este problema.");
			return feed();
		}
	}
	
	private static List<Long> getInterestIds(List<models.Interest> i){
		ArrayList<Long> ids = new ArrayList<Long>();
		for (models.Interest interest : i) {
			if(interest.getStatus() > 0)
				ids.add(interest.getId());
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