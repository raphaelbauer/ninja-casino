//package dao;
//
//import com.google.common.base.Optional;
//import models.User;
//
//import com.googlecode.objectify.Objectify;
//
//import conf.OfyService;
//import static models.User.MAXIMUM_TIME_PASSWORD_RESET_IS_VALID;
//
//public class UserDao {
//
//    public void post(User user) {
//
//        Objectify ofy = OfyService.ofy();
//        ofy.save().entity(user);
//
//    }
//
//    public Optional<User> getUserWithActivationCode(String activationCode) {
//
//        Objectify ofy = OfyService.ofy();
//        User user = ofy.load().type(User.class)
//                .filter(User.ACTIVATION_CODE, activationCode).first().now();
//
//        return Optional.fromNullable(user);
//
//    }
//
//    public void activateUser(User user) {
//
//        Objectify ofy = OfyService.ofy();
//
//        user.activationCode = null;
//
//        ofy.save().entity(user);
//
//    }
//
//    public Optional<String> resetPasswordOfUserAndReturnPasswordResetCode(String email) {
//
//        String passwordResetCode = null;
//
//        Objectify ofy = OfyService.ofy();
//
//        User user = ofy.load().type(User.class).filter(User.EMAIL, email).first().now();
//
//        if (user != null) {
//
//            passwordResetCode = user.createPasswordResetCode();
//            ofy.save().entity(user);
//
//        }
//
//        return Optional.fromNullable(passwordResetCode);
//
//    }
//    
//    
//    public boolean updatePasswordWithPasswordResetCode(
//            String passwordPlainText,
//            String passwordResetCode) {
//        
//        Objectify ofy = OfyService.ofy();
//        
//        User user = ofy.load().type(User.class).filter(
//                User.PASSWORD_RESET_CODE, passwordResetCode).first().now();
//
//        if (user != null) {
//
//            if (System.currentTimeMillis() - user.getTimestampResetCode()
//                    < MAXIMUM_TIME_PASSWORD_RESET_IS_VALID) {
//            
//                user.clearPasswordResetCode();
//                user.updatePassword(passwordPlainText);
//            
//                ofy.save().entity(user).now();
//            }
//
//        }
//
//        return false;
//        
//    }
//
//    public boolean isUserAndPasswordValid(String email, String passwordPlainText) {
//
//        if (email != null && passwordPlainText != null) {
//
//            Objectify ofy = OfyService.ofy();
//            User user = ofy.load().type(User.class)
//                    .filter(User.EMAIL, email).first().now();
//
//            if (user != null
//                    && user.activationCode == null) {
//
//                if (user.isThisThePasswordOfTheUser(passwordPlainText)) {
//
//                    return true;
//                }
//
//            }
//
//        }
//
//        return false;
//
//    }
//
//}
