package controllers;

import play.mvc.Controller;

public class MupiSession extends Controller {
  
  public static void setLocalInterest(Long i){
    session().put("interest", i.toString());
  }
  
  public static Long getLocalInterest(){
    String i = session().get("interest");
    return getInterest(i);
  }

  public static Long getInterest(String i){
    if(i != null && !i.isEmpty())
      return Long.parseLong(i);
    else
      return null;
  }

  public static void setLocalLocation(Long l){
    session().put("location", l.toString());
  }
  
  public static Long getLocalLocation(){
    String l = session().get("location");
    return getLocation(l);
  }

  public static Long getLocation(String l){
    if(l != null && !l.isEmpty())
      return Long.parseLong(l);
    else
      return null;
  }
}
