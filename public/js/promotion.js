jQuery(function(){	
	jQuery('#promotion_comments textarea').autosize();
	
	jQuery('#sendComent').live('click', function(){
		event.preventDefault();
    	jsRoutes.controllers.Feed.commentPromotion(
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
	
})






