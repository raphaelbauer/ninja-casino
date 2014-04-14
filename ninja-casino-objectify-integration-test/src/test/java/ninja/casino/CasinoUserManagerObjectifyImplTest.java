package ninja.casino;




import com.google.common.base.Optional;

import com.googlecode.objectify.Objectify;
import conf.ObjectifyProvider;
import models.User;
import ninja.NinjaDocTester;
import ninja.appengine.AppEngineEnvironment;
import ninja.appengine.NinjaAppengineEnvironment;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;


public class CasinoUserManagerObjectifyImplTest extends NinjaDocTester {

    @Before
    public void before() {
    
        // setup app environment => we are not entering the thread via servlet...
        NinjaAppengineEnvironment ninjaAppengineEnvironment 
                = getInjector().getInstance(NinjaAppengineEnvironment.class);
        ninjaAppengineEnvironment.initOrSkip();
    
    }

    @Test
    public void testThatOldRecoveryCodeInternalsWork() {
        
        ObjectifyProvider objectifyProvider 
                = getInjector().getInstance(ObjectifyProvider.class);
       
        
        User user = new User("user@email.com", null, null);
        user.recoverPasswordCode = "a_cover_password_code";
        // and an up to date password time stamp...
        user.recoverPasswordCodeTimeStamp = System.currentTimeMillis();
        
        objectifyProvider.get().save().entity(user).now();
        
        
        CasinoUserManager casinoUserManager 
                = getInjector().getInstance(CasinoUserManager.class);
        
        // we should not find it
        assertThat(
                casinoUserManager.getCasinoUserWithRecoveryPasswordCode(
                        "a_cover_password_code"),
                equalTo(Optional.of("user@email.com")));
        
        // check in the datastore that user has been modified...
        User userFromDb = objectifyProvider.get().load().type(User.class).filter(
                User.EMAIL, "user@email.com").first().now();
        
        // assert that recover stuff has been deleted
        assertThat(userFromDb, notNullValue());
        assertThat(userFromDb.recoverPasswordCode, nullValue());
        assertThat(userFromDb.recoverPasswordCodeTimeStamp, nullValue());
       
    }
    
    @Test
    public void testThatOldRecoveryCodeGetsDeleted() {
        
        // setup
        NinjaAppengineEnvironment ninjaAppengineEnvironment 
                = getInjector().getInstance(NinjaAppengineEnvironment.class);
        ninjaAppengineEnvironment.initOrSkip();
        
        
        ObjectifyProvider objectifyProvider 
                = getInjector().getInstance(ObjectifyProvider.class);
       
        
        User user = new User("user@email.com", null, null);
        user.recoverPasswordCode = "a_cover_password_code";
        user.recoverPasswordCodeTimeStamp = 0L; // this is a very old revocery code
        
        objectifyProvider.get().save().entity(user).now();
        
        
        CasinoUserManager casinoUserManager 
                = getInjector().getInstance(CasinoUserManager.class);
        
        // we should not find it
        assertThat(
                casinoUserManager.getCasinoUserWithRecoveryPasswordCode(
                        "a_cover_password_code"),
                equalTo(Optional.<String>absent()));
        
        // check in the datastore that user has been modified...
        User userFromDb = objectifyProvider.get().load().type(User.class).filter(
                User.EMAIL, "user@email.com").first().now();
        
        // assert that recover stuff has been deleted
        assertThat(userFromDb, notNullValue());
        assertThat(userFromDb.recoverPasswordCode, nullValue());
        assertThat(userFromDb.recoverPasswordCodeTimeStamp, nullValue());
       
    }
    
}
