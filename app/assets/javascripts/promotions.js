jQuery(function(){	
	jQuery('.meetUp_subscription_feed').live('click', function(){
		var subscribeButton = jQuery(this);
		var meetUp = subscribeButton.prevAll('.meetup:first');
		var confirmed = meetUp.find('.n_confirmed:first');
		jsRoutes.controllers.Mupi.subscribeToMeetUp(meetUp.attr('prom_id')).ajax({
				success : function(data) {
					var response = data.split("||");
					if(response[0] == 0) {
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.addClass('alert-success')
						.html(response[1]);						
						subscribeButton.removeClass('meetUp_subscription_feed').addClass('meetUp_unsubscription_feed');
						if(meetUp.attr('pub_typ') == 4)
							subscribeButton.text("Desinscrever-me");
					    else{
					    	subscribeButton.text("Não vou mais");
					    }
						confirmed.text(parseInt(confirmed.text()) + 1);
					}else if(response[0] == 2){
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.addClass('alert-success')
						.html(response[1]);
					}else if(response[0] == 4){
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.html(response[1]);
					}else{
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.addClass('alert-error')
						.html(response[1]);
					}
				},
				error : function() {
					jQuery('.page-alert').html("<div class='alert hide'> </div>");
					jQuery('.page-alert > .alert')
					.addClass('alert-error')
					.html("Erro no servidor, por favor tente novamente mais tarde");

				}
		});
	});
	
	jQuery('.meetUp_unsubscription_feed').live('click', function(){
		var unsubscribeButton = jQuery(this);
		var meetUp = unsubscribeButton.prevAll('.meetup:first');
		var confirmed = meetUp.find('.n_confirmed:first');
		jsRoutes.controllers.Mupi.unsubscribeFromMeetUp(meetUp.attr('prom_id')).ajax({
				success : function(data) {
					var response = data.split("||");
					if(response[0] == 0) {
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.addClass('alert-success')
						.html(response[1]);
					}else if(response[0] == 2){
						jQuery('.page-alert').html("");
						unsubscribeButton.removeClass('meetUp_unsubscription_feed').addClass('meetUp_subscription_feed');
						if(meetUp.attr('pub_typ') == 4)
							unsubscribeButton.text("Quero ir!");
					    else{
					    	unsubscribeButton.text("Eu vou!");
					    }
						confirmed.text(parseInt(confirmed.text()) - 1);
					}else{
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.addClass('alert-error')
						.html(response[1]);
					}
				},
				error : function() {
					jQuery('.page-alert').html("<div class='alert hide'> </div>");
					jQuery('.page-alert > .alert')
					.addClass('alert-error')
					.html("Erro no servidor, por favor tente novamente mais tarde");

				}
		});
	});

	jQuery('.meetUp_subscription_promotion').live('click', function(){
		var subscribeButton = jQuery(this);
		var meetUp = jQuery('#event_body');
		jsRoutes.controllers.Mupi.subscribeToMeetUp(meetUp.attr('prom_id')).ajax({
				success : function(data) {
					var response = data.split("||");
					if(response[0] == 0) {
						subscribeButton.removeClass('meetUp_subscription_promotion').addClass('meetUp_unsubscription_promotion');
						if(meetUp.attr('pub_typ') == 4){
							jQuery('.page-alert').html("<div class='alert hide'> </div>");
							jQuery('.page-alert > .alert')
							.addClass('alert-success')
							.html(response[1]);	
							subscribeButton.text("Desinscrever-me");
						}
					    else{
					    	subscribeButton.text("Não vou mais");
					    }
						jQuery('.n_confirmed').each(function(){
							console.log(jQuery(this));
							jQuery(this).text(parseInt(jQuery(this).text()) + 1);
						});						
					}else if(response[0] == 2){
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.addClass('alert-success')
						.html(response[1]);
					}else if(response[0] == 4){
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.html(response[1]);
					}else{
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.addClass('alert-error')
						.html(response[1]);
					}
				},
				error : function() {
					jQuery('.page-alert').html("<div class='alert hide'> </div>");
					jQuery('.page-alert > .alert')
					.addClass('alert-error')
					.html("Erro no servidor, por favor tente novamente mais tarde");

				}
		});
	});

	
	jQuery('.meetUp_unsubscription_promotion').live('click', function(){
		var unsubscribeButton = jQuery(this);
		var meetUp = jQuery('#event_body');
		jsRoutes.controllers.Mupi.unsubscribeFromMeetUp(meetUp.attr('prom_id')).ajax({
				success : function(data) {
					var response = data.split("||");
					if(response[0] == 0) {
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.addClass('alert-success')
						.html(response[1]);
					}else if(response[0] == 2){
						jQuery('.page-alert').html("");
						unsubscribeButton.removeClass('meetUp_unsubscription_promotion').addClass('meetUp_subscription_promotion');
						if(meetUp.attr('pub_typ') == 4){
							jQuery('.page-alert').html("<div class='alert hide'> </div>");
							jQuery('.page-alert > .alert')
							.addClass('alert-success')
							.html(response[1]);	
							unsubscribeButton.text("Quero ir!");
						}else{
					    	unsubscribeButton.text("Eu vou!");
					    }
						jQuery('.n_confirmed').each(function(){
							console.log(jQuery(this));
							jQuery(this).text(parseInt(jQuery(this).text()) - 1);
						});	
					}else{
						jQuery('.page-alert').html("<div class='alert hide'> </div>");
						jQuery('.page-alert > .alert')
						.addClass('alert-error')
						.html(response[1]);
					}
				},
				error : function() {
					jQuery('.page-alert').html("<div class='alert hide'> </div>");
					jQuery('.page-alert > .alert')
					.addClass('alert-error')
					.html("Erro no servidor, por favor tente novamente mais tarde");

				}
		});
	});
})
