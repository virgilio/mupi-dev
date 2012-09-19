jQuery(function(){
   jQuery(".comment_lnk").click(function (){
       jQuery(this).parent().parent().parent(".publication").removeClass('pub_usual').addClass('pub_opened');
       jQuery(this).parent().parent().next(".comment").slideDown();
   });
   
    
})
