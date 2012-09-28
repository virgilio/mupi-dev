jQuery(function(){	
	jQuery(".comment_lnk").live('click', function (event){
		event.preventDefault();
		if(jQuery("#" + jQuery(this).attr("comments")).is(':visible'))
			jQuery("#" + jQuery(this).attr("comments")).slideUp('fast');
		else
	       jQuery("#" + jQuery(this).attr("comments")).slideDown('fast');
	       
	});

	jQuery("#open_publication_input").live('click', function(event){
		event.preventDefault();
		if(jQuery("#input_publication").is(':visible'))
			jQuery("#input_publication").slideUp('fast');
		else{
			jQuery("#input_publication").slideDown();
		}
    });
	
    jQuery('.btn_local').live('click', function(event){
    	event.preventDefault();
    	var i = jQuery("#selectedInterest").val();
    	if(jQuery(this).children('i').hasClass('icon-check')){
    		var l = -1;
            jQuery(this).children('i').removeClass('icon-check').addClass('icon-check-empty');
            jsRoutes.controllers.Feed.selectFeed(i, l).ajax(loadFeed(i,l));
        } else {
        	var l = jQuery(this).attr('id');
        	jQuery('.icon-check').removeClass('icon-check').addClass('icon-check-empty');
            jQuery(this).children('i').removeClass('icon-check-empty').addClass('icon-check');
            jsRoutes.controllers.Feed.selectFeed(i, l).ajax(loadFeed(i,l));
        };
    });
    
    jQuery('.interestIcon').live('click', function(event){
    	event.preventDefault();
    	jQuery('.selected_i').removeClass('selected_i');
    	jQuery(this).parent().addClass('selected_i');
    	var i =jQuery(this).attr('id');
    	var l = jQuery("#selectedLocation").val();
    	jsRoutes.controllers.Feed.selectFeed(i, l).ajax(loadFeed(i, l));
    });
    
    jQuery('.sendComent').live('click', function(event){
    	event.preventDefault();
    	var i = jQuery("#selectedInterest").val();
		var l = jQuery("#selectedLocation").val();
    	jsRoutes.controllers.Feed.comment(
			encodeURIComponent(jQuery(this).parent().prev('textarea').val()),
			jQuery(this).attr('publication')
		).ajax(loadFeed(i,l))
    });
    
    jQuery('#btn_confirm_publication').live('click', function(event){
    	event.preventDefault();
    	var i = jQuery("#selectedInterest").val();
    	var l = jQuery("#selectedLocation").val();
    	jsRoutes.controllers.Feed.publish(
			encodeURIComponent(jQuery('#body_publication').val())
    	).ajax(loadFeed(i,l));
    });

    jQuery("#btn_publish").live('click', function(event){
    	event.preventDefault();
    	jQuery("#preview_publication > .modal-body").html(jQuery("#body_publication").val());
    });
    
    
    
    	
    
    jQuery('.textarea_comment textarea').live('focus', function(){
    	jQuery(this).autosize();
    });
    jQuery('.textarea_comment textarea').live('focus', function(){
    	jQuery(this).next('.btns_comment').slideDown();
    });
    jQuery('.enter_cancel').live('click', function(){
    	jQuery(this).parent().hide(0,function(){
    		jQuery(this).siblings('textarea').val('').resize();
    	});
    	
    });

    var loadFeed = function(i, l){
    	return {
    		success : function(data) {
    			var response = data.split("||");
    			if(response[0] == 0) jQuery('#feed_content').html(response[1]);
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
    
    var findByName = function(src, n) {
	  for (var i = 0; i < src.length; i++) {
	    if (src[i].name === n) {
	      return src[i].value;
	    }
	  }
	}
        
})






