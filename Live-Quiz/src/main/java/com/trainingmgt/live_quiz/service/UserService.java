package com.trainingmgt.live_quiz.service;

import com.trainingmgt.live_quiz.resource.Participant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
public class UserService {

    public String getUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No request context available");
        }

        HttpServletRequest request = attributes.getRequest();
        String userId = request.getHeader("X-User-Email");
        if (userId == null) {
            throw new IllegalArgumentException("Missing 'user' header");
        }
        return userId;
    }

    public String getUserRoles() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No request context available");
        }

        HttpServletRequest request = attributes.getRequest();
        String userRole = request.getHeader("crossroads");
        if (userRole == null) {
            throw new IllegalArgumentException("Missing 'role' header");
        }
        return userRole;
    }
    public Participant getPlayerDetails(String userId) {
    //#TODO: call user service or user profile service to fetch details
        return new Participant(userId, "Trainee " + userId, 0,"avatar_url_" + userId,"Trainee");
    }


}
