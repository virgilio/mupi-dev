jQuery(function() {
    jQuery('.fileupload').fileupload('image');
    jQuery('.btn_delete').click(function(){
    	jQuery(this).parent().hide('slow');
    	
    	jsRoutes.controllers.Interest.ignoreInterest(jQuery(this).parent().attr('id')).ajax({
	        success: function(data) {
	          alert("=)");
	        },
	        error: function() {
	        	alert("=(");
	        }
	      });
    });

    jQuery('.btn_favorite').toggle(function(){

        //                    Shrinks the heart icon
        jQuery(this).css({
            'background':'transparent url(jsRoutes.Assets.at(img/favorite-hover-mini.png)) no-repeat'
        });

        //                    Checks the checkbox that refers to this interest
        if(jQuery(this).siblings('input:not(:checked)')){
            jQuery(this).siblings('input[type=checkbox]').attr('checked', true);
        }

        jQuery(this).siblings('.btn_delete').hide();

        //                    Shrinks the image div
        jQuery(this).parent().fadeOut('fast',function(){
            jQuery(this).removeClass('interest_normal').addClass('interest_thumb').appendTo('#chosen_interests').fadeIn()
            });
        
        
        jsRoutes.controllers.Interest.checkInterest(jQuery(this).parent().attr('id')).ajax({
	        success: function(data) {
	          alert("=)");
	        },
	        error: function() {
	        	alert("=(");
	        }
	      });
    }, function(){

        //                    Unchecks the checkbox that refers to this interest
        if(jQuery(this).siblings('input:checked')){
            jQuery(this).siblings('input[type=checkbox]').attr('checked', false);
        }

        jQuery(this).css({
            'background':'transparent url(jsRoutes.Assets.at(img/favorite.png)) no-repeat'
        });


        jQuery(this).parent().fadeOut('fast',function(){
            jQuery(this).removeClass('interest_thumb').addClass('interest_normal').appendTo('#all_interests').fadeIn()
            });

        jQuery(this).siblings('.btn_delete').show();
        
        
        jsRoutes.controllers.Interest.uncheckInterest(jQuery(this).parent().attr('id')).ajax({
	        success: function(data) {
	          alert("=)");
	        },
	        error: function() {
	        	alert("=(");
	        }
	      });

    });
})

