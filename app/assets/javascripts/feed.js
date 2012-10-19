jQuery(function(){	
	jQuery(".comment_lnk").live('click', function (event){
		event.preventDefault();
		if(jQuery("#" + jQuery(this).attr("comments")).is(':visible'))
			jQuery("#" + jQuery(this).attr("comments")).slideUp('fast');
		else
			jQuery("#" + jQuery(this).attr("comments")).slideDown('fast');

		console.log(jQuery("#" + jQuery(this).attr("comments")).offset().top);
		//console.log(jQuery(this).attr("comments"));
		//jQuery("html, body").animate({ scrollTop : (jQuery("#" + jQuery(this).attr("comments")).offset().top - 50)});

	});

	jQuery("#open_publication_input").live('click', function(event){
		event.preventDefault();
		if(jQuery("#input_publication").is(':visible'))
			jQuery("#input_publication").slideUp('fast');
		else{
			jQuery("#input_publication").slideDown();
			jQuery('#pub_form').validate({
				rules : {
					interest : {
						min: 0
					},
					location : {
						min: 0
					}			
				},
				validClass: "success",
				errorClass: "error",
				highlight : function(element) {
					jQuery(element).removeClass('success').addClass('error');
				},
				success : function(element) {
					jQuery(element).removeClass('error').addClass('success');
				},
				submitHandler: function(form) {
					var serialized = jQuery(form).serializeArray();
					jQuery("#interest_publication").val(serialized[2].value);
					jQuery("#location_publication").val(serialized[3].value);
					jQuery("#preview_publication > .modal-body").html(
							"<span class='pub_opts'><i class='icon-star'></i> " +
							jQuery("#interest_publication > option[value="+serialized[2].value +"]").text() +  " " +
							"<i class='icon-map-marker'></i> " +
							jQuery("#location_publication > option[value="+serialized[3].value +"]").text() +  "</span><br/>" +
							jQuery("#body_publication").val()
					);
					jQuery("#preview_publication").modal('show');
				},
				errorPlacement: function(error, element) {} // Necessary for not to put error/success label
			});
			jQuery('#pub_form').valid();
		}
	});
	jQuery("#disabled_publication_input").live('click', function(event){
		jQuery('.page-alert').html("<div class='alert hide'>Escolha uma <b>cidade</b> no menu superior e um <b>interesse</b> no menu lateral.</div>");
		jQuery('.page-alert > .alert')
		.addClass('alert-error');
		jQuery('#locals .btn').delay(4000).animate({
			opacity: 0.2
		}).animate({
			opacity: 1
		});
		jQuery('.sidebar li').delay(5000).animate({
			opacity: 0.2
		}).animate({
			opacity: 1
		})
	});
	jQuery('.btn_local').live('click', function(event){
		event.preventDefault();
		var i = jQuery("#selectedInterest").val();
		if(jQuery(this).children('i').hasClass('icon-check')){
			var l = -1;
			jQuery(this).children('i').removeClass('icon-check').addClass('icon-check-empty');
			jsRoutes.controllers.Feed.selectFeed(i, l).ajax(loadFeed(i,l));
		} else {
			var l = jQuery(this).attr('id');
			jQuery('.icon-check').removeClass('icon-check').addClass('icon-check-empty');
			jQuery(this).children('i').removeClass('icon-check-empty').addClass('icon-check');
			jsRoutes.controllers.Feed.selectFeed(i, l).ajax(loadFeed(i,l));
		};
	});

	jQuery('.interestIcon').live('click', function(event){
		event.preventDefault();
		jQuery('.selected_i').removeClass('selected_i');
		jQuery(this).parent().addClass('selected_i');
		var i =jQuery(this).attr('id');
		var l = jQuery("#selectedLocation").val();
		jsRoutes.controllers.Feed.selectFeed(i, l).ajax(loadFeed(i, l));
		jQuery("html, body").animate({ scrollTop : 0});
	});

	jQuery('.sendComent').live('click', function(event){
		event.preventDefault();
		var i = jQuery("#selectedInterest").val();
		var l = jQuery("#selectedLocation").val();
		jsRoutes.controllers.Feed.comment(
				encodeURIComponent(jQuery(this).parent().prev('textarea').val()),
				jQuery(this).attr('publication')
		).ajax(loadFeed(i,l, jQuery(this).attr('publication')))
	});

	jQuery('#btn_confirm_publication').live('click', function(event){
		event.preventDefault();
		var i = jQuery("#selectedInterest").val();
		var l = jQuery("#selectedLocation").val();
		jsRoutes.controllers.Feed.publish(
				jQuery("#interest_publication").val(),
				jQuery("#location_publication").val(),
				encodeURIComponent(
						jQuery('#body_publication').val()
				)
		).ajax(loadFeed(i,l));
	});

	jQuery('.textarea_comment textarea').live('focus', function(){
		jQuery(this).autosize();
	});
	jQuery('.textarea_comment textarea').live('focus', function(){
		jQuery(this).next('.btns_comment').slideDown();
	});
	jQuery('.enter_cancel').live('click', function(){
		jQuery(this).parent().hide(0,function(){
			jQuery(this).siblings('textarea').val('').resize();
		});

	});
	
	jQuery('.remove_comment').live('click', function(){
		var comment = jQuery(this).parent();
		jsRoutes.controllers.Feed.removeComment(comment.attr('comment_id')).ajax({
			success : function(data) {
				var response = data.split("||");
				if(response[0] == 0){
					comment.remove();
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

	var loadFeed = function(i, l, p){
		return {
			success : function(data) {
				var response = data.split("||");
				if(response[0] == 0) jQuery('#feed_content').html(response[1]);
				jQuery("a[rel=clickover]").clickover();
				if(typeof p != undefined){
					console.log(".comment_lnk[comments=comments_" + p + "]");
					//jQuery(".comment_lnk[comments=comments_" + p + "]").click();
					jQuery("#comments_" + p).removeClass("hide");
					jQuery("html, body").animate({ scrollTop: jQuery(".publication[pub_id=" + p + "]").offset().top - 20});
				}
			},
			error : function() {
				jQuery('.page-alert').html("<div class='alert hide'> </div>");
				jQuery('.page-alert > .alert')
				.addClass('alert-error')
				.html("Erro no servidor, por favor tente novamente mais tarde");

			}
		}
	}

	var findByName = function(src, n) {
		for (var i = 0; i < src.length; i++) {
			if (src[i].name === n) {
				return src[i].value;
			}
		}
	}

})




