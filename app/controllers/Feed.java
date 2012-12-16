package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import models.Promotion;
import models.PubComment;
import models.Publication;
import models.User;
import models.Profile.UserEmail;

import views.html.feedHelpers.feedContent;
import views.html.feedHelpers.promList;
import views.html.feedHelpers.pubList;
import views.html.mupiHelpers.comments;


import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHeaders;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import utils.AjaxResponse;
import utils.AssyncEmailSender;
import utils.ImageHandler;
import views.html.index;
import be.objectify.deadbolt.actions.Dynamic;
import be.objectify.deadbolt.actions.Restrict;

import com.avaje.ebean.Page;

import conf.MupiParams;
import conf.MupiParams.PubType;



public class Feed extends Controller {
  private static final Form<utils.Forms.MeetUpPromotion> PROMOTE_MEETUP_FORM = form(utils.Forms.MeetUpPromotion.class);
  private static final Form<utils.Forms.MeetUpHosting> HOST_MEETUP_FORM = form(utils.Forms.MeetUpHosting.class);
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
      session("ref", request().getHeader(HttpHeaders.REFERER));
      return ok(index.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, MyUsernamePasswordAuthProvider.SIGNUP_FORM));
    } else if(session("ref") != null && !session().get("ref").equalsIgnoreCase("null")) {
      String url = session("ref");
      session().remove("ref");
      return redirect(url);
    } else if(user.getProfile().getInterests().isEmpty()){
      return redirect(routes.Interest.interestManager());
    } else if(user.getProfile().getStatus() == MupiParams.FIRST_LOGIN){
      return redirect(routes.Profile.profile());
    }  else{
      Long interest = getLocalInterest();
      Long location = getLocalLocation();

      models.Interest i; if(interest == null) i = null; else i=models.Interest.find.byId(interest);
      models.Location l; if(location == null) l = null; else l=models.Location.find.byId(location);

      return ok(views.html.feed.render(
        user,
        user.getProfile().getInterests(),
        user.getProfile().getLocations(),
        i, l,
        Publication.findByInterests(models.Interest.getIds(user.getProfile().getInterests()), 0l),
        Promotion.findByInterests(models.Interest.getIds(user.getProfile().getInterests()), 0l),
        form(models.Promotion.class),
        PROMOTE_MEETUP_FORM,
        HOST_MEETUP_FORM
      ));
    }
  }



  @Dynamic("editor")
  public static Result hostMeetUp(){
    final Form<utils.Forms.MeetUpHosting> filledForm = HOST_MEETUP_FORM.bindFromRequest();
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.getProfile();
    String lastName = p.getLastName(); if(lastName == null) lastName = "";
    String interest = "--desconhecido--";
    String location = "--desconhecida--";

    if(getLocalInterest() != null && getLocalInterest() != -1)
      interest = models.Interest.find.byId(getLocalInterest()).getName();

    if(getLocalLocation() != null && getLocalLocation() != -1)
      location = models.Location.find.byId(getLocalLocation()).getName();

    final String subject = "[EventoMupi][Local] " + p.getFirstName() + " " + lastName + " quer receber encontros amiguinhos!  Yayyy!!";

    final String body = "O usuário " + p.getFirstName() + " " + lastName + " (" + u.email + ") " +
        "quer receber encontros da seguinte comunidade:\n" +
        "\n    Localidade - " + location +
        "\n    Interesse - " + interest +
        "\n\n Ele redigiu a seguinte descrição:\n" +
        filledForm.get().description;
    final String from = "noreply@mupi.me";
    final String to   = MupiParams.HOST_MEETUP_EMAIL;
    final String replyTo = "noreply@mupi.me";

    new AssyncEmailSender(subject, body, from, replyTo, to).send();

    final String userSubject = "Receber Evento Mupi";
    final String userBody    = "Olá " + p.getFirstName() + ",\n\n" +
        "Recebemos sua mensagem sobre o interesse em receber Eventos Mupi. Em breve entraremos em contato para os próximos passos.\n\n\n" +
            "Atenciosamente,\n" +
            "Equipe Mupi";
    final String userFrom = "contato@mupi.me";
    final String userTo   = u.email;
    final String userReplyTo = "contato@mupi.me";

    new AssyncEmailSender(userSubject, userBody, userFrom, userReplyTo, userTo).send();

    return redirect(routes.Feed.feed());
  }


  @Dynamic("editor")
  public static Result promoteMeetUp(){
    final Form<utils.Forms.MeetUpPromotion> filledForm = PROMOTE_MEETUP_FORM.bindFromRequest();
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.getProfile();
    String lastName = p.getLastName();
    if(lastName == null) lastName = "";

    String interest = "--desconhecido--";
    String location = "--desconhecida--";

    if(getLocalInterest() != null && getLocalInterest() != -1)
      interest = models.Interest.find.byId(getLocalInterest()).getName();

    if(getLocalLocation() != null && getLocalLocation() != -1)
      location = models.Location.find.byId(getLocalLocation()).getName();

    final String subject = "[EventoMupi][Sugestão] " + p.getFirstName() + " " + lastName + " quer organizar um encontro-mupi!";

    final String body = "O usuário " + p.getFirstName() + " " + lastName + " (" + u.email + ") " +
        "quer organizar um encontro na seguinte comunidade:\n" +
        "\n    Localidade - " +location +
        "\n    Interesse - " + interest +
        "\n\n Ele redigiu a seguinte descrição do evento:\n" +
        filledForm.get().description;
    final String from = "noreply@mupi.me";
    final String to   = MupiParams.PROMOTE_MEETUP_EMAIL;
    final String replyTo = "noreply@mupi.me";

    new AssyncEmailSender(subject, body, from, replyTo, to).send();


    final String userSubject = "Sugestão de Evento Mupi: ";
    final String userBody    = "Olá " + p.getFirstName() + ",\n\n" +
        "Recebemos sua mensagem sobre um Evento Mupi. Em breve entraremos em contato para os próximos passos.\n\n\n" +
            "Atenciosamente,\n" +
            "Equipe Mupi";
    final String userFrom = "contato@mupi.me";
    final String userTo   = u.email;
    final String userReplyTo = "contato@mupi.me";

    new AssyncEmailSender(userSubject, userBody, userFrom, userReplyTo, userTo).send();

    return redirect(routes.Feed.feed());
  }

  @Dynamic("editor")
  public static Result removeComment(Long id){
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.profile;

    if(models.PubComment.find.byId(id).getProfile().getId() == p.getId()){
      models.PubComment.uncomment(id);
      return AjaxResponse.build(0, "Cometário removido!");
    }else{
      return AjaxResponse.build(2, "Este comentário não é seu! Você não pode removê-lo");
    }
  }

  @Dynamic("editor")
  public static Result removePublication(Long id){
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.profile;

    Publication pub = models.Publication.find.byId(id);

    if(pub.getProfile().getId() == p.getId() && (
        pub.getPub_typ() == conf.MupiParams.PubType.DISCUSSION ||
        pub.getPub_typ() == conf.MupiParams.PubType.IDEA ||
        pub.getPub_typ() == conf.MupiParams.PubType.QUESTION
      )
    ){
      models.Publication.unpublish(id);
      return AjaxResponse.build(0, "Publicação removida!");
    } else if(pub.getProfile().getId() == p.getId() && pub.getPub_typ() == MupiParams.PubType.EVENT) {
      return AjaxResponse.build(2, "Esta publicação pertence à um evento! Você não pode removê-la!");
    } else if(pub.getProfile().getId() == p.getId() && pub.getPub_typ() == MupiParams.PubType.MUPI_EVENT) {
      return AjaxResponse.build(2, "Esta publicação pertence à um evento Mupi! Você não pode removê-la!");
    } else {
      return AjaxResponse.build(4, "Esta publicação não é sua! Você não pode removê-la");
    }
  }


  @Restrict(Mupi.USER_ROLE)
  public static Result comment(String body, Long id){
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.profile;
    final models.Publication pub = models.Publication.find.byId(id);

    String safeBody = Jsoup.clean(textWithLinks(body.replaceAll("(\r\n|\n)", " <br/> ")), Whitelist.none().addTags("br", "a").addAttributes("a", "href", "target"));

    if(pub != null)
      PubComment.create(pub, p, safeBody);
    List<UserEmail> l_ue = models.Profile.emailsFromPublication(pub);
    for(UserEmail ue : l_ue){
      if(u.getEmail().equalsIgnoreCase(ue.getEmail()))
        System.out.println("Commenter: " + ue.getEmail());
      else
        System.out.println(ue.getEmail());

    }
    return selectFeed(getLocalInterest(), getLocalLocation());
  }



  @Restrict(Mupi.USER_ROLE)
  public static Result commentPublication(String body, Long id){
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.profile;
    final models.Publication pub = models.Publication.find.byId(id);
    String safeBody = Jsoup.clean(textWithLinks(body.replaceAll("(\r\n|\n)", " <br/> ")), Whitelist.none().addTags("br", "a").addAttributes("a", "href", "target"));
    if(pub != null){
      PubComment.create(pub, p, safeBody);
      List<UserEmail> l_ue = models.Profile.emailsFromPublication(pub);
      for(UserEmail ue : l_ue){
        if(u.getEmail().equalsIgnoreCase(ue.getEmail()))
          System.out.println("Commenter: " + ue.getEmail());
        else
          System.out.println(ue.getEmail());

      }
      return AjaxResponse.build(
          0,
          comments.render(pub.getComments()).body()
          );
    }
    else{
      return AjaxResponse.build(
          1,
          null
          );
    }
  }

  @Dynamic("editor")
  public static Result publish(){
    Form<utils.Forms.PublicationBinder> bindedForm = form(utils.Forms.PublicationBinder.class).bindFromRequest();

    Long l = bindedForm.get().location;
    Long i = bindedForm.get().interest;

    if(i != null && l != null){
      final User u = Mupi.getLocalUser(session());
      final models.Profile p = u.profile;

      String safeBody = Jsoup.clean(
          bindedForm.get().body,
          Whitelist.basicWithImages().addEnforcedAttribute("a", "target", "_blank").addTags("h1", "h2")
      );

      Publication.create(
          p,
          models.Location.find.byId(l),
          models.Interest.find.byId(i),
          PubType.get(bindedForm.get().pub_typ),
          safeBody);
    }
    return selectFeed(getLocalInterest(),getLocalLocation());
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
      if(l == null || l == -1){pubs = models.Publication.findNewerByInterests(models.Interest.getIds(p.getInterests()), first).getList();}
      else {pubs = models.Publication.findNewerByInterestsLocation(models.Interest.getIds(p.getInterests()), l, first).getList();}
    }
    else{
      if(l == null || l == -1){pubs = models.Publication.findNewerByInterest(i, first).getList();}
      else{pubs = models.Publication.findNewerByInterestLocation(i, l, first).getList();}
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
      if(l == null || l == -1){pubs = Publication.findByInterests(models.Interest.getIds(p.getInterests()), last).getList();}
      else {pubs = Publication.findByInterestsLocation(models.Interest.getIds(p.getInterests()), l, last).getList();}
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
  public static Result nextPromotions(Long fromId){
    final models.Profile p = Mupi.getLocalUser(session()).profile;
    Long i = MupiSession.getLocalInterest();
    Long l = MupiSession.getLocalLocation();
    List<models.Promotion> proms = Promotion.findByInterestLocation(i, l, 0, 5, "date, time", fromId).getList();

    Integer status = 0;
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

    return AjaxResponse.build(status, feedContent.render(
        p.getLocations(),
        p.getInterests(),
        l_pubs,
        l_prom,
        i,
        l,
        PROMOTION_FORM,
        PROMOTE_MEETUP_FORM,
        HOST_MEETUP_FORM).body());
  }

  @Restrict(Mupi.USER_ROLE)
  public static Result selectFeed(Long i, Long l){
    final models.Profile p = Mupi.getLocalUser(session()).profile;

    return renderFeedContent(
        0,
        Publication.findByInterestLocation(i, l, 0, 10, "created desc"),
        Promotion.findByInterestLocation(i, l, 0, 5, "date, time"),
        models.Interest.find.byId(i),
        models.Location.find.byId(l)
    );
  }

  // TODO: Global?
  static String BLANK_EVT = "/blank_event.jpg";

  @Dynamic("editor")
  public static Result promote(){
    models.User u = Mupi.getLocalUser(session());
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart picture = body.getFile("picture");
    String picturePath = BLANK_EVT;

    DynamicForm bindedForm = form().bindFromRequest();
    Long i = getInterest(bindedForm.get("interest"));
    Long l = getLocation(bindedForm.get("location"));
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

        File destinationFile = new File(MupiParams.EVENT_ROOT + MupiParams.PIC_ROOT + "//" +
            hashCommunity + "//" + hashTime + fileName);
        FileUtils.copyFile(file, destinationFile);
        picturePath = "/" + hashCommunity + "/" + hashTime + fileName;

        File medium = new File(MupiParams.EVENT_ROOT + MupiParams.PIC_MEDIUM + "//" +
            hashCommunity + "//" + hashTime + fileName);
        medium.mkdirs();

        BufferedImage bi = ImageHandler.createSmallInterest(destinationFile);
        bi = ImageHandler.createMediumPromotion(destinationFile);
        ImageIO.write(bi, extension, medium);
      }else{picturePath = BLANK_EVT;}


      String safeDesc = Jsoup.clean(
          filledForm.get().getDescription(),
          Whitelist.simpleText()
      );


      Promotion pr = models.Promotion.create(
        p,
        lObj,
        iObj,
        filledForm.get().getTitle(),
        filledForm.get().getAddress(),
        filledForm.get().getDate(),
        filledForm.get().getTime(),
        safeDesc,
        filledForm.get().getLink(),
        picturePath,
        PubType.EVENT,
        new Integer(0),
        new Integer(0),
        new Double(0.0));

      flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.promotion.created"));

      List<UserEmail> l_ue = models.Profile.emailsFromPublication(pr.getPublication());
      for(UserEmail ue : l_ue){
        if(u.getEmail().equalsIgnoreCase(ue.getEmail()))
          System.out.println("Commenter: " + ue.getEmail());
        else
          System.out.println(ue.getEmail());

      }


      return redirect(routes.Feed.feed());
    }catch (Exception e) {
      flash(Mupi.FLASH_ERROR_KEY, "Erro ao divulgar evento, por favor contate-nos para que possamos resolver este problema.");
      //System.out.println(e.getMessage());
      e.printStackTrace();
      return redirect(routes.Feed.feed());
    }

  }

  public static Long getLocalInterest(){
    String i = session().get("interest");
    return getInterest(i);
  }

  public static Long getInterest(String i){
    if(i != null && !i.isEmpty())
      return Long.parseLong(i);
    else
      return null;
  }

  public static Long getLocalLocation(){
    String l = session("location");
    return getLocation(l);
  }

  public static Long getLocation(String l){
    if(l != null && !l.isEmpty())
      return Long.parseLong(l);
    else
      return null;
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

  private static String textWithLinks(String text) {
    String regex1 = "(?<!http://)(www[0-9]?\\.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    Pattern pattern1 = Pattern.compile(regex1);
    Matcher m1 = pattern1.matcher(text);
    StringBuffer sb1 = new StringBuffer(text.length());
    while(m1.find()){
      m1.appendReplacement(sb1, "http://"+ m1.group());
    }
    text = m1.appendTail(sb1).toString();

    String regex2 = "(https?://)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    Pattern pattern2 = Pattern.compile(regex2);
    Matcher m2 = pattern2.matcher(text);
    StringBuffer sb2 = new StringBuffer(text.length());
    while(m2.find()){
      m2.appendReplacement(sb2, "<a target=\"_blank\" href=\"" + m2.group() + "\">" + m2.group() + "</a>");
    }

    return m2.appendTail(sb2).toString();
  }
}
