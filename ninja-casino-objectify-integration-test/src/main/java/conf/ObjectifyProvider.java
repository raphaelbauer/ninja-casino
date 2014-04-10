/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package conf;

import com.google.inject.Provider;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import models.User;

/**
 *
 * @author ra
 */
public class ObjectifyProvider implements Provider<Objectify> {
    
    static {
        ObjectifyService.register(User.class);
    }

    @Override
    public Objectify get() {
        return ObjectifyService.ofy();
    }

    
}
