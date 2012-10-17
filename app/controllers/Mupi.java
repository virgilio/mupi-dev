package controllers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.Promotion;
import models.User;
import models.NotificationBucket;
import play.Routes;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.Result;
import play.mvc.Results;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import views.html.about;
import views.html.contact;
import views.html.help;
import views.html.index;
import views.html.media;
import views.html.privacyPolicies;
import views.html.signup;
import views.html.statistics;
import views.html.terms;
import views.html.promotion;
import views.html.publicationSingle;
import be.objectify.deadbolt.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;

import conf.MupiParams;

public class Mupi extends Controller {

  public static final String FLASH_MESSAGE_KEY = "message";
  public static final String FLASH_ERROR_KEY = "error";
  public static final String FLASH_WARNING_KEY = "warning";
  public static final String USER_ROLE = "user";
  public static final String ADMIN_ROLE = "admin";

  public static Result at(final String path){
    final File file = new File(MupiParams.UPLOAD_ROOT + "//" + path);

    if (!file.exists()) {
      return notFound();
    }else{
      if(file.isDirectory())
        return notFound();
      else
        return ok(file);
    }
  }

  public static Result index() {
    return redirect(routes.Feed.feed());
  }

  public static User getLocalUser(final Session session) {
    final User localUser = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session));
    return localUser;
  }

  public static Result login() {
    return redirect(routes.Feed.feed());
  }

  public static Result doLogin() {
    final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM.bindFromRequest();
    if (filledForm.hasErrors()) { // User did not fill everything properly
      return badRequest(index.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, MyUsernamePasswordAuthProvider.SIGNUP_FORM));
    } else { // Everything was filled
      return UsernamePasswordAuthProvider.handleLogin(ctx());
    }
  }

  public static Result jsRoutes() {
    return ok(
        Routes.javascriptRouter(
            "jsRoutes",
            controllers.routes.javascript.Signup.forgotPassword(),
            controllers.routes.javascript.Interest.checkInterest(),
            controllers.routes.javascript.Interest.uncheckInterest(),
            controllers.routes.javascript.Interest.ignoreInterest(),
            controllers.routes.javascript.Profile.changeLocation(),
            controllers.routes.javascript.Feed.publish(),
            controllers.routes.javascript.Feed.selectFeed(),
            controllers.routes.javascript.Feed.comment(),
            controllers.routes.javascript.Feed.promote(),
            controllers.routes.javascript.Feed.commentPublication(),
            controllers.routes.javascript.Feed.nextPublications(),
            controllers.routes.javascript.Feed.nextPromotions(),
            controllers.routes.javascript.Feed.refreshPublications(),
            controllers.routes.javascript.Profile.suggestLocation(),
            controllers.routes.javascript.Mupi.clearBucket()
        )).as("text/javascript");
  }

  public static Result signup() {
    final User user = getLocalUser(session());
    if(user == null){
      return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
    }
    else{
      flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.signup.already_logged"));
      return redirect(routes.Feed.feed());
    }
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
  
  public static Result about() {
    return ok(about.render());
  }

  public static Result contact() {
    return ok(contact.render());
  }

  public static Result help() {
    return ok(help.render());
  }

  public static Result promotion(Long id) {
      final User user = getLocalUser(session());
      if(user != null){
	  //System.out.println("Reset bucket of: " + user.getProfile().getFirstName());
          NotificationBucket.setNotified((models.Promotion.find.byId(id)).getPublication(), user.getProfile());
      }
      return ok(promotion.render(models.Promotion.find.byId(id)));
      
  }
    
  public static Result publication(Long id) {
      final User user = getLocalUser(session());
      if(user != null){
        //System.out.println("Reset bucket of: " + user.getProfile().getFirstName());
        NotificationBucket.setNotified(models.Publication.find.byId(id), user.getProfile());
      }
      models.Publication pub = models.Publication.find.byId(id);
      if(pub.getPub_typ() == models.Publication.EVENT)
        return promotion(Promotion.getByPublicationId(id).getId());
      else
        return ok(publicationSingle.render(pub));
  }
    
  public static Result media() {
    return ok(media.render());
  }

  public static Result privacyPolicies() {
    return ok(privacyPolicies.render());
  }

  public static Result statistics() {
    return ok(statistics.render());
  }

  public static Result terms() {
    return ok(terms.render());
  }

  @Restrict(Mupi.USER_ROLE)
  public static Result configuration() {
    final User user = getLocalUser(session());
    return ok(views.html.configuration.render(user));
  }

  @Restrict(Mupi.USER_ROLE)
  public static Result inbox() {
    final User user = getLocalUser(session());
    return ok(views.html.inbox.render(user));
  }

  @Restrict(Mupi.USER_ROLE)
  public static Result notifications() {
    final User user = getLocalUser(session());
    return ok(views.html.notifications.render(user, NotificationBucket.getBucket(user.profile)));
  }
  
  @Restrict(Mupi.USER_ROLE)
  public static Result clearBucket() {
    final User user = getLocalUser(session());
    NotificationBucket.setAllNotified(user.getProfile());
    return ok();
  }
}
