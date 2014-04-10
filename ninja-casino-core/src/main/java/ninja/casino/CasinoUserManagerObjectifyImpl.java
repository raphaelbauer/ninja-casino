package ninja.casino;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import models.User;
import ninja.appengine.AppEngineEnvironment;

@AppEngineEnvironment
public class CasinoUserManagerObjectifyImpl implements CasinoUserManager {

    @Inject
    Provider<Objectify> objectifyProvider;
    
    @Override
    public boolean createNewCasinoUser(
            String email,
            String passwordHash,
            String confirmationCode) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify.load().type(User.class).filter(User.EMAIL, email).first().now();

        if (user != null) {
            return false;
        }

        user = new User(email, passwordHash, confirmationCode);
        objectify.save().entity(user).now();

        return true;

    }

    @Override
    public boolean isUserActivated(String email) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify.load().type(User.class).filter(User.EMAIL, email).first().now();

        // the email should be there
        if (user == null) {
            return false;
        }

        // make sure the user confirmed the name
        if (user.confirmationCode.length() != 0) {
            return false;
        }

        return true;
    }

    @Override
    public boolean hasRole(String email, String role) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify.load().type(User.class).filter(User.EMAIL, email).first().now();

        if (user == null) {
            return false;
        }

        return user.hasRole(role);

    }

    @Override
    public void addRole(String email, String role) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify.load().type(User.class).filter(User.EMAIL, email).first().now();

        if (user == null) {
            return;
        }

        user.addRole(role);

        objectify.save().entity(user).now();

    }

    @Override
    public void removeRole(String email, String role) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify.load().type(User.class).filter(User.EMAIL, email).first().now();

        if (user == null) {
            return;
        }

        user.removeRole(role);
        objectify.save().entity(user).now();

    }

    @Override
    public boolean setPasswordRecoveryCode(
            String email,
            String recoveryPasswordCode) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify.load().type(User.class).filter(User.EMAIL, email).first().now();

        if (user == null) {
            return false;
        }

        user.recoverPasswordCode = recoveryPasswordCode;
        objectify.save().entity(user).now();
        
        return true;

    }

    @Override
    public Optional<String> getCasinoUserWithActivationCode(String confirmationCode) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify
                .load()
                .type(User.class)
                .filter(User.CONFIRMATION_CODE, confirmationCode)
                .first()
                .now();

        if (user == null) {
            return Optional.absent();
        }

        return Optional.of(user.email);

    }

    @Override
    public void deleteConfirmationCodeOfCasioUser(String email) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify.load().type(User.class).filter(User.EMAIL, email).first().now();

        if (user == null) {
            return;
        }

        user.confirmationCode = null;
        objectify.save().entity(user);

    }

    @Override
    public Optional<String> getUserPasswordHash(String email) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify.load().type(User.class).filter(User.EMAIL, email).first().now();

        if (user == null) {
        
            return Optional.absent();
            
        }
        
        return Optional.of(user.passwordHash);
    }

    @Override
    public void setNewPasswordHashForUser(String email, String passwordHash) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify.load().type(User.class).filter(User.EMAIL, email).first().now();

        if (user == null) {
            return;
        }

        user.passwordHash = passwordHash;

        objectify.save().entity(user).now();
    }

    @Override
    public boolean doesUserExist(String email) {

        Objectify objectify = objectifyProvider.get();
        Key<User> user = objectify
                .load()
                .type(User.class)
                .filter(User.EMAIL, email)
                .keys()
                .first()
                .now();

        if (user == null) {
            return false;
        }

        return true;
    }

    @Override
    public Optional<String> getCasinoUserWithRecoveryPasswordCode(
            String recoverPasswordCode) {

        Objectify objectify = objectifyProvider.get();
        User user = objectify
                .load()
                .type(User.class)
                .filter(User.RECOVER_PASSWORD_CODE, recoverPasswordCode)
                .first()
                .now();

        if (user == null) {
            return Optional.absent();
        }

        return Optional.of(user.email);
    }

}
