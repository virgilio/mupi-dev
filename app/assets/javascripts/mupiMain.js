jQuery(function(){




	jQuery('.mupi_thumbs .fix_img img').load(function(){

       	var h = jQuery(this).height();
    	var w = jQuery(this).width();

    	var hp = jQuery(this).parent().height();
    	var wp = jQuery(this).parent().width();


    	if (h > w){
    		jQuery(this).css({'width':'100%'});
//        		var h2 = jQuery(img).height();
//        		var dh = (hp - h2)/2;
//        		jQuery(img).css({'margin-top':'-' + dh +'px'});
    	} else	{
    		jQuery(this).css({'height':'100%', 'max-width':'none'});
//        		var w2 = jQuery(img).width();
//        		var dw = (wp - w2)/2;
//        		jQuery(img).css({'margin-left': '-'+ dw +'px'});
    	}
    })


    jQuery("a[rel=clickover]").clickover();
});


//function getsize(img){
//	var h = jQuery(img).height();
//	var w = jQuery(img).width();
//
//	var hp = jQuery(img).parent().height();
//	var wp = jQuery(img).parent().width();
//
//
//	if (h > w){
//		jQuery(img).css({'width':'100%'});
////		var h2 = jQuery(img).height();
////		var dh = (hp - h2)/2;
////		jQuery(img).css({'margin-top':'-' + dh +'px'});
//	} else	{
//		jQuery(img).css({'height':'100%', 'max-width':'none'});
////		var w2 = jQuery(img).width();
////		var dw = (wp - w2)/2;
////		jQuery(img).css({'margin-left': '-'+ dw +'px'});
//	}
//
//}
