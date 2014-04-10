/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;
import ninja.utils.NinjaConstant;

/**
 *
 * @author ra
 */
public class AuthenticityFilter implements Filter {
    
    public static final String AUTHENTICITY_TOKEN = "authenticityToken";
    
    @Override
    public Result filter(FilterChain filterChain, Context context) {
        
        Session session = context.getSession();
        String authenticityToken = context.getParameter(AUTHENTICITY_TOKEN);
        
        if (session.getAuthenticityToken().equals(authenticityToken)) {
        
            return filterChain.next(context);
            
        } else {
            
            return Results
                    .forbidden()
                    .template(NinjaConstant.LOCATION_VIEW_FTL_HTML_FORBIDDEN);
            
        }
        
       
    }
    
}
