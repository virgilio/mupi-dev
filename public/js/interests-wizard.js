jQuery(function() {
    jQuery('.fileupload').fileupload('image');
    jQuery('.btn_delete').click(function(){
        jQuery(this).parent().hide('slow');
    });

    jQuery('.btn_favorite').toggle(function(){

        //                    Shrinks the heart icon
        jQuery(this).css({
            'background':'transparent url(@routes.Assets.at(img/favorite-hover-mini.png)) no-repeat'
        });

        //                    Checks the checkbox that refers to this interest
        if(jQuery(this).siblings('input:not(:checked)')){
            jQuery(this).siblings('input[name=interests]').attr('checked', true);
        }

        jQuery(this).siblings('.btn_delete').hide();

        //                    Shrinks the image div
        jQuery(this).parent().fadeOut('fast',function(){
            jQuery(this).removeClass('interest_normal').addClass('interest_thumb').appendTo('#chosen_interests').fadeIn()
            });


    }, function(){

        //                    Unchecks the checkbox that refers to this interest
        if(jQuery(this).siblings('input:checked')){
            jQuery(this).siblings('input[name=interests]').attr('checked', false);
        }

        jQuery(this).css({
            'background':'transparent url(@routes.Assets.at(img/favorite.png)) no-repeat'
        });



        jQuery(this).parent().fadeOut('fast',function(){
            jQuery(this).removeClass('interest_thumb').addClass('interest_normal').appendTo('#all_interests').fadeIn()
            });

        jQuery(this).siblings('.btn_delete').show();

    });
})