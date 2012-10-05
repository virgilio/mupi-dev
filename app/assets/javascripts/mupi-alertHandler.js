jQuery(function(){
	jQuery('.page-alert').bind('DOMNodeInserted',function(event){
		jQuery(this).children().slideDown().delay(3000).slideUp('slow');
	});
	if (jQuery('.page-alert').children().length > 0 ){
		jQuery('.page-alert').children().slideDown().delay(3000).slideUp('slow');
	};
});




function succErrBuilder(f){
	return({
		success : function(data) {
			var response = data.split("||");
			var messageType = 'warning';
			if (response[0] == 0) messageType = 'success';
			else if(response[0] == 1) messageType = 'error';
			
			jQuery('.page-alert').html("<div class='alert hide alert-" +messageType + "'> " + response[1] + " </div>");
			if(f) f(response);
		},
		error : function() {
			jQuery('.page-alert').html("<div class='alert hide'> </div>");
			jQuery('.page-alert > .alert')
				.addClass('alert-error')
				.html("Erro no servidor, por favor tente novamente mais tarde");
		}
	})
}