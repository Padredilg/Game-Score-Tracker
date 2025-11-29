package com.cen4010.gamescoretracker.utils.requireadmin;


import com.cen4010.gamescoretracker.api.user.UserService;
import com.cen4010.gamescoretracker.api.user.database.User;
import com.cen4010.gamescoretracker.utils.exceptions.ForbiddenAccessException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminCheckAspect {

    private final UserService userService;

    @Before("@annotation(requireAdmin)") // Runs before any method annotated with @RequireAdmin
    public void checkAdmin(RequireAdmin requireAdmin) {

        // Fetch the currently logged-in user
        User currentUser = userService.getCurrentUser();

        if (currentUser.getGroup() == null ||
                currentUser.getGroup().getAdmin() == null ||
                !currentUser.getGroup().getAdmin().getUserId().equals(currentUser.getUserId())) {
            throw new ForbiddenAccessException("User must be an admin to perform this action");
        }
    }
}

