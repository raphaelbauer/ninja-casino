package models;



import com.google.common.collect.ImmutableSet;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Index
public class User {

    @Id
    public Long id;

    public static final String EMAIL = "email";
    public String email;

    public static final String PASSWORD_HASH = "passwordHash";
    public String passwordHash;

    public static final String CONFIRMATION_CODE = "confirmationCode";
    public String confirmationCode;

    public static final String RECOVER_PASSWORD_CODE = "recoverPasswordCode";
    public String recoverPasswordCode;
    
    public static final String RECOVER_PASSWORD_CODE_TIMESTAMP = "recoverPasswordCodeTimeStamp";
    public Long recoverPasswordCodeTimeStamp;

    /**
     * A simple String based role checking. use methods hasRole / addRole /
     * removeRole to manage roles.
     *
     * You can use your own roles in your domain.
     *
     * Examples would be: admin or superadmin

     */
    private Set<String> roles;

    // Objectify needs default constructor
    public User() {}
    
    public User(String email, String passwordHash, String confirmationCode) {

        this.email = email;
        this.passwordHash = passwordHash;
        this.confirmationCode = confirmationCode;

        this.roles = new HashSet<>();
    }

    public boolean hasRole(String role) {

        return roles.contains(role);

    }

    public void addRole(String role) {

        roles.add(role);

    }

    public void removeRole(String role) {

        roles.remove(role);

    }

}
