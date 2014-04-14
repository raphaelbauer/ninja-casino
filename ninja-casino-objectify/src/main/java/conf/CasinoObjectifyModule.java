package conf;

import com.google.inject.AbstractModule;
import com.googlecode.objectify.Objectify;
import ninja.appengine.AppEngineModule;
import ninja.casino.CasinoUserManager;
import ninja.casino.CasinoUserManagerObjectifyImpl;

public class CasinoObjectifyModule extends AbstractModule {
    

    @Override
    protected void configure() {
        
        bind(CasinoUserManager.class).to(CasinoUserManagerObjectifyImpl.class); 
        
    }

}
