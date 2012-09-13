var succErrMsg = {
	success : function(data) {
		if (jQuery('.alert').size() == 0) {
			jQuery('.navbar-container').next().prepend("<div class='alert alert-success'> </div>");
		}
		if (data)
			jQuery('.alert').attr('class', 'alert alert-success').html(data);
	},
	error : function() {
		if (jQuery('.alert').size() == 0) {
			jQuery('.navbar-container').next().prepend("<div class='alert-error'> </div>");
		}
		jQuery('.alert')
				.addClass('alert-error')
				.html("Server error, please try again later!");
	}
}

function registerLocation(allLocs) {
	var loc = jQuery("#locationSelector").val();

	var exists = false;
	jQuery.each(allLocs, function(i, v) {
		if (v.name == loc) {
			jsRoutes.controllers.Profile.changeLocation(0, v.id).ajax(succErrMsg);
		}
	});
	if (!exists) {
		if (jQuery('.alert').size() == 0) {
			jQuery('.navbar-container').next().prepend(
					"<div class='alert'> </div>");
		}
		jQuery('.alert')
				.addClass('alert-error')
				.html("<div class='alert alert-error'>This location does not exist yet in the database! If you want to suggest it click on 'Suggest Location' </div>");
	}
};

function removeLocation(id) {
	jsRoutes.controllers.Profile.changeLocation(1, id).ajax(succErrMsg);
};