package conf;

import com.google.inject.Provider;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import models.User;

public class ObjectifyProvider implements Provider<Objectify> {
    
    static {
        ObjectifyService.register(User.class);
    }

    @Override
    public Objectify get() {
        return ObjectifyService.ofy();
    }
  
}
