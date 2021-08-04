package com.hark.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hark.exceptions.UserNotVerifiedException;
import com.hark.model.User;
import com.hark.model.enums.ResponseStatus;
import com.hark.model.payload.response.MessageResponse;
import com.hark.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ProfileVerificationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = null;
        String username = null;
        try {
            username = request.getUserPrincipal().getName();
            user = userRepository.findByUsername(username).get();
        }catch (Exception exception){
            System.out.println("User not found for user name: "+username+" with exception "+exception.getMessage());
        }
        if (null != user && !user.isEnabled()) {
            System.out.println("Throwing exception user not verified.");
            throw new UserNotVerifiedException("User " + request.getUserPrincipal().getName() + " is not verified.");
        }
        return super.preHandle(request,response,handler);
    }

}
