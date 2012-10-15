jQuery(function(){	
   
	jQuery("#open_notifications").toggle(
		function(){
			jQuery("#notifications").fadeIn('fast');
		},
		function(){
			jQuery("#notifications").fadeOut('fast');
		}
	)
	jQuery("#clearNotifications").click( function(){
		jsRoutes.controllers.Mupi.clearBucket().ajax();
	})
})



