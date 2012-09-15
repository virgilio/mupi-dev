function registerLocation(allLocs) {
	var loc = jQuery("#locationSelector").val();
	jQuery("#locationSelector").val("");

	var exists = false;
	jQuery.each(allLocs, function(i, v) {
		if (v.name == loc) {
			jsRoutes.controllers.Profile.changeLocation(0, v.id).ajax(succErrBuilder(
				function(response){
					if(response[0] == 0){
						jQuery('.selectedLocations').append(
					      "<li class='selectedLocation'>"+
							"<span id='loc_" + v.id + "' >" + v.name + "</span>" +
							"<button location='" + v.id + "' class='removeLocation'>x</button>" +
						  "</li>"
						);
					}
				}
			));
			exists = true;
		}
	});
	if (!exists) {
		if (jQuery('.alert').size() == 0) {
			jQuery('.navbar-container').next().prepend(
					"<div class='alert'> </div>");
		}
		jQuery('.alert')
				.addClass('alert-error')
				.html("This location does not exist yet in the database! If you want to suggest it click on 'Suggest Location'");
	}
};

function removeLocation(id) {
	jsRoutes.controllers.Profile.changeLocation(1, id).ajax(succErrBuilder(
			function(){
				jQuery('#loc_'+id).parent().remove();
			}
	));
};