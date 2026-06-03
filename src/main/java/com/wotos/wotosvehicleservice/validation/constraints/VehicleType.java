package com.wotos.wotosvehicleservice.validation.constraints;


import com.wotos.wotosvehicleservice.validation.VehicleTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Constraint(validatedBy = VehicleTypeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VehicleType {
    String message() default "Invalid Vehicle Type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}