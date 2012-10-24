package controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.imageio.ImageIO;

import models.Location;
import models.User;

import org.apache.commons.io.FileUtils;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

import conf.MupiParams;

import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Results;
import utils.AjaxResponse;
import utils.ImageHandler;
import views.html.profile;
import be.objectify.deadbolt.actions.Restrict;

public class Profile extends Controller {

	private static final Form<models.Profile> PROFILE_FORM = form(models.Profile.class);

	@Restrict(Mupi.USER_ROLE)
	public static Result profile() {
		final User user = Mupi.getLocalUser(session());

		models.Profile p = user.getProfile();
		final Form<models.Profile> form = PROFILE_FORM.fill(p);

		final List<Location> notSelected = Location.find.fetch("id", "name").findList();

		return ok(profile.render(form, p, notSelected));
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result doProfile() {
		final Form<models.Profile> filledForm = PROFILE_FORM.bindFromRequest();
		final User user = Mupi.getLocalUser(session());


		try {
			MultipartFormData body = request().body().asMultipartFormData();
			FilePart picture = body.getFile("picture");

			String picturePath = MupiParams.BLANK_PIC;

			if (picture != null) {
				String fileName = picture.getFilename();
				File file = picture.getFile();
				int i = (fileName.toLowerCase()).lastIndexOf('.');
				String extension = "png";

				if(i > 0 && i < fileName.length() - 1){
					extension = fileName.substring(i + 1).toLowerCase();
				}

				String hashTime = getMD5(System.currentTimeMillis());
				String hashUser = getMD5(user.email);

				File destinationFile = new File(MupiParams.PROFILE_ROOT + MupiParams.PIC_ROOT + "//" +
					hashUser + "//" + hashTime + fileName);
				FileUtils.copyFile(file, destinationFile);
				picturePath = "/" + hashUser + "/" + hashTime + fileName;

				File thumb = new File(MupiParams.PROFILE_ROOT + MupiParams.PIC_THUMB+ "//" +
					hashUser + "//" + hashTime + fileName);
				thumb.mkdirs();
				BufferedImage bi = ImageHandler.createSmallProfile(destinationFile);
				ImageIO.write(bi, extension, thumb);

				File medium = new File(MupiParams.PROFILE_ROOT + MupiParams.PIC_MEDIUM + "//" +
					hashUser + "//" + hashTime + fileName);
				medium.mkdirs();
				bi = ImageHandler.createMediumSquare(destinationFile);
				ImageIO.write(bi, extension, medium);

			}else{
				picturePath = user.getProfile().getPicture();
			}

			models.Profile.update(
				Mupi.getLocalUser(session()),
				filledForm.get().getFirstName(),
				filledForm.get().getLastName(),
				filledForm.get().getAbout(),
				filledForm.get().getBirthDate(),
				picturePath,
				filledForm.get().getGender(),
            filledForm.get().getNotificationLevel(),
				filledForm.get().getLocations()
				);


			flash(Mupi.FLASH_MESSAGE_KEY, Messages.get("mupi.profile.updated"));
			if (user.getProfile().getStatus() == conf.MupiParams.FIRST_LOGIN) {
				models.Profile.changeStatus(user.getProfile(), conf.MupiParams.ALL_HELPS);
				return redirect(routes.Interest.interestManager());
			}
			else {
				return redirect(routes.Profile.profile());
			}

		} catch (IOException e){
			flash(Mupi.FLASH_ERROR_KEY, Messages.get("mupi.errorSendingFile"));
			e.printStackTrace();
			return redirect(routes.Profile.profile());
		}
	}

	@Restrict(Mupi.USER_ROLE)
  public static Result suggestLocation(String city){
    final User u = Mupi.getLocalUser(session());
    final models.Profile p = u.getProfile();
    String lastName = p.getLastName();
    if(lastName == null) lastName = "";

    final String subject = p.getFirstName() + " " + lastName + " sugeriu uma cidade!!  Yayyy!!";

    final String body = "[Cidade][Sugestão] O usuário " + p.getFirstName() + " " + lastName + " (" + u.email + ") " +
        "sugeriu que a cidade " + city + " seja inserida ao banco de dados!";

    MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
    mail.setSubject( subject );
    mail.addRecipient(MupiParams.LOCATION_SUGGESTION_EMAIL);
    mail.addFrom("noreply@mupi.me");
    mail.setReplyTo("noreply@mupi.me");
    mail.send( body );

    return  Results.ok("Esta localização não está disponível no momento, assim que estiver entraremos em contato");
  }

	@Restrict(Mupi.USER_ROLE)
	public static Result changeLocation(Integer op, Long id){
	  System.out.println(op + "   " + id);
	  
		if(op == 0) return addLocation(id) ;
		else if(op == 1) return removeLocation(id);
		return AjaxResponse.build(1, "Server Error!");
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result addLocation(Long id){


		final User user = Mupi.getLocalUser(session());
		final models.Profile profile = user.profile;
		final Location location = Location.find.byId(id);

		if(location != null){
			if(profile.getLocations() != null && profile.getLocations().contains(location)){
				return AjaxResponse.build(2, "Você já adicionou essa cidade!");
			}else{
				profile.getLocations().add(location);
				profile.update();
				return AjaxResponse.build(0, "Cidade adicionada com sucesso!");
			}
		}else{
			return AjaxResponse.build(1, "Essa cidade ainda não está registrada");
		}
	}

	@Restrict(Mupi.USER_ROLE)
	public static Result removeLocation(Long id){
		final User user = Mupi.getLocalUser(session());
		final models.Profile profile = user.profile;
		final Location location = Location.find.byId(id);

		if(location != null){
			if(profile.getLocations() != null){
				profile.getLocations().remove(location);
				profile.update();
			}
			return AjaxResponse.build(0, "Cidade removida!");
		}else{
			return AjaxResponse.build(2, "Você não tem essa ciadde!");
		}
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
