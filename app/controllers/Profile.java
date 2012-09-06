package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.InterestUser;
import models.User;
import play.Routes;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Messages;
import play.mvc.*;
import play.mvc.Http.Session;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyIdentity;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import views.html.*;
import views.html.account.signup.password_forgot;

import be.objectify.deadbolt.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;

public class Profile extends Controller {
	
	private static final Form<models.Profile> PROFILE_FORM = form(models.Profile.class);

	
	public static Result profile() {
		final User user = Mupi.getLocalUser(session());		
		Form<models.Profile> form = PROFILE_FORM;
		models.Profile p = models.Profile.findByUserId(user.id);
		if( p != null ){
			form = PROFILE_FORM.fill(p);
			return ok(profile.render(form));
		}
		else{
			p = models.Profile.create(user);
			if(p == null)
				return Controller.badRequest();
			else
				form = PROFILE_FORM.fill(p);
				return ok(profile.render(form));
		}
	}
	
	public static Result doProfile() {
		final Form<models.Profile> filledForm = PROFILE_FORM.bindFromRequest();
		
		models.Profile.update(
				Mupi.getLocalUser(session()),
				filledForm.get().firstName,
				filledForm.get().lastName,
				filledForm.get().about,
				filledForm.get().birthDate,
				filledForm.get().picture,
				filledForm.get().gender,
				new Date(),
				new Date()
		);
		
		flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.profile.updated"));

		return redirect(routes.Profile.profile());
	}
}