jQuery(function() {
    jQuery('.interest_normal > .btn_delete').live('click', function(){
        jsRoutes.controllers.Interest.ignoreInterest(jQuery(this).parent().attr('id')).ajax(succErrBuilder());
        jQuery(this).parent().hide('slow');
    });

    jQuery('.interest_normal > .btn_favorite').live('click', function(){
        // Checks the checkbox that refers to this interest
    	jsRoutes.controllers.Interest.checkInterest(jQuery(this).parent().attr('id')).ajax(succErrBuilder());

        if(jQuery(this).siblings('input:not(:checked)')){
            jQuery(this).siblings('input[name=interests]').attr('checked', true);
        }

        // Shrinks the image div
        jQuery(this).parent().fadeOut('fast',function(){
            jQuery(this).removeClass('interest_normal').addClass('interest_thumb').appendTo('#chosen_interests').fadeIn()
        });
    });

    jQuery('.interest_thumb > .btn_favorite').live('click',function(){
    	// Unchecks the checkbox that refers to this interest
    	jsRoutes.controllers.Interest.uncheckInterest(jQuery(this).parent().attr('id')).ajax(succErrBuilder());
    	
    	if(jQuery(this).siblings('input:checked')){
            jQuery(this).siblings('input[name=interests]').attr('checked', false);
        }

        jQuery(this).parent().fadeOut('fast',function(){
            jQuery(this).removeClass('interest_thumb').addClass('interest_normal').appendTo('#all_interests').fadeIn()
        });
    });
    

})