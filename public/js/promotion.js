jQuery(function(){	
	jQuery('#promotion_comments textarea').autosize();
	
	
	jQuery('.sendComent').live('click', function(){
    	var i = jQuery("#selectedInterest").val();
		var l = jQuery("#selectedLocation").val();
    	jsRoutes.controllers.Feed.comment(
			i,l,
			encodeURIComponent(jQuery(this).parent().prev('textarea').val()),
			jQuery(this).attr('publication')
		).ajax(loadFeed(i,l))
    });

    var loadFeed = function(i, l){
    	return {
    		success : function(data) {
    			var response = data.split("||");
    			if(response[0] == 0) jQuery('#feeds').html(response[1]);
    			if(i != -1 && l != -1 ) loadEditor();
    		},
    		error : function() {
    			if (jQuery('.alert').size() == 0) {
    				jQuery('#content').next().prepend("<div class='alert alert-error'> </div>");
    			}
    			jQuery('.alert')
    				.addClass('alert-error')
    				.html("Server error, please try again later!");
    		}
    	}
    }
	
})






