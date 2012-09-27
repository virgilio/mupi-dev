jQuery(function(){	
	jQuery("#nextPublications").click(function(event){
		event.preventDefault();
		if(jQuery('.all_publications .publication').size() == 0)
			jsRoutes.controllers.Feed.nextPublications("-1").ajax(loadMorePubs());
		else{
			jsRoutes.controllers.Feed.nextPublications(jQuery('.publication:last').attr('pub_id')).ajax(
			  loadMorePubs()
			);
	    }
	});

    var loadMorePubs = function(){
    	return {
    		success : function(data) {
    			var response = data.split("||");
    			if(response[0] == 0) {
    				jQuery('#pubsFootPaginate').before(response[1]);
    			}
    			else jQuery('#pubsPaginateMsg').text("Não existem mais publicações")
    					.slideDown('fast')
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
    			else jQuery('#promsPaginateMsg').text("Não existem mais publicações")
					.slideDown('fast')
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