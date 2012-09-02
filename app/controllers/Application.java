package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.User;
import play.Routes;
import play.api.templates.Html;
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

public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";

	public static Result index() {
		return ok(index.render());
	}

	/**
	 * TODO Home controller
	 * @return
	 */
	public static Result home(){
		return ok(main.render("Home", "nav",
				new Html("<h1>This is the user home page</h1>" +
						"<p>Here we gonna have feed and menu options</p>" +
						"<p>Only for authenticated users</p>")));
	}

	/**
	 * 
	 * TODO Interests controller
	 * @return
	 */
	public static Result interests(){
		return ok(main.render("Home", "nav",
				new Html("<h1>This is the user interests page</h1>" +
						"<p>Here we gonna have feed and menu options</p>" +
						"<p>Only for authenticated users</p>")));
	}

	/**
	 * TODO config controller
	 * @return
	 */
	public static Result config(){
		return ok(main.render("Home", "nav",
				new Html("<h1>This is the user config page</h1>" +
						"<p>Here we gonna have configuration and menu options</p>" +
						"<p>Only for authenticated users</p>")));
	}

	/**
	 * TODO config controller
	 * @return
	 */
	public static Result wizard(){
		return ok(main.render("Home", "nav",
				new Html("<h1>This is the user wizard page</h1>" +
						"<p>Here the user chooses the first interests</p>" +
						"<p>Only for authenticated users</p>")));
	}
	
	public static User getLocalUser(final Session session) {
		final User localUser = User.findByAuthUserIdentity(PlayAuthenticate
				.getUser(session));
		return localUser;
	}

	@Restrict(Application.USER_ROLE)
	public static Result restricted() {
		final User localUser = getLocalUser(session());
		return ok(restricted.render(localUser));
	}

	@Restrict(Application.USER_ROLE)
	public static Result profile() {
		final User localUser = getLocalUser(session());
		return ok(profile.render(localUser));
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
						controllers.routes.javascript.Signup.forgotPassword()))
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
			return UsernamePasswordAuthProvider.handleSignup(ctx());
		}
	}

	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}

}