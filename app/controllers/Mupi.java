package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.User;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Session;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import views.html.*;

import be.objectify.deadbolt.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;

public class Mupi extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";
	public static final String ADMIN_ROLE = "admin";

	public static Result index() {
		return ok(index.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
	}

	/**
	 * TODO Home controller
	 * @return
	 */
	public static Result home(){
		return TODO;
//		return ok(main.render("Home", "nav",
//				new Html("<h1>This is the user home page</h1>" +
//						"<p>Here we gonna have feed and menu options</p>" +
//						"<p>Only for authenticated users</p>")));
	}

	/**
	 * 
	 * TODO Interests controller
	 * @return
	 */
	@Restrict(Mupi.USER_ROLE)
	public static Result interests(){
		return TODO;
//		return ok(main.render("Home", "nav",
//				new Html("<h1>This is the user interests page</h1>" +
//						"<p>Here we gonna have feed and menu options</p>" +
//						"<p>Only for authenticated users</p>")));
	}

	/**
	 * TODO config controller
	 * @return
	 */
	public static Result config(){
		return TODO;
//		return ok(main.render("Home", "nav",
//				new Html("<h1>This is the user config page</h1>" +
//						"<p>Here we gonna have configuration and menu options</p>" +
//						"<p>Only for authenticated users</p>")));
	}

	/**
	 * TODO config controller
	 * @return
	 */
	@Restrict(Mupi.USER_ROLE)
	public static Result wizard(){
		final User user = getLocalUser(session());
		return ok(views.html.wizard.render(user));
	}
	
	public static User getLocalUser(final Session session) {
		final User localUser = User.findByAuthUserIdentity(PlayAuthenticate
				.getUser(session));
		return localUser;
	}
	

	@Restrict(Mupi.USER_ROLE)
	public static boolean hasInterests() {
		final User localUser = getLocalUser(session());
		final int interests = localUser.interests.size();
		return interests > 0;
	}

	public static Result login() {
		return ok(login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
	}

	public static Result doLogin() {
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(login.render(filledForm));
		} else {
			// Everything was filled
			return UsernamePasswordAuthProvider.handleLogin(ctx());
		}
	}

	public static Result signup() {
		return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
	}

	public static Result jsRoutes() {
		return ok(
			Routes.javascriptRouter("jsRoutes",
					controllers.routes.javascript.Signup.forgotPassword(),
					controllers.routes.javascript.Interest.checkInterest(),
					controllers.routes.javascript.Interest.uncheckInterest(),
					controllers.routes.javascript.Interest.ignoreInterest(),
					controllers.routes.javascript.Profile.changeLocation()
			))
			.as("text/javascript");
	}

	public static Result doSignup() {
		final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(signup.render(filledForm));
		} else {
			// Everything was filled
			// do something with your part of the form before handling the user
			// signup
			
			Result ret = UsernamePasswordAuthProvider.handleSignup(ctx());

			// TODO: How to do an ACID operation here? if there's a problem on handleSignup we need 
			// to remove the profile. For now, resolved with the return of Profile.create
			if(models.Profile.create(getLocalUser(session())) == null)
				return Controller.badRequest();
			
			return ret;
		}
	}

	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}
	
	public static Result about(){
		return ok(about.render());
	}
	public static Result contact(){
		return ok(contact.render());
	}
	public static Result help(){
		return ok(help.render());
	}
	public static Result media(){
		return ok(media.render());
	}
	public static Result privacyPolicies(){
		return ok(privacyPolicies.render());
	}
	public static Result statistics(){
		return ok(statistics.render());
	}
	public static Result terms(){
		return ok(terms.render());
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result feed(){
		final User user = getLocalUser(session());
		return ok(views.html.feed.render(user));
	}
	@Restrict(Mupi.USER_ROLE)
	public static Result configuration(){
		final User user = getLocalUser(session());
		return ok(views.html.configuration.render(user));
	}
	@Restrict(Mupi.USER_ROLE)
	public static Result inbox(){
		final User user = getLocalUser(session());
		return ok(views.html.inbox.render(user));
	}
	@Restrict(Mupi.USER_ROLE)
	public static Result notifications(){
		final User user = getLocalUser(session());
		return ok(views.html.notifications.render(user));
	}

}