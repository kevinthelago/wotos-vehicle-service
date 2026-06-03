package com.wotos.wotosvehicleservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

class VehicleTypeValidatorTests {

    private final VehicleTypeValidator validator = new VehicleTypeValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class, RETURNS_DEEP_STUBS);

    @Test
    void acceptsKnownTypes() {
        assertThat(validator.isValid(new String[]{"lightTank", "heavyTank", "AT-SPG"}, context)).isTrue();
    }

    @Test
    void acceptsNull() {
        assertThat(validator.isValid(null, context)).isTrue();
    }

    @Test
    void rejectsUnknownType() {
        assertThat(validator.isValid(new String[]{"lightTank", "spaceshipTank"}, context)).isFalse();
    }
}
