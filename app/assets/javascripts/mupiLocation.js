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
		jsRoutes.controllers.Profile.suggestLocation(loc).ajax({
			success:function(data) {
				if (jQuery('.alert').size() == 0) {
					jQuery('.container').prepend(
							"<div class='alert'> </div>");
				}
				jQuery('.alert')
						.addClass('alert-success')
						.html(data);
			},
		    error:function(data) {
		    	if (jQuery('.alert').size() == 0) {
					jQuery('.container').prepend(
							"<div class='alert'> </div>");
				}
				jQuery('.alert')
						.addClass('alert-error')
						.html("Esta localização não está disponível no momento");
		    }
		})
		
	}
};

function removeLocation(id) {
	jsRoutes.controllers.Profile.changeLocation(1, id).ajax(succErrBuilder(
			function(){
				jQuery('#loc_'+id).parent().remove();
			}
	));
};