package com.wotos.wotosvehicleservice.validation;

import com.wotos.wotosvehicleservice.validation.constraints.VehicleType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.EnumSet;

public class VehicleTypeValidator implements ConstraintValidator<VehicleType, String[]> {

    public enum VehicleType {
        LIGHT_TANK("Light Tank", "lightTank"),
        MEDIUM_TANK("Medium Tank", "mediumTank"),
        HEAVY_TANK("Heavy Tank", "heavyTank"),
        TANK_DESTROYER("Anti-Tank Self Propelled Gun Carrier", "AT-SPG"),
        ARTILLERY("Self Propelled Gun Carrier", "SPG");

        private final String name;
        private final String code;

        VehicleType(String name, String code) {
            this.name = name;
            this.code = code;
        }
    }

    @Override
    public boolean isValid(String[] strings, ConstraintValidatorContext constraintValidatorContext) {
        if (strings != null) {
            for (String string : strings) {
                VehicleType vehicleType = EnumSet.allOf(VehicleType.class).stream().filter(value -> value.code.equals(string)).findFirst().orElse(null);

                if (vehicleType == null) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Vehicle Type of: " + string + " is invalid").addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
