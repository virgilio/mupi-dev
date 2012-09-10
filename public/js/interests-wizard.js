jQuery(function() {
    jQuery('.fileupload').fileupload('image');
    jQuery('.interest_normal > .btn_delete').click(function(){
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

    jQuery('.interest_normal > .btn_favorite').live('click', function(){
        //                    Checks the checkbox that refers to this interest
        if(jQuery(this).siblings('input:not(:checked)')){
            jQuery(this).siblings('input[name=interests]').attr('checked', true);
        }

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
    });

    jQuery('.interest_thumb > .btn_favorite').live('click',function(){
        //                    Unchecks the checkbox that refers to this interest
        if(jQuery(this).siblings('input:checked')){
            jQuery(this).siblings('input[name=interests]').attr('checked', false);
        }

        jQuery(this).parent().fadeOut('fast',function(){
            jQuery(this).removeClass('interest_thumb').addClass('interest_normal').appendTo('#all_interests').fadeIn()
        });
    });
    jsRoutes.controllers.Interest.uncheckInterest(jQuery(this).parent().attr('id')).ajax({
        success: function(data) {
            alert("=)");
        },
        error: function() {
            alert("=(");
        }
    });

})