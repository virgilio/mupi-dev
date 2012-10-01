package utils;

import play.mvc.Results;
import play.mvc.Result;



public class AjaxResponse {
	public static Result build(Integer code, String message){
		if (code == 0 || code == 2)
			return Results.ok(code + "||" + message);
		else
			return Results.badRequest(code + "||" + message);
	}	
}
