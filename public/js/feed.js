jQuery(function(){	
	jQuery(".comment_lnk").live('click', function (){
		event.preventDefault();
		if(jQuery("#" + jQuery(this).attr("comments")).is(':visible'))
			jQuery("#" + jQuery(this).attr("comments")).slideUp('fast');
		else
	       jQuery("#" + jQuery(this).attr("comments")).slideDown('fast');
	       
	});

	jQuery("#open_publication_input").live('click', function(){
		event.preventDefault();
		if(jQuery("#input_publication").is(':visible'))
			jQuery("#input_publication").slideUp('fast');
		else
			jQuery("#input_publication").slideDown();
    });
	
    jQuery('.btn_local').live('click', function(){
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
    
    jQuery('.interestIcon').live('click', function(){
    	event.preventDefault();
    	var i =jQuery(this).attr('id');
    	var l = jQuery("#selectedLocation").val();
    	jsRoutes.controllers.Feed.selectFeed(i, l).ajax(loadFeed(i, l));
    });
    
    jQuery('.sendComent').live('click', function(){
    	event.preventDefault();
    	var i = jQuery("#selectedInterest").val();
		var l = jQuery("#selectedLocation").val();
    	jsRoutes.controllers.Feed.comment(
			i,l,
			encodeURIComponent(jQuery(this).parent().prev('textarea').val()),
			jQuery(this).attr('publication')
		).ajax(loadFeed(i,l))
    });
    
    jQuery('#btn_confirm_publication').live('click', function(){
    	event.preventDefault();
    	var i = jQuery("#selectedInterest").val();
    	var l = jQuery("#selectedLocation").val();
    	jsRoutes.controllers.Feed.publish(
			encodeURIComponent(jQuery('#body_publication').val()),
			i,l
    	).ajax(loadFeed(i,l));
    });

    jQuery("#btn_publish").live('click', function(){
    	event.preventDefault();
    	jQuery("#preview_publication > .modal-body").html(jQuery("#body_publication").val());
    });
    
    var loadEditor = function(){
    	jQuery('#body_publication').wysihtml5();
    	var iframeResizer = function() {
            editor.composer.iframe.scrolling = "no";
            editor.composer.iframe.style.height = editor.composer.element.scrollHeight + "px";
        }
        editor.on("load", function() {
          editor.composer.element.addEventListener("keyup", iframeResizer, false);
          editor.composer.element.addEventListener("blur", iframeResizer, false);
          editor.composer.element.addEventListener("focus", iframeResizer, false);
        });
    }
    
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

    var loadFeed = function(i, l){
    	return {
    		success : function(data) {
    			var response = data.split("||");
    			if(response[0] == 0) jQuery('#feeds').html(response[1]);
    			if(i != -1 && l != -1 ) loadEditor();
    		},
    		error : function() {
    			if (jQuery('.alert').size() == 0) {
    				jQuery('#content').next().prepend("<div class='alert alert-error'> </div>");
    			}
    			jQuery('.alert')
    				.addClass('alert-error')
    				.html("Server error, please try again later!");
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
     
//    $.validator.addMethod(
//	    "brDate",
//	    function(value, element) {
//	        return value.match(/^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$/);
//	    }
//	);
    
//    $.validator.addMethod(
//	    "time",
//	    function(value, element) {
//	        return value.match(/^([0-2]|[0-9]):[0-5][0-9]$/);
//	    }
//	);
//    
//   
//	var validator = jQuery('#profileForm').validate({
//		rules : {
//			title : {
//				required : true,
//				minlength : 5
//			},
//			address : {
//				required:true,
//				minlength : 10
//			},
//			date : {
//				required:true,
//				brDate: true
//			},
//			time : {
//				required:true,
//				time:true 
//			},
//			link : {
//				required:false,
//				url: true
//			},
//			description : {
//				required:true,
//				maxlength : 3000
//			},
//			int : {
//				required:true,
//				number: true
//			},
//			loc : {
//				required:true,
//				number: true
//			},
//			picture :{
//				accept: "png|jpe?g|gif"
//			}
//		},
//		messages: {
//			title : {
//				required : "Você precisa informar um título para seu evento",
//				minlength : "O título deve ter ao menos {0} caracteres"
//			},
//			address : {
//				required:"Você precisa informar o endereço do seu evento",
//				minlength : "O endereço deve ter ao menos {0} caracteres"
//			},
//			date : {
//				required: "Você precisa informar a data do seu evento",
//				brDate: "A data de seu evento deve estar no formato dd/mm/yyyy"
//			},
//			time : {
//				required:"Você precisa informar o horário do seu evento",
//				time:"O horário do seu evento deve estar no formato hh:mm"
//			},
//			link : { 
//				url: "O endereço do site informado é inválido"
//			},
//			description : {
//				required:"Você deve descrever o evento",
//				maxlength : "Sua descrição deve ter no máximo 3000 caracteres"
//			},
//			picture :{
//				accept: "Sua imagem deve estar em um dos sequintes formatos: png, jpg, jpeg, gif"
//			}
//		},
//		highlight : function(label) {
//			jQuery(label).closest('.control-group').removeClass('success').addClass('error');
//		},
//		success : function(label) {
//			label.closest('.control-group').removeClass('error').addClass('success');
//		}
//	});
    
    
})






