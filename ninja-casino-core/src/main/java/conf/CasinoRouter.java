/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package conf;

import controllers.CasinoController;
import ninja.Router;
import ninja.application.ApplicationRoutes;


public class CasinoRouter implements ApplicationRoutes {

    @Override
    public void init(Router router) {
        
        router.GET().route("/user/login").with(CasinoController.class, "login");
        router.POST().route("/user/login").with(CasinoController.class, "loginPost");
        router.GET().route("/user/logout").with(CasinoController.class, "logout");
        
        router.GET().route("/user/register").with(CasinoController.class, "register");
        router.POST().route("/user/register").with(CasinoController.class, "registerPost");
        
        router.GET().route("/user/activate/{activationCode}").with(CasinoController.class, "activateUser");
        
        router.GET().route("/user/request_reset_password").with(CasinoController.class, "requestResetPassword");
        router.POST().route("/user/request_reset_password").with(CasinoController.class, "requestResetPasswordPost");
        
        router.GET().route("/user/reset_password/{passwordResetCode}").with(CasinoController.class, "resetPassword");
        router.POST().route("/user/reset_password").with(CasinoController.class, "resetPasswordPost");
 
    }
    
}
