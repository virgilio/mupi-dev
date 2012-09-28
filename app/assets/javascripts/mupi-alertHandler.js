function succErrBuilder(f){
	return({
		success : function(data) {
			var response = data.split("||");
			if (jQuery('.alert').size() == 0) {
				jQuery('#content').prepend("<div class='alert'> </div>");
			}
			var messageType = 'warning';
			if (response[0] == 0) messageType = 'success';
			else if(response[0] == 1) messageType = 'error';
			
			jQuery('.alert').addClass('alert-'+messageType).html(response[1]);
			if(f) f(response);
		},
		error : function() {
			if (jQuery('.alert').size() == 0) {
				jQuery('#content').next().prepend("<div class='alert alert-error'> </div>");
			}
			jQuery('.alert')
				.addClass('alert-error')
				.html("Server error, please try again later!");
		}
	})
}