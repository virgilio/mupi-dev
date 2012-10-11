function registerLocation(location) {

	jsRoutes.controllers.Profile.changeLocation(0, location.id).ajax(succErrBuilder(
		function(response){
			if(response[0] == 0){
				jQuery('.selectedLocations').append(
			      "<li class='selectedLocation'>"+
					"<span id='loc_" + location.id + "' >" + location.text + "</span>" +
					"<button location='" + location.id + "' class='removeLocation'>x</button>" +
				  "</li>"
				);
			}
		}
	));
}

function suggestLocation(location){
	jsRoutes.controllers.Profile.suggestLocation(location.id).ajax({
		success:function(data) {
			jQuery('.page-alert').html("<div class='alert alert-success hide'> "+ data +"</div>");
		},
	    error:function(data) {
	    	jQuery('.page-alert').html("<div class='alert alert-error hide'> Esta localização não está disponível no momento</div>");
	    }
	})
}

function removeLocation(location) {
	jsRoutes.controllers.Profile.changeLocation(1, location).ajax(succErrBuilder(
		function(){
			jQuery('#loc_'+location).parent().remove();
		}
	));
};