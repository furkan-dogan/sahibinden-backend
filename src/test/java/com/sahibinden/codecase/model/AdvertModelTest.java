package com.sahibinden.codecase.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

public class AdvertModelTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testValidAdvertModel() {
        AdvertModel advert = new AdvertModel();
        advert.setTitle("Valid Title 123");
        advert.setDescription("This is a valid description with enough length.");
        advert.setCategory("Emlak");
        advert.setStatus("Active");

        Set<ConstraintViolation<AdvertModel>> violations = validator.validate(advert);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidTitleTooShort() {
        AdvertModel advert = new AdvertModel();
        advert.setTitle("Short");
        advert.setDescription("This is a valid description with enough length.");
        advert.setCategory("Emlak");
        advert.setStatus("Active");

        Set<ConstraintViolation<AdvertModel>> violations = validator.validate(advert);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("İlan başlığı en az 10, en fazla 50 karakter olmalıdır.\n", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidTitleSpecialCharacter() {
        AdvertModel advert = new AdvertModel();
        advert.setTitle("@InvalidTitle");
        advert.setDescription("This is a valid description with enough length.");
        advert.setCategory("Emlak");
        advert.setStatus("Active");

        Set<ConstraintViolation<AdvertModel>> violations = validator.validate(advert);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("İlan başlığı harf veya rakam ile başlamalıdır.\n", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidDescriptionTooShort() {
        AdvertModel advert = new AdvertModel();
        advert.setTitle("Valid Title 123");
        advert.setDescription("Too short.");
        advert.setCategory("Emlak");
        advert.setStatus("Active");

        Set<ConstraintViolation<AdvertModel>> violations = validator.validate(advert);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("İlan detay açıklaması en az 20, en fazla 200 karakter olmalıdır.", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidCategory() {
        AdvertModel advert = new AdvertModel();
        advert.setTitle("Valid Title 123");
        advert.setDescription("This is a valid description with enough length.");
        advert.setCategory("InvalidCategory");
        advert.setStatus("Active");

        Set<ConstraintViolation<AdvertModel>> violations = validator.validate(advert);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Geçersiz ilan kategorisi.", violations.iterator().next().getMessage());
    }
}
