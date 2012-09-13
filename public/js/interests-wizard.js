jQuery(function() {
	// Error and success messages for ajax calls	
	var succErrMsg = {
			success : function(data) {
				if (jQuery('.alert').size() == 0) {
					jQuery('.navbar-container').next().prepend("<div class='alert alert-success'> </div>");
				}
				if (data)
					jQuery('.alert').attr('class', 'alert alert-success').html(data);
			},
			error : function() {
				if (jQuery('.alert').size() == 0) {
					jQuery('.navbar-container').next().prepend("<div class='alert-error'> </div>");
				}
				jQuery('.alert')
						.addClass('alert-error')
						.html("Server error, please try again later!");
			}
		}
	
    jQuery('.fileupload').fileupload('image');

	
    jQuery('.interest_normal > .btn_delete').live('click', function(){
        jsRoutes.controllers.Interest.ignoreInterest(jQuery(this).parent().attr('id')).ajax(succErrMsg);
        jQuery(this).parent().hide('slow');
    });

    jQuery('.interest_normal > .btn_favorite').live('click', function(){
        // Checks the checkbox that refers to this interest
    	jsRoutes.controllers.Interest.checkInterest(jQuery(this).parent().attr('id')).ajax(succErrMsg);

        if(jQuery(this).siblings('input:not(:checked)')){
            jQuery(this).siblings('input[name=interests]').attr('checked', true);
        }

        //                    Shrinks the image div
        jQuery(this).parent().fadeOut('fast',function(){
            jQuery(this).removeClass('interest_normal').addClass('interest_thumb').appendTo('#chosen_interests').fadeIn()
        });
    });

    jQuery('.interest_thumb > .btn_favorite').live('click',function(){
    	//Unchecks the checkbox that refers to this interest
    	jsRoutes.controllers.Interest.uncheckInterest(jQuery(this).parent().attr('id')).ajax(succErrMsg);
    	
    	if(jQuery(this).siblings('input:checked')){
            jQuery(this).siblings('input[name=interests]').attr('checked', false);
        }

        jQuery(this).parent().fadeOut('fast',function(){
            jQuery(this).removeClass('interest_thumb').addClass('interest_normal').appendTo('#all_interests').fadeIn()
        });
    });
    

})