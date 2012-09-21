package controllers;

import java.util.ArrayList;
import java.util.List;

import models.PubComment;
import models.Publication;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import utils.AjaxResponse;
import views.html.feedHelpers.feedContent;
import views.html.feedHelpers.feedPublications;
import be.objectify.deadbolt.actions.Restrict;

public class Feed extends Controller {
	
	@Restrict(Mupi.USER_ROLE)
	public static Result feed(){
		final User user = Mupi.getLocalUser(session());
		
		if(!user.profile.interests.isEmpty())
			return ok(views.html.feed.render(
				user, 
				user.profile.interests, 
				user.profile.locations, 
				null, 
				null,
				Publication.findByInterests(getInterestIds(user.profile.interests), 0).getList()
			));
		else 
			return Interest.interestManager();
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result comment(String body, Long id){
		
		System.out.println(id + "   " + body);
		
		final models.Profile p = Mupi.getLocalUser(session()).profile;
		final models.Publication pub = models.Publication.find.byId(id);
		
		if(pub != null){
			PubComment.create(pub, p, body);
			return ok();
		}
		else{
			return badRequest();
		}
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result publish(String body, Long interest, Long location){
		final User user = Mupi.getLocalUser(session());
		final models.Interest i = models.Interest.find.byId(interest);
		final models.Location l = models.Location.find.byId(location);
		
		Publication.create(user.profile, l, i, 0, body);
		return AjaxResponse.build(0, feedPublications.render(
				Publication.findByInterestLocation(i.id, l.id, 0).getList(), i,	l
		).body());
	}
	
	@Restrict(Mupi.USER_ROLE)
	public static Result renderFeedContent(
			int status, 
			List<models.Publication> l_pubs,
			models.Interest selectedInterest,
			models.Location selectedLocation){
		return AjaxResponse.build(status, feedContent.render(l_pubs, selectedInterest, selectedLocation).body());
	}
		
	@Restrict(Mupi.USER_ROLE)
	public static Result selectFeed(Long interest, Long location){
		final models.Profile p = Mupi.getLocalUser(session()).profile;
		if(interest == null || interest == -1){
			if(location == null || location == -1){
				return renderFeedContent(
						0, 
						Publication.findByInterests(getInterestIds(p.interests), 0).getList(), 
						null, 
						null
				);
			} else {
				final models.Location l = models.Location.find.byId(location);
				return renderFeedContent(
						0, 
						Publication.findByInterestsLocation(getInterestIds(p.interests), location, 0).getList(), 
						null, 
						l
				);
			}
		} else {
			final models.Interest i = models.Interest.find.byId(interest);
			
			if(location == null || location == -1){
				return renderFeedContent(
						0, 
						Publication.findByInterest(interest, 0).getList(), 
						i, 
						null
				);
			} else {
				final models.Location l = models.Location.find.byId(location);
				return renderFeedContent(
						0, 
						Publication.findByInterestLocation(interest, location, 0).getList(), 
						i, 
						l
				);
			}
		}
	}	
	
	private static List<Long> getInterestIds(List<models.Interest> i){
		ArrayList<Long> ids = new ArrayList<Long>();
		for (models.Interest interest : i) {
			ids.add(interest.id);
		}
		return ids;
	}
}