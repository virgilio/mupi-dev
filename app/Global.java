import java.util.Arrays;
import java.util.HashMap;

import models.Location;
import models.SecurityRole;
import play.Application;
import play.GlobalSettings;
import play.mvc.Call;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import views.html.pageNotFound;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;

public class Global extends GlobalSettings{
	public static final String USER_ROLE = "user";

	public void onStart(Application app) {
		PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
				return routes.Feed.feed();
			}

			@Override
			/**
			 * Intercept a redirection after an authorization
			 */
			public Call afterAuth() {
					return routes.Feed.feed();
			}

			@Override
			public Call afterLogout() {
				return routes.Feed.feed();
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
			  return routes.Feed.feed();
			}

			@Override
			public Call askLink() {
				return routes.Feed.feed();
			}

			@Override
			public Call onException(final AuthException e) {
				return super.onException(e);
			}			
		});
		
				initialData();
	}

	@Override
  public Result onHandlerNotFound(RequestHeader request) {
	  // TODO: Inform to the notFound page which was the address tried 
	  // It will be easy at play 2.1
	  // LOOK AT: https://play.lighthouseapp.com/projects/82401-play-20/tickets/382-messages-in-onhandlernotfound-globalsettings
    return Results.ok(views.html.pageNotFound.render());
  }
	
	private void initialData() {
		if (SecurityRole.find.findRowCount() == 0) {
			for (final String roleName : Arrays.asList(controllers.Mupi.USER_ROLE, controllers.Mupi.ADMIN_ROLE)) {
				final SecurityRole role = new SecurityRole();
				role.roleName = roleName;
				role.save();
			}
		}
		Location.create("Campinas (SP)", "");
		Location.create("São Paulo (SP)", "");
	}
}
