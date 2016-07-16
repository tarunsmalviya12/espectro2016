package in.mbm.espectro.utils;

/**
 * Created by tarunsmalviya12 on 29/9/15.
 */
public class URLS {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public URLS() {
    }

    public static final String BASE_URL = "http://www.espectro2016.in/android/api/";

    public static final String EVENT_IMG_URL = BASE_URL + "get/event_img/";
    public static final String SPONSOR_IMG_URL = BASE_URL + "get/sponsor_img/";

    public static final String GET_URL = BASE_URL + "get/";
    public static final String GET_COLLEGE_LIST = GET_URL + "college.php?";
    public static final String GET_LOGIN = GET_URL + "login.php?";
    public static final String GET_FORGOT_PASSWORD = GET_URL + "forgot_password.php?";
    public static final String GET_EVENT_LIST = GET_URL + "event.php?";
    public static final String GET_NOTIFICATION_LIST = GET_URL + "notification.php?";
    public static final String GET_SPONSOR_LIST = GET_URL + "sponsor.php?";

    public static final String POST_URL = BASE_URL + "post/";
    public static final String POST_REGISTER = POST_URL + "register.php?";
    public static final String POST_EVENT_REGISTER = POST_URL + "event_register.php?";
}