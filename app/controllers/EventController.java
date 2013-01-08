package controllers;

import be.objectify.deadbolt.actions.Restrict;
import models.NotificationBucket;
import models.Promotion;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import utils.AjaxResponse;
import utils.Forms.Community;
import conf.MupiParams;
import conf.MupiParams.PubType;
import utils.AssyncEmailSender;

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

		return ok(views.html.promotions.render(
				models.Interest.find.all(),
				models.Location.find.all(),     
				new Form<Community>(Community.class).fill(new Community(i, l)),
				Promotion.findByInterestLocation(i, l, p, MupiParams.EVS_PER_PAGE, "date, time"),
				 MyUsernamePasswordAuthProvider.LOGIN_FORM,
	        MyUsernamePasswordAuthProvider.SIGNUP_FORM
				));
	}

	//  @Restrict(Mupi.USER_ROLE)
	public static Result demand(Integer p){
		Long i = MupiSession.getLocalInterest();
		Long l = MupiSession.getLocalLocation();

		return ok(views.html.demand.render(
				models.Interest.find.all(),
				models.Location.find.all(),     
				new Form<Community>(Community.class).fill(new Community(i, l)),
				Promotion.findByPubType(p, MupiParams.DEMANDS_PER_PAGE, "created", PubType.DEMAND),
				MyUsernamePasswordAuthProvider.LOGIN_FORM,
				MyUsernamePasswordAuthProvider.SIGNUP_FORM
				));
	}

	//  @Restrict(Mupi.USER_ROLE)
	public static Result filterEvents(){
		Form<utils.Forms.Community> bindedForm = form(utils.Forms.Community.class).bindFromRequest();
		Long l = bindedForm.get().location;
		Long i = bindedForm.get().interest;

		MupiSession.setLocalInterest(i);
		MupiSession.setLocalLocation(l);

		return events(0);
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result demandTeachMeetUp(Long id) {
		final User u = Mupi.getLocalUser(session());
		final models.Profile p = u.profile;
		String lastName = p.getLastName() != null ? p.getLastName() : "";
		Promotion prom = Promotion.find.byId(id);

		//		if (prom.getSubscribers().contains(u)) {
		//			return AjaxResponse.build(4, "Você já se inscreveu neste Evento!");
		//		} else {
		try {
			if (prom.getPublication().getPub_typ().compareTo(PubType.DEMAND) == 0) {
				final String subject = "[Professor][EventoMupi] " + prom.getTitle();
				final String body = "O usuário " + p.getFirstName() + " " + lastName
						+ " (" + u.email + ") quer participar como professor deste evento!";
				final String from = "noreply@mupi.me";
				final String to = MupiParams.SUBSCRIBE_TO_MEETUP_EMAIL;
				final String replyTo = "noreply@mupi.me";
	
				new AssyncEmailSender(subject, body, from, replyTo, to).send();
	
				final String userSubject = "Confirmação de interesse em: "
						+ prom.getTitle();
				final String userBody = ""
						+ "Olá "
						+ p.getFirstName()
						+ ",\n\n"
						+ "Seu interesse em participar na realização de " + prom.getTitle() + " foi enviado. Em breve entraremos em contato com mais detalhes.\n\n\n"
						+ "Atenciosamente,\n" + "Equipe Mupi";
				final String userFrom = "contato@mupi.me";
				final String userTo = u.email;
				final String userReplyTo = "contato@mupi.me";
	
				new AssyncEmailSender(userSubject, userBody, userFrom, userReplyTo,
						userTo).send();
	//			NotificationBucket.updateBucketWithoutNotify(prom.getPublication(), p);
				return AjaxResponse
						.build(
								0,
								"Sua interesse foi enviado. Logo entraremos em contato para mais informações.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return AjaxResponse
				.build(
						1,
						"Erro ao confirmar seu interesse. Entre em contato com contato@mupi.me");
		//		else {
		//			models.Promotion.subscribeToEvent(u, prom);
		//			NotificationBucket.updateBucketWithoutNotify(prom.getPublication(), p);
		//			return AjaxResponse.build(0,
		//					"Acompanhe na página do evento quem também confirmou presença!");
		//		}
		//		}
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result demandLearnMeetUp(Long id) {
		final User u = Mupi.getLocalUser(session());
		final models.Profile p = u.profile;
		String lastName = p.getLastName() != null ? p.getLastName() : "";
		Promotion prom = Promotion.find.byId(id);

		//		if (prom.getSubscribers().contains(u)) {
		//			return AjaxResponse.build(4, "Você já se inscreveu neste Evento!");
		//		} else {
		try {
			if (prom.getPublication().getPub_typ().compareTo(PubType.DEMAND) == 0) {
				final String subject = "[Participante][EventoMupi] " + prom.getTitle();
				final String body = "O usuário " + p.getFirstName() + " " + lastName
						+ " (" + u.email + ") quer participar deste evento como aluno!";
				final String from = "noreply@mupi.me";
				final String to = MupiParams.SUBSCRIBE_TO_MEETUP_EMAIL;
				final String replyTo = "noreply@mupi.me";
	
				new AssyncEmailSender(subject, body, from, replyTo, to).send();
	
				final String userSubject = "Confirmação de interesse em: "
						+ prom.getTitle();
				final String userBody = ""
						+ "Olá "
						+ p.getFirstName()
						+ ",\n\n"
						+ "Seu interesse na realização de " + prom.getTitle() + " foi enviado. Em breve entraremos em contato com mais detalhes.\n\n\n"
						+ "Atenciosamente,\n" + "Equipe Mupi";
				final String userFrom = "contato@mupi.me";
				final String userTo = u.email;
				final String userReplyTo = "contato@mupi.me";
	
				new AssyncEmailSender(userSubject, userBody, userFrom, userReplyTo,
						userTo).send();
	//			NotificationBucket.updateBucketWithoutNotify(prom.getPublication(), p);
				return AjaxResponse
						.build(
								0,
								"Sua interesse foi enviado. Logo entraremos em contato para mais informações.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return AjaxResponse
				.build(
						1,
						"Erro ao confirmar seu interesse. Entre em contato com contato@mupi.me");
		//		else {
		//			models.Promotion.subscribeToEvent(u, prom);
		//			NotificationBucket.updateBucketWithoutNotify(prom.getPublication(), p);
		//			return AjaxResponse.build(0,
		//					"Acompanhe na página do evento quem também confirmou presença!");
		//		}
		//		}
	}
}
