package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.User;
import play.Routes;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import views.html.about;
import views.html.contact;
import views.html.help;
import views.html.index;
import views.html.login;
import views.html.media;
import views.html.privacyPolicies;
import views.html.signup;
import views.html.statistics;
import views.html.terms;
import be.objectify.deadbolt.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;

public class Mupi extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";
	public static final String ADMIN_ROLE = "admin";

	public static Result index() {
		// TODO If logged, redirect to the feed
		final User user = getLocalUser(session());
		if(user != null){
			return Feed.feed();
		} else {
			return ok(index.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, MyUsernamePasswordAuthProvider.SIGNUP_FORM));
		}
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

	public static User getLocalUser(final Session session) {
		final User localUser = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session));
		return localUser;
	}

	@Restrict(Mupi.USER_ROLE)
	public static boolean hasInterests() {
		final User localUser = getLocalUser(session());
		final int interests = localUser.profile.interests.size();
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
				controllers.routes.javascript.Profile.changeLocation(),
				controllers.routes.javascript.Feed.publish(),
				controllers.routes.javascript.Feed.selectFeed(),
				controllers.routes.javascript.Feed.comment(),
				controllers.routes.javascript.Feed.promote()
			)).as("text/javascript");
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
			return UsernamePasswordAuthProvider.handleSignup(ctx());
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
	public static Result promotion(){
		return ok(promotion.render());
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