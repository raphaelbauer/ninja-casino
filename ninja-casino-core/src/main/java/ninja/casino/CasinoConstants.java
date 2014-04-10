package ninja.casino;

/**
 * Used in application.conf
 *
 * @author ra
 *
 */
public interface CasinoConstants {

    /* must be set in application conf */
    public static String EMAIL_FROM = "casino.emailFrom";

    /* optional in application conf */
    public static String SECURE_URL = "casino.secureUrl";

    /* optional in application conf */
    public static String REGULAR_URL = "casino.regularUrl";

    /**
     * must implement AfterUserCreationHook interface...
     */
    public static String AFTER_CREATION_HOOK = "casino.afterUserCreationHook";

    public static final String CASINO_USER_MANAGER = "casino.userManager";
    
    public static final String CASINO_HASH_ROUNDS_KEY = "casino.bcyrptHashRounds";
    
    public static final int CASINO_HASH_ROUNDS_DEFAULT_VALUE = 10;
    
    public static final String CASINO_EMAIL_FROM_ADDRESS = "casino.email.from.address";

}
