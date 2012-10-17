package controllers;

import models.User;
import be.objectify.deadbolt.actions.Restrict;
import be.objectify.deadbolt.actions.RoleHolderPresent;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;

import play.data.Form;
import play.data.format.Formats.NonEmpty;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthUser;
import views.html.account.*;

public class Account extends Controller {

	public static class Accept {

		@Required
		@NonEmpty
		public Boolean accept;

	}

	public static class PasswordChange {
		@MinLength(5)
		@Required
		public String password;

		@MinLength(5)
		@Required
		public String repeatPassword;

		public String validate() {
			if (password == null || !password.equals(repeatPassword)) {
				return Messages
						.get("playauthenticate.change_password.error.passwords_not_same");
			}
			return null;
		}
	}
	
	private static final Form<Account.PasswordChange> PASSWORD_CHANGE_FORM = form(Account.PasswordChange.class);

	@Restrict(Mupi.USER_ROLE)
	public static Result verifyEmail() {
		final User user = Mupi.getLocalUser(session());
		if (user.status == 1) {
			// E-Mail has been validated already
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("playauthenticate.verify_email.error.already_validated"));
		} else if(user.email != null && !user.email.trim().isEmpty()){
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get( "playauthenticate.verify_email.message.instructions_sentto", user.email));
			MyUsernamePasswordAuthProvider.getProvider().sendVerifyEmailMailingAfterSignup(user, ctx());
			
		} else {
			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get( "playauthenticate.verify_email.error.set_email_first", user.email));
		}
		return redirect(routes.Feed.feed());
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result changePassword() {
		final User u = Mupi.getLocalUser(session());

		if (u.status == 0) {
			return ok(unverified.render());
		} else {
			return ok(password_change.render(PASSWORD_CHANGE_FORM));
		}
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result doChangePassword() {
		final Form<Account.PasswordChange> filledForm = PASSWORD_CHANGE_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not select whether to link or not link
			return badRequest(password_change.render(filledForm));
		} else {
			final User user = Mupi.getLocalUser(session());
			final String newPassword = filledForm.get().password;
			user.changePassword(new MyUsernamePasswordAuthUser(newPassword),
					true);
			flash(Mupi.FLASH_MESSAGE_KEY,
					Messages.get("playauthenticate.change_password.success"));
			return redirect(routes.Profile.profile());
		}
	}
}
