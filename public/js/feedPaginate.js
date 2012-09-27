jQuery(function(){	
	
	jQuery("#refresh_publications").click(function(event){
		event.preventDefault();
		if(jQuery('.all_publications .publication').size() == 0)
			jsRoutes.controllers.Feed.refreshPublications("-1").ajax(loadMorePubs('top'));
		else{
			jsRoutes.controllers.Feed.refreshPublications(jQuery('.publication:first').attr('pub_id')).ajax(
			  loadMorePubs('top')
			);
	    }
	});
	
	
	
	jQuery("#nextPublications").click(function(event){
		event.preventDefault();
		if(jQuery('.all_publications .publication').size() == 0)
			jsRoutes.controllers.Feed.nextPublications("-1").ajax(loadMorePubs('bottom'));
		else{
			jsRoutes.controllers.Feed.nextPublications(jQuery('.publication:last').attr('pub_id')).ajax(
			  loadMorePubs('bottom')
			);
	    }
	});

    var loadMorePubs = function(position){
    	return {
    		success : function(data) {
    			var response = data.split("||");
    			if(response[0] == 0) {
    				
    				if(position == 'bottom')
    					jQuery(response[1]).hide().insertAfter('.publication:last').slideDown('fast');
    				else if(position == 'top')
    					jQuery(response[1]).hide().insertBefore('.publication:first').slideDown('fast');
    			}
    			else if(position == 'bottom') 
    				jQuery('#pubsPaginateMsg').slideDown('fast').delay('700').slideUp('fast');
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
    
    jQuery("#nextPromotions").click(function (event){
		event.preventDefault();
		if(jQuery('.meetups .meetup').size() == 0)
			jsRoutes.controllers.Feed.nextPromotions("-1").ajax(loadMoreProms());
		else{
			jsRoutes.controllers.Feed.nextPromotions(jQuery('.meetup:last').attr('prom_id')).ajax(
				loadMoreProms()
			);
		}
	});

    var loadMoreProms = function(){
    	return {
    		success : function(data) {
    			var response = data.split("||");
    			if(response[0] == 0) {
    				console.log("recebemos: " + response[1]);
    				jQuery('#promsFootPaginate').before(response[1]);
    			}
    			else jQuery('#promsPaginateMsg').slideDown('fast')
					.delay('700')
					.slideUp('fast');
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