jQuery(function(){	
	
	
	jQuery(".comment_lnk").live('click', function (event){
		event.preventDefault();
		if(jQuery("#" + jQuery(this).attr("comments")).is(':visible'))
			jQuery("#" + jQuery(this).attr("comments")).slideUp('fast');
		else
			jQuery("#" + jQuery(this).attr("comments")).slideDown('fast');

	});


	jQuery('#sendComment').live('click', function(event){
		event.preventDefault();
		jsRoutes.controllers.Feed.commentPublication(
				encodeURIComponent(jQuery(this).parent().prev('textarea').val()),
				jQuery(this).attr('publication')
		).ajax(loadComments());
		jQuery(this).parent().prev('textarea').val("");
	});

	var loadComments = function(){
		return {
			success : function(data) {
				var response = data.split("||");
				if(response[0] == 0) jQuery('#comments').html(decodeURIComponent(response[1]));
				jQuery("#textToSend").val("");
				jQuery("#textToSend").resize();
				jQuery("a[rel=clickover]").clickover();
			},
			error : function() {
				jQuery('.page-alert').html("<div class='alert hide'> </div>");
				jQuery('.page-alert > .alert')
				.addClass('alert-error')
				.html("Erro no servidor, por favor tente novamente mais tarde");
			}
		}
	}


	jQuery('.textarea_comment textarea').live('focus', function(){
		jQuery(this).autosize();
	});	
})






