     _______  .___ _______        ____.  _____   
     \      \ |   |\      \      |    | /  _  \  
     /   |   \|   |/   |   \     |    |/  /_\  \ 
    /    |    \   /    |    \/\__|    /    |    \
    \____|__  /___\____|__  /\________\____|__  /
         web\/framework   \/                  \/ 
        
[![Build Status](https://buildhive.cloudbees.com/job/raphaelbauer/job/ninja-casino/badge/icon)](https://buildhive.cloudbees.com/job/raphaelbauer/job/ninja-casino/)

ninja-casino
============

Simple user management aka authentication and authorization for Ninja.


roadmap
=======

Stuff planned:

 * bump to sable versions (appengine, ninja) once they are on maven central
 * password reset form
 * transfer of login token between two domains after successful login
   (use case appengine appspot)
 * Add csrf protection to forms
 * Add JPA module + integration test
 * OAuth support via scribe (facebook, twitter, google login) 


General installation instructions:
==================================

application.conf:

    application.server.name=https://www.myrealserver.com
    %dev.application.server.name=http://localhost:8080

    casino.email.from.address=raphael.andre.bauer@gmail.com
    %test.casino.email.from.address=do_no_reply@mysuperservice.com
    %dev.casino.email.from.address=do_no_reply@mysuperservice.com


messages.properties:

    include=ninja/casino/casino_messages.properties

in your conf/Routes.java file add routes:

    public class Routes implements ApplicationRoutes {
    
        @Inject
        CasinoRouter casinoRouter;

        @Override
        public void init(Router router) { 
        
            casinoRouter.init(router);

            ...
        }
    }


More for Objectify:
===================

in your pom.xml add the following dependency:

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-casino-objectify</artifactId>
        <version>LATEST_VERSION</version>
    </dependency>


in your Module.conf:
    
    install(new CasinoObjectifyModule());




