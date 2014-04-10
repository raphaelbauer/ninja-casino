/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.google.appengine.labs.repackaged.com.google.common.base.Predicates;
import ninja.NinjaDocTester;
import ninja.NinjaFluentLeniumTest;
import ninja.SecureFilter;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author ra
 */
public class CasinoControllerDocTester extends NinjaFluentLeniumTest {
    
    final String EMAIL = "me@example.com";
    final String PASSWORD = "a_secret_password";
        
    final String URL_LOGIN = "user/login";
    final String URL_REGISTER = "user/register";
    final String URL_REQUEST_RESET_PASSWORD = "user/request_reset_password";
    
    final String CSS_ID_EMAIL = "#email";
    final String CSS_ID_PASSWORD = "#password";
    final String CSS_ID_ACCEPT_TERMS_OF_SERVICE = "#acceptTermsOfService";
    
    final String CSS_ID_LOGIN_BUTTON = "#login_button";
    final String CSS_ID_REGISTER_BUTTON = "#register_button";
    
    @Test
    public void testLoginWithInvalidCredentials() {

        goTo(getServerAddress() + URL_LOGIN);

        // some shots to make sure the html content of the page is ok
        assertThat(pageSource(), containsString("<form action=\"/user/login\""));
        assertThat(pageSource(), containsString("<input id=\"password\" type=\"password\""));
        assertThat(pageSource(), containsString("<input id=\"email\" type=\"text\""));
        assertThat(pageSource(), containsString("<button id=\"login_button\" type=\"submit\""));

        submit(CSS_ID_LOGIN_BUTTON);
        assertThat(pageSource(), containsString("Oops. Login not successful."));

    }
    
    @Test
    public void testThatRegisterAllErrorMessagesWork() {
    
        goTo(getServerAddress() + "user/register");
    
        click(CSS_ID_REGISTER_BUTTON);
        assertThat(pageSource(), containsString("Not a valid email address"));
        assertThat(pageSource(), containsString("Not a valid password"));
        assertThat(pageSource(), containsString("You have to accept the terms to get an account"));
    
    }
    
    @Test
    public void testThatRegisterEmailErrorMessageWorks() {
    
        goTo(getServerAddress() + "user/register");
        fill(CSS_ID_EMAIL).with(EMAIL);
        
        click(CSS_ID_REGISTER_BUTTON);
        
        assertThat(pageSource(), not(containsString("Not a valid email address")));
        assertThat(pageSource(), containsString("Not a valid password"));
        assertThat(pageSource(), containsString("You have to accept the terms to get an account"));
    
    }
    
    @Test
    public void testThatRegisterPasswordErrorMessageWorks() {
    
        goTo(getServerAddress() + "user/register");
    
        fill(CSS_ID_PASSWORD).with(PASSWORD);
        
        click(CSS_ID_REGISTER_BUTTON);
        
        assertThat(pageSource(), containsString("Not a valid email address"));
        assertThat(pageSource(), not(containsString("Not a valid password")));
        assertThat(pageSource(), containsString("You have to accept the terms to get an account"));
    
    }
        @Test
    public void testThatRegisterAcceptTermsOfServiceErrorMessageWorks() {
    
        goTo(getServerAddress() + "user/register");
        // make sure checkbox is not checked initially
        assertThat(find(CSS_ID_ACCEPT_TERMS_OF_SERVICE).getAttribute("checked"), not(equalTo("true")));
        // accept the terms..
        click(CSS_ID_ACCEPT_TERMS_OF_SERVICE);
        
        
        click(CSS_ID_REGISTER_BUTTON);
        
        //now the checkbox should be checked...
        assertThat(find(CSS_ID_ACCEPT_TERMS_OF_SERVICE).getAttribute("checked"), equalTo("true"));
        
        assertThat(pageSource(), containsString("Not a valid email address"));
        assertThat(pageSource(), containsString("Not a valid password"));
        assertThat(pageSource(), not(containsString("You have to accept the terms to get an account")));
    
    }

