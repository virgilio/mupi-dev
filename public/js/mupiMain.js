var getsize = function(){	
	if ((jQuery('.fix_img img').first().height()) > (jQuery('.fix_img img').first().width())){
		jQuery('.fix_img img').css({'max-width':'100%'});
	} else	{
		jQuery('.fix_img img').css({'max-height':'100%', 'max-width':'none'});
	}

}