package conf;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class MupiParams {
  private static final Config conf = ConfigFactory.load();

  // E-MAIL ADDRESSES
  public static final String LOCATION_SUGGESTION_EMAIL = "contato@mupi.me";
  public static final String PROMOTE_MEETUP_EMAIL      = "contato@mupi.me";
  public static final String HOST_MEETUP_EMAIL         = "contato@mupi.me";

  // Directories
  public static final String UPLOAD_ROOT    = conf.getString("upload.path");

  public static final String INTEREST_ROOT  = UPLOAD_ROOT + "//interest";
  public static final String EVENT_ROOT     = UPLOAD_ROOT + "//event";
  public static final String PROFILE_ROOT   = UPLOAD_ROOT + "//profile";

  public static final String PIC_ROOT       = "//picture";
  public static final String PIC_THUMB      = PIC_ROOT + "//thumb";
  public static final String PIC_MEDIUM     = PIC_ROOT + "//medium";

  public static final String BLANK_PIC      = "//blank.jpg";

  // Profile Params
  public static final Integer FIRST_LOGIN   = Integer.parseInt("00001",2);
  public static final Integer HELP_PROFILE  = Integer.parseInt("00010",2);
  public static final Integer HELP_INTEREST = Integer.parseInt("00100",2);
  public static final Integer HELP_FEED     = Integer.parseInt("01000",2);
  public static final Integer ALL_HELPS     = HELP_INTEREST & HELP_PROFILE & HELP_FEED;
  public static final Integer NO_DEPS       = Integer.parseInt("10000",2);
}
