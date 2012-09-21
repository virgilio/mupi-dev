jQuery(function(){	
   
	
	jQuery(".comment_lnk").live('click', function (){
		if(jQuery("#" + jQuery(this).attr("comments")).is(':visible'))
			jQuery("#" + jQuery(this).attr("comments")).slideUp('fast');
		else
	       jQuery("#" + jQuery(this).attr("comments")).slideDown('fast');
	       
	});
	
	
	jQuery("#open_publication_input").live('click', function(){
		if(jQuery("#input_publication").is(':visible'))
			jQuery("#input_publication").slideUp('fast');
		else
			jQuery("#input_publication").slideDown();
    });
	
    jQuery('.btn_local').live('click', function(){
        if(jQuery(this).children('i').hasClass('icon-check')){
            jQuery(this).children('i').removeClass('icon-check').addClass('icon-check-empty');
            getFeed(jQuery("#selectedInterest").val(), -1);
        } else {
            jQuery(this).children('i').removeClass('icon-check-empty').addClass('icon-check');
            getFeed(jQuery("#selectedInterest").val(), jQuery(this).attr('id'));
        };
    });
    
    jQuery('.interestIcon').live('click', function(){
    	getFeed(jQuery(this).attr('id'), -1);
    });
    
    jQuery('.commentPub').live('click', function(){
    	jsRoutes.controllers.Feed.comment(jQuery(this).prev('textarea').val(),jQuery(this).attr('publication')).ajax({
    		success : function(data) {
    			getFeed(jQuery("#selectedInterest").val(), jQuery("#selectedLocation").val());
    		},
    		error : function() {
    			if (jQuery('.alert').size() == 0) {
    				jQuery('#content').next().prepend("<div class='alert alert-error'> </div>");
    			}
    			jQuery('.alert')
    				.addClass('alert-error')
    				.html("Server error, please try again later!");
    		}
    	});
    	
    })


    jQuery('#btn_confirm_publication').live('click', function(){
    	jsRoutes.controllers.Feed.publish(
    			jQuery('#body_publication').val(),
    			jQuery(this).attr('interest'),
    			jQuery(this).attr('location')
    		).ajax({
    			success : function(data) {
    				var response = data.split("||");
    				if(response[0] == 0) jQuery('#publications').html(response[1]);
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
    	);
    });

    jQuery("#btn_publish").live('click', function(){
    	    jQuery("#preview_publication > .modal-body").html(jQuery("#body_publication").val());
    });
    
    var loadEditor = function(){
    	jQuery('#body_publication').wysihtml5();
    	var iframeResizer = function() {
            editor.composer.iframe.scrolling = "no";
            editor.composer.iframe.style.height = editor.composer.element.scrollHeight + "px";
        }
        editor.on("load", function() {
          editor.composer.element.addEventListener("keyup", iframeResizer, false);
          editor.composer.element.addEventListener("blur", iframeResizer, false);
          editor.composer.element.addEventListener("focus", iframeResizer, false);
        });
    }
    
    jQuery('.textarea_comment > textarea').live('focus', function(){
    	jQuery(this).autosize();
    })
    
    var getFeed = function(i, l){
    	jsRoutes.controllers.Feed.selectFeed(i, l).ajax({
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
    	})
    }
})






