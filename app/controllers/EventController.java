package controllers;

import models.Promotion;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import utils.Forms.Community;
import conf.MupiParams;


/**
 * Controller for Mupi and external Events
 * @author banduk
 *
 */
public class EventController extends Controller {

//  @Restrict(Mupi.USER_ROLE)
  public static Result events(Integer p){
    Long i = MupiSession.getLocalInterest();
    Long l = MupiSession.getLocalLocation();
        
    System.out.println("SHIPAW2:  " + i +"  " + l);
    
    return ok(views.html.promotions.render(
        models.Interest.find.all(),
        models.Location.find.all(),     
        new Form<Community>(Community.class).fill(new Community(i, l)),
        Promotion.findByInterestLocation(i, l, p, MupiParams.EVS_PER_PAGE, "date, time")
      ));
  }
  
//  @Restrict(Mupi.USER_ROLE)
  public static Result filterEvents(){
    Form<utils.Forms.Community> bindedForm = form(utils.Forms.Community.class).bindFromRequest();
    Long l = bindedForm.get().location;
    Long i = bindedForm.get().interest;
    
    System.out.println("SHIPAW:  " + i +"  " + l);
    
    MupiSession.setLocalInterest(i);
    MupiSession.setLocalLocation(l);
    
    return events(0);
  }
}
