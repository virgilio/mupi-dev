jQuery(function(){	
    jQuery('#promotion_comments textarea').autosize();
    
    jQuery('#sendComent').live('click', function(event){
	event.preventDefault();
    	jsRoutes.controllers.Feed.commentPublication(
	    encodeURIComponent(jQuery(this).parent().prev('textarea').val()),
	    jQuery(this).attr('publication')
	).ajax(loadComments())
	
    });

    var loadComments = function(){
    	return {
    	    success : function(data) {
    		var response = data.split("||");
    		if(response[0] == 0) jQuery('#comments').html(decodeURIComponent(response[1]));
    		jQuery("#textToSend").val("");
    		jQuery("#textToSend").resize();
    		jQuery("#promotion_comments_number").text(parseInt(jQuery("#promotion_comments_number").text()) + 1);
		jQuery("a[rel=clickover]").clickover();
    	    },
    	    error : function() {
    		jQuery('.page-alert').html("<div class='alert hide'> </div>");
		jQuery('.page-alert > .alert')
		    .addClass('alert-error')
		    .html("Erro no servidor, por favor tente novamente mais tarde");
    	    }
    	}
    }
    
    var geocoder = new google.maps.Geocoder();
    var mapOptions = {
	zoom: 15,
	mapTypeId: google.maps.MapTypeId.ROADMAP,
	mapTypeControl: false,
	scaleControl: false,
	streetViewControl: false,
	overviewMapControl: false,
	panControl: false,
	zoomControl: true
    }
    var map = new google.maps.Map(document.getElementById('promotion_map'), mapOptions);
    
    
    
    geocoder.geocode( { 'address': jQuery("#address_val").text()}, function(results, status) {
	if (status == google.maps.GeocoderStatus.OK) {
	    map.setCenter(results[0].geometry.location);
	    var marker = new google.maps.Marker({
	        map: map,
	        position: results[0].geometry.location
	    });
	    var directionsService = new google.maps.DirectionsService();
	    directionsDisplay = new google.maps.DirectionsRenderer();
	    directionsDisplay.setMap(map);
	} else {
	    
	}
    });
    
    
    
    
    jQuery("#login_modal").modal("hide");
    jQuery("#login_modal_but").live('click', function(event){
        event.preventDefault();
        if(jQuery("#login_modal").is(':visible'))
        	jQuery("#login_modal").modal("hide");
        else{
        	jQuery("#login_modal").modal("show");
        }
      });

    jQuery("#not_logged_comments  input, #not_logged_comments textarea").live('click focus keyup', function(event){
        event.preventDefault();
        if(jQuery("#login_modal").is(':visible'))
        	jQuery("#login_modal").modal("hide");
        else{
        	jQuery("#not_logged_comments textarea").val("");
        	jQuery("#login_modal").modal("show");
        } 
      });
    
    if(window.location.hash == "#login_modal"){
    	jQuery("#login_modal").modal("show");
    }
    
    
    
    
})






