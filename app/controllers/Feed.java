package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import models.Promotion;
import models.PubComment;
import models.Publication;
import models.User;
import org.apache.commons.io.FileUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import utils.AjaxResponse;
import utils.ImageHandler;
import views.html.index;
import views.html.feedHelpers.feedContent;
import views.html.feedHelpers.pubList;
import views.html.feedHelpers.promList;
import views.html.mupiHelpers.comments;
import be.objectify.deadbolt.actions.Restrict;

import com.avaje.ebean.Page;
import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

import conf.MupiParams;


public class Feed extends Controller {
  private static final Form<utils.MeetUpPromotion> PROMOTE_MEETUP_FORM = form(utils.MeetUpPromotion.class);
  private static final Form<utils.MeetUpHosting> HOST_MEETUP_FORM = form(utils.MeetUpHosting.class);
  private static final Form<models.Promotion> PROMOTION_FORM = form(models.Promotion.class);

  @Restrict(Mupi.USER_ROLE)
  public static Result resetFeed(){
    session().put("interest", "-1");
    session().put("location", "-1");
    return redirect(routes.Feed.feed());
  }


  
  public static Result feed(){
    final User user = Mupi.getLocalUser(session());

    if(user == null || user.profile == null){
      return ok(index.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, MyUsernamePasswordAuthProvider.SIGNUP_FORM));
    }else if(user.getProfile().getInterests().isEmpty()){
      return redirect(routes.Interest.interestManager());
    }
    else if(user.getProfile().getStatus() == models.Profile.FIRST_LOGIN){
      models.Profile.changeStatus(user.getProfile(),models.Profile.REGULAR);
      return redirect(routes.Profile.profile());
    }
    else{
      Long interest = getLocalInterest();
      Long location = getLocalLocation();

      models.Interest i; if(interest == null) i = null; else i=models.Interest.find.byId(interest);
      models.Location l; if(location == null) l = null; else l=models.Location.find.byId(location);

      return ok(views.html.feed.render(
          user, 
          user.getProfile().getInterests(), 
          user.getProfile().getLocations(), 
          i, l,
          Publication.findByInterests(getInterestIds(user.getProfile().getInterests()), new Long(0)),
          Promotion.findByInterests(getInterestIds(user.getProfile().getInterests()), new Long(0)),
          form(models.Promotion.class),
          PROMOTE_MEETUP_FORM,
          HOST_MEETUP_FORM
          ));
    }
  }


  @Restrict(Mupi.USER_ROLE)
  public static Result hostMeetUp(){
    final Form<utils.MeetUpHosting> filledForm = HOST_MEETUP_FORM.bindFromRequest();
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.getProfile();
    String lastName = p.getLastName(); if(lastName == null) lastName = "";
    String interest = ""; 
    
    if(getLocalInterest() != null && getLocalInterest() != -1)
      interest = models.Interest.find.byId(getLocalInterest()).getName();

    final String subject = p.getFirstName() + " " + lastName + " quer receber encontros amiguinhos!  Yayyy!!";

    final String body = "O usuário " + p.getFirstName() + " " + lastName + " (" + u.email + ") " +
        "quer receber encontros da seguinte comunidade:\n" + 
        "\n    Localidade - " + models.Location.find.byId(getLocalLocation()).getName() +
        "\n    Interesse - " + interest +
        "\n\n Ele redigiu a seguinte descrição:\n" +
        filledForm.get().description;

    MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
    mail.setSubject( subject );
    mail.addRecipient(MupiParams.HOST_MEETUP_EMAIL);
    mail.addFrom("noreply@mupi.me");
    mail.setReplyTo("noreply@mupi.me");
    mail.send( body );

    return redirect(routes.Feed.feed());
  }


  @Restrict(Mupi.USER_ROLE)
  public static Result promoteMeetUp(){
    final Form<utils.MeetUpPromotion> filledForm = PROMOTE_MEETUP_FORM.bindFromRequest();
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.getProfile();
    String lastName = p.getLastName();
    if(lastName == null) lastName = "";

    final String subject = p.getFirstName() + " " + lastName + " quer organizar um encontro amiguinhos!  Yayyy!!";

    final String body = "O usuário " + p.getFirstName() + " " + lastName + " (" + u.email + ") " +
        "quer organizar um encontro na seguinte comunidade:\n" + 
        "\n    Localidade - " + models.Location.find.byId(getLocalLocation()).getName() +
        "\n    Interesse - " + models.Interest.find.byId(getLocalInterest()).getName() +
        "\n\n Ele redigiu a seguinte descrição do evento:\n" +
        filledForm.get().description;

    MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
    mail.setSubject( subject );
    mail.addRecipient(MupiParams.PROMOTE_MEETUP_EMAIL);
    mail.addFrom("noreply@mupi.me");
    mail.setReplyTo("noreply@mupi.me");
    mail.send( body );

    return redirect(routes.Feed.feed());
  }


  @Restrict(Mupi.USER_ROLE)
  public static Result comment(String body, Long id){
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.profile;
    final models.Publication pub = models.Publication.find.byId(id);		

    if(pub != null)
      PubComment.create(pub, p, body);

    return selectFeed(getLocalInterest(), getLocalLocation());
  }

  @Restrict(Mupi.USER_ROLE)
  public static Result commentPromotion(String body, Long id){
    final models.Profile p = Mupi.getLocalUser(session()).profile;
    final models.Publication pub = models.Publication.find.byId(id);

    if(pub != null){
      PubComment.create(pub, p, body);
      List<PubComment> reverseComments = pub.getComments();

      Collections.reverse(reverseComments);
      return AjaxResponse.build(
          0, 
          comments.render(reverseComments).body()
          );
    }
    else{
      return AjaxResponse.build(
          1, 
          null
          );
    }
  }


  @Restrict(Mupi.USER_ROLE)
  public static Result publish(String body){
    Long i = getLocalInterest();
    Long l = getLocalLocation();
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.profile;

    Publication.create(
        p,
        models.Location.find.byId(l),
        models.Interest.find.byId(i),
        models.Publication.PUBLICATION,
        body);

    return selectFeed(i,l);
  }
  
  @Restrict(Mupi.USER_ROLE)
  public static Result refreshPublications(Long first){
    final models.Profile p = Mupi.getLocalUser(session()).profile;
    Long i = getLocalInterest();
    Long l = getLocalLocation();
    models.Interest iObj = null;
    models.Location lObj = null;
    if(i != null) iObj = models.Interest.find.byId(i);
    if(l != null) lObj= models.Location.find.byId(l);
    List<models.Publication> pubs = new ArrayList<models.Publication>();
    
    if(i == null || i == -1){
      if(l == null || l == -1){pubs = Publication.findNewerByInterests(getInterestIds(p.getInterests()), first).getList();}
      else {pubs = Publication.findNewerByInterestsLocation(getInterestIds(p.getInterests()), l, first).getList();}
    }
    else{
      if(l == null || l == -1){pubs = Publication.findNewerByInterest(i, first).getList();}
      else{pubs = Publication.findNewerByInterestLocation(i, l, first).getList();}
    }
    
    if(pubs.isEmpty()) 
      return AjaxResponse.build(2, "");
    else
      return AjaxResponse.build(0, pubList.render(pubs, iObj, lObj).body());
  }
  
  @Restrict(Mupi.USER_ROLE)
  public static Result nextPublications(Long last){
    final models.Profile p = Mupi.getLocalUser(session()).profile;
    Long i = getLocalInterest();
    Long l = getLocalLocation();
    models.Interest iObj = null;
    models.Location lObj = null;
    if(i != null) iObj = models.Interest.find.byId(i);
    if(l != null) lObj= models.Location.find.byId(l);
    
    List<models.Publication> pubs = new ArrayList<models.Publication>();
    
    if(i == null || i == -1){
      if(l == null || l == -1){pubs = Publication.findByInterests(getInterestIds(p.getInterests()), last).getList();}
      else {pubs = Publication.findByInterestsLocation(getInterestIds(p.getInterests()), l, last).getList();}
    }
    else{
      if(l == null || l == -1){pubs = Publication.findByInterest(i, last).getList();}
      else{pubs = Publication.findByInterestLocation(i, l, last).getList();}
    }    
    
    if(pubs.isEmpty())
      return AjaxResponse.build(2, "");
    else
      return AjaxResponse.build(0, pubList.render(pubs, iObj, lObj).body());
  }
  
  
  @Restrict(Mupi.USER_ROLE)
  public static Result nextPromotions(Long last){
    final models.Profile p = Mupi.getLocalUser(session()).profile;
    Long i = getLocalInterest();
    Long l = getLocalLocation();
    Integer status = 0;
    List<models.Promotion> proms = new ArrayList<models.Promotion>();
    
    if(i == null || i == -1){
      if(l == null || l == -1) proms = Promotion.findByInterests(getInterestIds(p.getInterests()), last).getList();
      else proms = Promotion.findByInterestsLocation(getInterestIds(p.getInterests()), l, last).getList();
    }else{
      if(l == null || l == -1) proms = Promotion.findByInterest(i, last).getList();
      else proms = Promotion.findByInterestLocation(i, l, last).getList();
    }
    
    if(proms.isEmpty()) status = 2;
    return AjaxResponse.build(status, promList.render(proms).body());
  }
  

  @Restrict(Mupi.USER_ROLE)
  public static Result renderFeedContent(
      int status, 
      Page<models.Publication> l_pubs,
      Page<models.Promotion> l_prom,
      models.Interest i,
      models.Location l){
	  
	final models.Profile p = Mupi.getLocalUser(session()).profile;
    final String iSession; if(i == null) iSession = "-1"; else iSession = i.getId().toString();
    final String lSession; if(l == null) lSession = "-1"; else lSession = l.getId().toString();

    session().put("interest", iSession);
    session().put("location", lSession);

    return AjaxResponse.build(status, feedContent.render(p.getLocations(), l_pubs, l_prom, i, l, PROMOTION_FORM, PROMOTE_MEETUP_FORM,HOST_MEETUP_FORM).body());
  }
  
  @Restrict(Mupi.USER_ROLE)
  public static Result selectFeed(Long interest, Long location){
    final models.Profile p = Mupi.getLocalUser(session()).profile;
    if(interest == null || interest == -1){
      List<Long> interests = getInterestIds(p.getInterests());
      if(location == null || location == -1){
        return renderFeedContent(
            0, 
            Publication.findByInterests(interests,new Long(0)),
            Promotion.findByInterests(interests, new Long(0)),
            null, 
            null
        );
      } else {
        final models.Location l = models.Location.find.byId(location);
        return renderFeedContent(
            0, 
            Publication.findByInterestsLocation(interests, location, new Long(0)),
            Promotion.findByInterestsLocation(interests, location, new Long(0)),
            null, 
            l
        );
      }
    } else {
      final models.Interest i = models.Interest.find.byId(interest);

      if(location == null || location == -1){
        return renderFeedContent(
            0,
            Publication.findByInterest(interest, new Long(0)), 
            Promotion.findByInterest(interest, new Long(0)),
            i, 
            null
        );
      } else {
        final models.Location l = models.Location.find.byId(location);
        return renderFeedContent(
            0,
            Publication.findByInterestLocation(interest, location, new Long(0)),
            Promotion.findByInterestLocation(interest, location, new Long(0)),
            i, 
            l
        );
      }
    }
  }	

  // TODO: Global?
  static String BLANK_EVT = "/blank_event.jpg";
  
  @Restrict(Mupi.USER_ROLE)
  public static Result promote(){
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart picture = body.getFile("picture");
    String picturePath = BLANK_EVT;

    DynamicForm bindedForm = form().bindFromRequest();
    Long i = getLocalInterest();
    Long l = getLocalLocation();
    models.Interest iObj = null;
    models.Location lObj = null;
    if(i != null) iObj = models.Interest.find.byId(i);
    if(l != null) lObj= models.Location.find.byId(l);

    final Form<models.Promotion> filledForm = form(models.Promotion.class).bindFromRequest();
    final models.Profile p = Mupi.getLocalUser(session()).profile;
    
    try {
      if (picture != null) {
        String fileName = picture.getFilename();
        File file = picture.getFile();
        int index = (fileName.toLowerCase()).lastIndexOf('.');
        String extension = "png";
        
        if(index > 0 && index < fileName.length() - 1){
          extension = fileName.substring(index + 1).toLowerCase();
        }
        
        String hashTime = getMD5(System.currentTimeMillis());
        String hashCommunity = getMD5(iObj.toString() + lObj.toString());

        File destinationFile = new File(play.Play.application().path().toString() +
            "//public//upload//event//picture//" + hashCommunity +
            "//" + hashTime + fileName);
        FileUtils.copyFile(file, destinationFile);
        picturePath = "/" + hashCommunity + "/" + hashTime + fileName;

        File medium = new File(play.Play.application().path().toString() +
            "//public//upload//event//picture//medium//" + hashCommunity +
            "//" + hashTime + fileName);
        medium.mkdirs();
        
        BufferedImage bi = ImageHandler.createSmallInterest(destinationFile);
        bi = ImageHandler.createMediumPromotion(destinationFile);
        ImageIO.write(bi, extension, medium);
      }else{picturePath = BLANK_EVT;}
    
      models.Promotion.create(
        p,
        lObj,
        iObj,
        filledForm.get().getTitle(), 
        filledForm.get().getAddress(), 
        filledForm.get().getDate(), 
        filledForm.get().getTime(), 
        filledForm.get().getDescription(), 
        filledForm.get().getLink(),
        picturePath);

      flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.promotion.created"));
      
      return redirect(routes.Feed.feed());
    }catch (Exception e) {
      flash(Mupi.FLASH_ERROR_KEY, "Erro ao divulgar evento, por favor contate-nos para que possamos resolver este problema.");
      return redirect(routes.Feed.feed());
    }
    
  }

  public static Long getLocalInterest(){
    String i = session().get("interest");
    if(i != null && !i.isEmpty())
      return Long.parseLong(i);
    else
      return null;
  }
  public static Long getLocalLocation(){
    String l = session("location");
    if(l != null && !l.isEmpty())
      return Long.parseLong(l);
    else
      return null;
  }


  private static List<Long> getInterestIds(List<models.Interest> i){
    ArrayList<Long> ids = new ArrayList<Long>();
    for (models.Interest interest : i) {
      if(interest.getStatus() > 0)
        ids.add(interest.getId());
    }
    return ids;
  }

  private static String getMD5(Object input){
    try {
      return new BigInteger(1, MessageDigest.getInstance("MD5")
          .digest(String.valueOf(input).getBytes())).toString(16);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }

  }


}