    @Test
    public void testCompleteUserSignupPasswordResetLoginLogoutCycle() {

        ////////////////////////////////////////////////////////////////////////
        // Register a new user
        ////////////////////////////////////////////////////////////////////////
        goTo(getServerAddress() + "user/register");

        // some shots to make sure the html content of the page is ok
        assertThat(pageSource(), containsString("<form action=\"/user/register\" method=\"post\""));
        assertThat(pageSource(), containsString("<input id=\"email\" type=\"text\""));
        assertThat(pageSource(), containsString("<input id=\"password\" type=\"password\""));
        assertThat(pageSource(), containsString("<input id=\"acceptTermsOfService\" type=\"checkbox\""));
        assertThat(pageSource(), containsString("<button id=\"register_button\" type=\"submit\""));

        // now enter some values
        fill(CSS_ID_EMAIL).with(EMAIL);
        fill(CSS_ID_PASSWORD).with(PASSWORD);
        // accept terms of service
        click(CSS_ID_ACCEPT_TERMS_OF_SERVICE);

        click(CSS_ID_REGISTER_BUTTON);

        assertThat(
                pageSource(), 
                containsString("New user registered. Please check your mails to activate user."));

        // grep link to activation from mail
        PostofficeMockImpl postofficeMockImpl = (PostofficeMockImpl) getInjector().getInstance(Postoffice.class);
        Mail mail = postofficeMockImpl.getLastSentMail();

        assertThat(mail.getFrom(), equalTo("do_no_reply@mysuperservice.com"));

        String bodyText = mail.getBodyText();

        String userActivationHttpLink = grepHttpLinkFromMail(bodyText);

        ////////////////////////////////////////////////////////////////////////
        // Activate user via link sent in email
        ////////////////////////////////////////////////////////////////////////
        goTo(userActivationHttpLink);

        assertThat(
                pageSource(), 
                containsString("User activated. You can now log in!"));
        assertThat(url(), containsString(URL_LOGIN));

        // now enter some values
        fill(CSS_ID_EMAIL).with(EMAIL);
        fill(CSS_ID_PASSWORD).with(PASSWORD);

        click(CSS_ID_LOGIN_BUTTON);

        assertThat(url(), equalTo(getServerAddress()));
        assertThat(pageSource(), containsString("Login successful"));

        ////////////////////////////////////////////////////////////////////////
        // Reset password via link in email 
        // => works when invalid user is requested
        ////////////////////////////////////////////////////////////////////////
        goTo(getServerAddress() + "user/request_reset_password");
        fill(CSS_ID_EMAIL).with("ME@NON_EXISTENT_EMAIL");

        click("#request_password_reset_button");
        assertThat(url(), equalTo(getServerAddress()));
        // MUST SHOW SUCCESS MESSAGE => We don't want to allow anyone to spoof this.
        assertThat(
                pageSource(), 
                containsString("Sent mail with link to reset password. Please check your mails."));

        // no new mail has been sent
        Mail mail2 = postofficeMockImpl.getLastSentMail();
        assertThat(mail2, equalTo(mail));

        ////////////////////////////////////////////////////////////////////////
        // Reset password via link in email 
        // => works when valid user is requested
        ////////////////////////////////////////////////////////////////////////
        goTo(getServerAddress() + "user/request_reset_password");
        fill(CSS_ID_EMAIL).with(EMAIL);

        click("#request_password_reset_button");
        assertThat(url(), equalTo(getServerAddress()));
        assertThat(
                pageSource(),
                containsString(
                        "Sent mail with link to reset password. Please check your mails."));

        // no new mail has been sent
        Mail mail3 = postofficeMockImpl.getLastSentMail();
        assertThat(mail3, not(equalTo(mail)));

        assertThat(mail3.getFrom(), equalTo("do_no_reply@mysuperservice.com"));
        String resetPasswordHttpLink = grepHttpLinkFromMail(mail3.getBodyText());

        ////////////////////////////////////////////////////////////////////////
        // Reset password with link in email
        ////////////////////////////////////////////////////////////////////////
        goTo(resetPasswordHttpLink);

        assertThat(
                pageSource(), 
                containsString("<input id=\"passwordResetCode\" type=\"hidden\""));

        fill(CSS_ID_PASSWORD).with("new_password");
        click("#reset_password_button");

        assertThat(
                pageSource(), 
                containsString("Password reset successful. You can now log in again."));
        assertThat(url(), equalTo(getServerAddress() + "user/login"));

        ////////////////////////////////////////////////////////////////////////
        // Check that login works with new password
        ////////////////////////////////////////////////////////////////////////
        fill(CSS_ID_EMAIL).with(EMAIL);
        fill(CSS_ID_PASSWORD).with("new_password");
        click(CSS_ID_LOGIN_BUTTON);

        assertThat(pageSource(), containsString("Login successful"));
        assertThat(url(), equalTo(getServerAddress()));
        
        assertThat(
                getCookie("NINJA_SESSION").getValue(), 
                containsString("username=me%40example.com"));
        
        ////////////////////////////////////////////////////////////////////////
        // Check that logout works
        ////////////////////////////////////////////////////////////////////////
        goTo(getServerAddress() + "user/logout");
        
        assertThat(getCookie("NINJA_SESSION"), CoreMatchers.nullValue());
        

    }

    /**
     * Just a helper that extracts a "http..." link from a string. Useful
     * for extracting activation / registration links from emails sent
     * to user via MockMailer.
     */
    private String grepHttpLinkFromMail(String bodyText) {

        String[] lines = bodyText.split("\n");

        for (String line : lines) {

            if (line.startsWith("http")) {

                return line;

            }

        }

        return null;

    }

}
