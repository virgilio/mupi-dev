jQuery(function() {	
	
	$.validator.addMethod(
		    "brDate",
		    function(value, element) {
		        // put your own logic here, this is just a (crappy) example
		        return value.match(/^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$/);
		    }
		);
	
	var validator = jQuery('#profileForm').validate({
		rules : {
			firstName : {
				required : true,
				minlength : 2
			},
			lastName : {
				required:false,
				minlength : 2
			},
			about : {
				required:false,
				maxlength : 500
			},
			gender : {
				required:false,
				range: [0,3]
			},
			birthDate : {
				required:false,
				brDate: true
			},
			picture :{
				accept: "png|jpe?g|gif"
			}
		},
		messages: {
			firstName : {
				required : "Você precisa digitar seu nome",
				minlength: jQuery.format("Seu nome deve ter ao menos {0} caracteres")
			},
			lastName : {
				minlength : jQuery.format("Seu sobrenome deve ter ao menos {0} caracteres")
			},
			about : {
				maxlength : jQuery.format("Seu about deve ter no máximo {0} caracteres")
			},
			gender : {
				range: jQuery.format("Seu sexo deve estar entre as opções listadas")
			},
			birthDate : {
				brDate: "Sua data de nascimento deve estar no formato dd/mm/aaaa"
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
	
	jQuery('#profileForm').valid();
})
