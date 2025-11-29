package com.cen4010.gamescoretracker.utils.requireadmin;

import java.lang.annotation.*;

@Target(ElementType.METHOD) // Can be applied to methods
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
@Documented
public @interface RequireAdmin {
}
