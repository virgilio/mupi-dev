package conf;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class MupiParams {
  private static final Config conf = ConfigFactory.load();
  
  // E-MAIL ADDRESSES
  public static final String LOCATION_SUGGESTION_EMAIL = "banduk@gmail.com";
  public static final String PROMOTE_MEETUP_EMAIL      = "banduk@gmail.com";
  public static final String HOST_MEETUP_EMAIL         = "banduk@gmail.com";  
  
  // Directories
  public static final String UPLOAD_ROOT    = conf.getString("upload.path");
  
  public static final String INTEREST_ROOT  = UPLOAD_ROOT + "//interest";
  public static final String EVENT_ROOT     = UPLOAD_ROOT + "//event";
  public static final String PROFILE_ROOT   = UPLOAD_ROOT + "//profile";
  
  public static final String PIC_ROOT       = "//picture";
  public static final String PIC_THUMB      = PIC_ROOT + "//thumb";
  public static final String PIC_MEDIUM     = PIC_ROOT + "//medium";  
  
  public static final String BLANK_PIC      = "//blank.jpg";
}