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
		jsRoutes.controllers.Mupi.clearBucket().ajax({
			success: function(data) {
				jQuery("#notification_popup").hide();	
				
				jQuery('#notifications').html(
						"<i class='icon-caret-up caret_not'></i>" +
						"<ul> <li class='li_empty_notification'>Não há novas notificações</li></ul>"
				);
				jQuery("#open_notifications").trigger('click');
			},
			error: function(data) {}
		})
	})
})



