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
import com.google.inject.Inject;
import com.google.inject.Provider;
import ninja.Context;
import ninja.Result;
import ninja.casino.CasinoConstants;
import ninja.casino.CasinoMessages;
import ninja.i18n.Lang;
import ninja.i18n.Messages;

import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasinoControllerMailer {
    
    public static final String APPLICATION_SERVER_NAME = "application.server.name";

    Logger logger = LoggerFactory.getLogger(CasinoControllerMailer.class);

    @Inject
    Postoffice postoffice;

    @Inject
    Provider<Mail> mailProvider;

    NinjaProperties ninjaProperties;
    
    @Inject
    Messages messages;
    
    final String EMAIL_FROM;
    
    @Inject
    CasinoControllerMailer(NinjaProperties ninjaProperties) {
    
        this.ninjaProperties = ninjaProperties;
        this.EMAIL_FROM = ninjaProperties.getOrDie(CasinoConstants.CASINO_EMAIL_FROM_ADDRESS);
    
    }

    public void sendEmailWithActivationCode(
            Context context,
            String emailTo,
            String activationCode) {
        

        Mail mail = mailProvider.get();

        mail.addTo(emailTo);

        mail.setFrom(EMAIL_FROM);
        
        Optional<String> subjectText = messages.get(
                CasinoMessages.CASINO_MAILER_SEND_EMAIL_WITH_ACTIVATION_CODE_SUBJECT, 
                context, 
                Optional.<Result>absent());

        Optional<String> bodyText = messages.get(
                CasinoMessages.CASINO_MAILER_SEND_EMAIL_WITH_ACTIVATION_CODE_BODY_TEXT, 
                context, 
                Optional.<Result>absent(),
                ninjaProperties.get(APPLICATION_SERVER_NAME),
                activationCode);
        
        if (!bodyText.isPresent()
                || !subjectText.isPresent()) {
        
            logger.error("Cannot get i18n message for activation message. Needs to be fixed. (Activation for: {})", emailTo);
            return;
        }
        
        
        mail.setSubject(subjectText.get());
        mail.setBodyText(bodyText.get());
                
        try {
            
            postoffice.send(mail);
            
        } catch (Exception exception) {
            logger.error("An error occurred while sending activation email ({})", 
                    emailTo, 
                    exception);
        }

    }

    public void sendEmailWithPasswordRecoveryCode(
            Context context,
            String emailTo,
            String passwordResetCode) {

        Mail mail = mailProvider.get();

        mail.addTo(emailTo);

        mail.setFrom(EMAIL_FROM);

        Optional<String> subjectText = messages.get(
                CasinoMessages.CASINO_MAILER_SEND_EMAIL_WITH_PASSWORD_RESET_CODE_SUBJECT, 
                context, 
                Optional.<Result>absent());

        Optional<String> bodyText = messages.get(
                CasinoMessages.CASINO_MAILER_SEND_EMAIL_WITH_PASSWORD_RESET_CODE_BODY_TEXT, 
                context, 
                Optional.<Result>absent(),
                ninjaProperties.get(APPLICATION_SERVER_NAME),
                passwordResetCode);
        
        if (!bodyText.isPresent()
                || !subjectText.isPresent()) {
        
            logger.error("Cannot get i18n message for password reset message. "
                    + "Needs to be fixed. (Password reset for: {})"
                    , emailTo);
            return;
        }
        
        mail.setSubject(subjectText.get());
        mail.setBodyText(bodyText.get());

        try {
            postoffice.send(mail);
        } catch (Exception exception) {
            logger.error(
                    "An error occurred while resetting password (email: {})"
                    , emailTo
                    , exception);

        }

    }

  
}
