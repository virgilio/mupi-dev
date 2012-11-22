jQuery(function(){	
	jQuery(".meetup_box").live('mouseenter', function(event){
		jQuery(this).find('.meetup_img').css({'opacity':'0.3'});
		jQuery(this).find('.meetup_info').fadeIn();
	});
	jQuery(".meetup_box").live('mouseleave', function(event){
		jQuery(this).find('.meetup_img').css({'opacity':'1'});
		jQuery(this).find('.meetup_info').hide();
	});
	
	jQuery("#interest_selector").change(function(){
  	  jQuery('#communitySelectionForm').submit();
	});
	
	jQuery("#location_selector").change(function(){
	  jQuery('#communitySelectionForm').submit();
	});
})




