package com.beanpack.beanify;

import org.junit.jupiter.api.Test;

import com.beanpack.beanify.Branding;

import static org.junit.jupiter.api.Assertions.*;


class BrandingTest {

    @Test
    void testLogoIsNotNull() {
        assertNotNull(Branding.logo, "The logo should not be null");
    }

    @Test
    void testLogoContainsVersion() {
        assertTrue(Branding.logo.contains("B E A N C H A I N::"), "The logo should contain 'B E A N C H A I N::'");
    }
}