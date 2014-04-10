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

import com.google.common.io.BaseEncoding;

import com.google.inject.Inject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import ninja.casino.CasinoAfterUserCreationHook;
import ninja.casino.CasinoConstants;
import ninja.utils.NinjaProperties;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CasinoControllerHelper {

    Logger logger = LoggerFactory.getLogger(CasinoControllerHelper.class);

    final NinjaProperties ninjaProperties;
    /**
     * If there is no custom CasinoAfterUserCreationHook we don't do anything.
     * Therefore it is optional.
     */
    @Inject(optional = true)
    CasinoAfterUserCreationHook casinoAfterUserCreationHook;

    final int NUMBER_OF_BCRYPT_HASH_ROUNDS;

    @Inject
    public CasinoControllerHelper(NinjaProperties ninjaProperties) {

        this.ninjaProperties = ninjaProperties;

        NUMBER_OF_BCRYPT_HASH_ROUNDS = ninjaProperties.getIntegerWithDefault(
                CasinoConstants.CASINO_HASH_ROUNDS_KEY,
                CasinoConstants.CASINO_HASH_ROUNDS_DEFAULT_VALUE);

    }

    boolean isPasswordValid(
            String passwordPlainText,
            String passwordHash) {

        return BCrypt.checkpw(passwordPlainText, passwordHash);

    }

    String createPasswordHashFromPassword(String passwordPlainText) {

        return BCrypt.hashpw(
                passwordPlainText,
                BCrypt.gensalt(NUMBER_OF_BCRYPT_HASH_ROUNDS));

    }

    public void executeAfterUserCreationHook(String email) {

        if (casinoAfterUserCreationHook != null) {

            casinoAfterUserCreationHook.execute(email);

        }

    }

    public String createWebSafeUuid() {

        try {

            UUID uuid = UUID.randomUUID();
            byte[] uuidAsBytes = uuid.toString().getBytes("UTF-8");
            return BaseEncoding.base64Url().encode(uuidAsBytes);

        } catch (UnsupportedEncodingException unsupportedEncodingException) {

            String MESSAGE = "There is something really wrong. We cannot use UTF-8 encoding. Stopping.";

            logger.error(MESSAGE, unsupportedEncodingException);

            throw new RuntimeException(
                    MESSAGE,
                    unsupportedEncodingException);
        }

    }

}
