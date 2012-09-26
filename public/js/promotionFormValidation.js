jQuery(function(){	

	$.validator.addMethod(
		    "brDate",
		    function(value, element) {
		    	return	value=="" || value=="__/__/____" || value.match(/^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[012])\/\d{4}$/);
		    }
		);
	    
    $.validator.addMethod(
	    "brTime",
	    function(value, element) {
	        return value=="" || value=="__:__" || value.match(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/);
	    }
	);
	    
	   
	var validator = jQuery('#promoteForm').validate({
		rules : {
			title : {
				required : true,
				minlength : 5
			},
			address : {
				required:true,
				minlength : 10
			},
			date : {
				required:true,
				brDate: true
			},
			time : {
				required:true,
				brTime:true 
			},
			link : {
				required:false,
				url: true
			},
			description : {
				required  : true,
				maxlength : 3000
			},
			int : {
				required:true,
				number: true
			},
			loc : {
				required:true,
				number: true
			},
			picture :{
				accept: "png|jpe?g|gif"
			}
		},
		messages: {
			title : {
				required : "Você precisa informar um título para seu evento",
				minlength : "O título deve ter ao menos {0} caracteres"
			},
			address : {
				required:"Você precisa informar o endereço do seu evento",
				minlength : "O endereço deve ter ao menos {0} caracteres"
			},
			date : {
				required: "Você precisa informar a data do seu evento",
				brDate: "A data de seu evento deve estar no formato dd/mm/yyyy"
			},
			time : {
				required:"Você precisa informar o horário do seu evento",
				brTime:"O horário do seu evento deve estar no formato hh:mm"
			},
			link : { 
				url: "O endereço do site informado é inválido"
			},
			description : {
				required:"Você deve descrever o evento",
				maxlength : "Sua descrição deve ter no máximo 3000 caracteres"
			},
			picture :{
				accept: "Sua imagem deve estar em um dos sequintes formatos: png, jpg, jpeg, gif"
			}
		},
		highlight : function(label) {
			jQuery(label).closest('.control-group').removeClass('success').addClass('error');
		},
		success : function(label) {
			label.closest('.control-group').removeClass('error').addClass('success');
		}
	});
	jQuery('#promoteForm').valid();    
})






