package security;

import org.springframework.context.ApplicationContext;

import models.User;
import play.api.Application;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Http.Context;
import be.objectify.deadbolt.AbstractDeadboltHandler;
import be.objectify.deadbolt.DynamicResourceHandler;
import be.objectify.deadbolt.models.RoleHolder;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUserIdentity;

import controllers.Mupi;
import controllers.routes;

public class MyDeadboltHandler extends AbstractDeadboltHandler {

	@Override
	public Result beforeRoleCheck(Http.Context context) {
		if (PlayAuthenticate.isLoggedIn(context.session())) {
			// user is logged in
			return null;
		} else {
			// user is not logged in

			// call this if you want to redirect your visitor to the page that
			// was requested before sending him to the login page
			// if you don't call this, the user will get redirected to the page
			// defined by your resolver
//			final String originalUrl = PlayAuthenticate.storeOriginalUrl(context);

//			Http.Context.current().flash().put("error", Messages.get("playauthenticate.handler.loginfirst", originalUrl));
			
//			Context.current().flash().put("error", Messages.get("playauthenticate.handler.loginfirst", originalUrl));
//			context = new Context(request,null, null);
//			context.flash().put("error", Messages.get("playauthenticate.handler.loginfirst", originalUrl));
			
			return redirect(routes.Mupi.index());
		}
	}

	@Override
	public RoleHolder getRoleHolder(final Http.Context context) {
		final AuthUserIdentity u = PlayAuthenticate.getUser(context);
		// Caching might be a good idea here
		return User.findByAuthUserIdentity(u);
	}

	@Override
	public DynamicResourceHandler getDynamicResourceHandler(
			final Http.Context context) {
		return null;
	}

	@Override
	public Result onAccessFailure(final Http.Context context,
			final String content) {
		// if the user has a cookie with a valid user and the local user has
		// been deactivated/deleted in between, it is possible that this gets
		// shown. You might want to consider to sign the user out in this case.
		return forbidden("Forbidden");
	}
}
