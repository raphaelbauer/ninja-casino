/**
 * Copyright (C) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package controllers;

import com.google.common.base.Optional;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;

import com.google.inject.Inject;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.Size;
import ninja.Context;
import ninja.SecureFilter;
import ninja.casino.CasinoMessages;
import ninja.casino.CasinoUserManager;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.utils.NinjaProperties;
import ninja.validation.JSR303Validation;
import ninja.validation.Length;
import ninja.validation.Required;
import ninja.validation.Validation;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasinoController {

    Logger logger = LoggerFactory.getLogger(CasinoController.class);

    @Inject
    CasinoUserManager casinoUserManager;

    NinjaProperties ninjaProperties;

    @Inject
    CasinoControllerHelper casinoControllerHelper;

    @Inject
    CasinoControllerMailer casinoControllerMailer;

    ///////////////////////////////////////////////////////////////////////////
    // Login and Logout
    ///////////////////////////////////////////////////////////////////////////
    public Result login(Session session) {

        return Results.html();

    }

    public Result loginPost(
            Context context,
            Session session,
            FlashScope flashScope,
            Validation validation,
            @Param("email") @Required @Length(min = 5) String email,
            @Param("password") @Required @Length(min = 5) String passwordPlainText) {

        if (!validation.hasViolations()) {

            Optional<String> passwordHash = casinoUserManager.getUserPasswordHash(email);

            if (passwordHash.isPresent()) {

                boolean isUserNameAndPasswordValid
                        = casinoControllerHelper.isPasswordValid(
                                passwordPlainText,
                                passwordHash.get());

                if (isUserNameAndPasswordValid) {
                    session.put(SecureFilter.USERNAME, email);
                    flashScope.success(CasinoMessages.CASINO_LOGIN_FLASH_SUCCESS);

                    return Results.redirect("/");

                }
            }

        }

        // something is wrong with the input or password not found.
        flashScope.put("email", email);
        flashScope.error(CasinoMessages.CASINO_LOGIN_FLASH_ERROR);

        return Results.redirect("/user/login");

    }

    public Result logout(
            FlashScope flashScope,
            Session session) {

        session.clear();
        flashScope.success(CasinoMessages.CASINO_LOGOUT_FLASH_SUCCESS);

        return Results.redirect("/");

    }

    ///////////////////////////////////////////////////////////////////////////
    // Register new user
    ///////////////////////////////////////////////////////////////////////////
    public Result register() {

        return Results.html();

    }

    public Result registerPost(
            Context context,
            FlashScope flashScope,
            Validation validation,
            @JSR303Validation RegisterForm registerForm) {

        // check if user already exists in db.
        boolean userAlreadyExists = false;

        if (!"".equals(registerForm.email)) {

            userAlreadyExists
                    = casinoUserManager.doesUserExist(registerForm.email);

        }

        if (validation.hasBeanViolations()
                || userAlreadyExists) {

            registerForm.clearErrorMessages();

            if (validation.hasBeanViolation(RegisterForm.EMAIL)
                    || userAlreadyExists) {

                registerForm.emailErrorMessage
                        = CasinoMessages.CASINO_REGISTER_FORM_ERROR_EMAIL;

            }

            if (validation.hasBeanViolation(RegisterForm.PASSWORD)) {
                registerForm.passwordErrorMessage
                        = CasinoMessages.CASINO_REGISTER_FORM_ERROR_PASSWORD;

            }
            
            if (validation.hasBeanViolation(RegisterForm.ACCEPT_TERMS_OF_SERVICE)) {
                registerForm.acceptTermsOfServiceErrorMessage
                        = CasinoMessages.CASINO_REGISTER_FORM_ERROR_ACCEPT_TERMS_OF_SERVICE;

            }

            return Results
                    .html()
                    .template("views/CasinoController/register.ftl.html")
                    .render(registerForm);

        } else {

            String passwordHash = casinoControllerHelper.createPasswordHashFromPassword(
                    registerForm.password);

            String activationCode = casinoControllerHelper.createWebSafeUuid();

            casinoUserManager.createNewCasinoUser(
                    registerForm.email,
                    passwordHash,
                    activationCode);

            casinoControllerMailer.sendEmailWithActivationCode(
                    context,
                    registerForm.email,
                    activationCode);

            flashScope.success(CasinoMessages.CASINO_REGISTER_SUCCESS);
            return Results.redirect("/");

        }

    }

    public Result activateUser(
            FlashScope flashScope,
            @PathParam("activationCode") String activationCode) {

        Optional<String> email
                = casinoUserManager.getCasinoUserWithActivationCode(activationCode);

        if (email.isPresent()) {

            casinoUserManager.deleteConfirmationCodeOfCasioUser(email.get());

            flashScope.success(CasinoMessages.CASINO_ACIVATE_USER_FLASH_SUCCESS);
            return Results.noContent().redirect("/user/login");

        } else {

            flashScope.error(CasinoMessages.CASINO_ACIVATE_USER_FLASH_ERROR);
            return Results.noContent().redirect("/");

        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // Reset password
    ///////////////////////////////////////////////////////////////////////////
    public Result requestResetPassword() {

        return Results.html();

    }

    public Result requestResetPasswordPost(
            Context context,
            FlashScope flashScope,
            Validation validation,
            @Param("email") @Required @Length(min = 3) String email) {

        if (email != null) {

            String passwordRecoveryCode = casinoControllerHelper.createWebSafeUuid();

            boolean resetCodeSettingWasSuccessful
                    = casinoUserManager.setPasswordRecoveryCode(
                            email,
                            passwordRecoveryCode);

            if (resetCodeSettingWasSuccessful) {

                casinoControllerMailer.sendEmailWithPasswordRecoveryCode(
                        context,
                        email,
                        passwordRecoveryCode);

            }

        }

        // This message might be wrong because we don't want anyone to 
        // brute force check registered users.
        flashScope.success(CasinoMessages.CASINO_REQUEST_RESET_PASSWORD_FLASH_SUCCESS);

        return Results.noContent().redirect("/");

    }

    public Result resetPassword(
            @PathParam("passwordResetCode") String passwordResetCode) {

        return Results.html().render("passwordResetCode", passwordResetCode);

    }

    public Result resetPasswordPost(
            FlashScope flashScope,
            @Param("passwordResetCode") String passwordResetCode,
            @Param("password") String password) {

        Optional<String> email
                = casinoUserManager.getCasinoUserWithRecoveryPasswordCode(
                        passwordResetCode);

        if (email.isPresent()) {

            String passwordHash = casinoControllerHelper.createPasswordHashFromPassword(password);

            casinoUserManager.setNewPasswordHashForUser(email.get(), passwordHash);
            casinoUserManager.setPasswordRecoveryCode(email.get(), null);
        }

        flashScope.success(CasinoMessages.CASINO_RESET_PASSWORD_FLASH_SUCCESS);

        return Results.noContent().redirect("/user/login");

    }

    // Just a helper that mimicks the form to create new user.
    public static class RegisterForm {

        @NotBlank
        @NotNull
        @Email
        public String email;
        public static final String EMAIL = "email";

        public String emailErrorMessage;

        @NotBlank
        @NotNull
        @Size(min=8, max=25)
        public String password;
        public static final String PASSWORD = "password";

        public String passwordErrorMessage;
        
        @NotNull
        public Boolean acceptTermsOfService;
        public static final String ACCEPT_TERMS_OF_SERVICE = "acceptTermsOfService";
        
        public String acceptTermsOfServiceErrorMessage;

        public RegisterForm() {
        }

        public void clearErrorMessages() {
            emailErrorMessage = null;
            passwordErrorMessage = null;
        }

    }

}
