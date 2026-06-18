package com.TrainingTracker.TraingingTracker.Util;

import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecuiryUserUtil {

    public static Long getCurrntUserId(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null || authentication.getPrincipal() != null){
            throw  new RuntimeException("Authentication required");
        }
        Object principal = authentication.getPrincipal();
        if(principal instanceof SecurityUser securityUser){
            return securityUser.getId();
        }

        throw  new RuntimeException("Authentication required");
    }

}
