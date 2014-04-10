package conf;

import models.User;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * Best practise for Objectify to register your entities.
 *
 * @author ra
 *
 */
public class OfyService {

    static {

        ObjectifyService.register(User.class);

    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

}
