jQuery(function(){	
   
	jQuery("#open_notifications").toggle(
		function(){
			jQuery("#notifications").slideDown('fast');
		},
		function(){
			jQuery("#notifications").slideUp('fast');
		}
	)
	jQuery("#clearNotifications").click( function(){
		jsRoutes.controllers.Mupi.clearBucket().ajax();
	})
})



