package com.wotos.wotosvehicleservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

class LanguageValidatorTests {

    private final LanguageValidator validator = new LanguageValidator();
    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class, RETURNS_DEEP_STUBS);

    @Test
    void acceptsKnownLanguageCode() {
        assertThat(validator.isValid("en", context)).isTrue();
        assertThat(validator.isValid("ru", context)).isTrue();
        assertThat(validator.isValid("ZH-CN", context)).isTrue(); // case-insensitive
    }

    @Test
    void rejectsUnknownLanguageCode() {
        assertThat(validator.isValid("zz", context)).isFalse();
    }
}
