import java.util.Arrays;

import models.SecurityRole;
import models.User;
import play.mvc.Http.Session;

import be.objectify.deadbolt.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.Mupi;
import controllers.routes;

import play.Application;
import play.GlobalSettings;
import play.mvc.Call;
import views.html.index;
import views.html.wizard;

public class Global extends GlobalSettings{

	public static final String USER_ROLE = "user";
	
	public void onStart(Application app) {
		PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
				return routes.Mupi.login();
			}

			
			@Override
			/**
			 * Intercept a redirection after an authorization
			 */
			public Call afterAuth() {
				// If the user has already registered at least one interest, go to profile
				if (Mupi.hasInterests())
					return routes.Profile.profile();
				// Otherwise go to the interests wizard
				else
					return routes.Mupi.wizard();
			}

			@Override
			public Call afterLogout() {
				return routes.Mupi.index();
			}

			@Override
			public Call auth(final String provider) {
				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				return com.feth.play.module.pa.controllers.routes.Authenticate
						.authenticate(provider);
			}

			@Override
			public Call askMerge() {
				return routes.Account.askMerge();
			}

			@Override
			public Call askLink() {
				return routes.Account.askLink();
			}

			@Override
			public Call onException(final AuthException e) {
				if (e instanceof AccessDeniedException) {
					return routes.Signup
							.oAuthDenied(((AccessDeniedException) e)
									.getProviderKey());
				}

				// more custom problem handling here...
				return super.onException(e);
			}
		});

		initialData();
	}

	private void initialData() {
		if (SecurityRole.find.findRowCount() == 0) {
			for (final String roleName : Arrays
					.asList(controllers.Mupi.USER_ROLE)) {
				final SecurityRole role = new SecurityRole();
				role.roleName = roleName;
				role.save();
			}
		}
	}
}