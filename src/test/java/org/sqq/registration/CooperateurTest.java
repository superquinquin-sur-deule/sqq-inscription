package org.sqq.registration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CooperateurTest {

    @Test
    public void testComputePartsForEtudiantWithNoBinomeAndNoSupport() {
        Cooperateur cooperateur = new Cooperateur();
        cooperateur.computeParts(true, null, null);
        assertEquals(1L, cooperateur.parts);
    }

    @Test
    public void testComputePartsForNonEtudiantWithBinomeAndSupport() {
        Cooperateur cooperateur = new Cooperateur();
        Binome binome = new Binome();
        cooperateur.computeParts(false, 3L, binome);
        assertEquals(15L, cooperateur.parts);
    }

    @Test
    public void testComputePartsForEtudiantWithBinomeAndSupport() {
        Cooperateur cooperateur = new Cooperateur();
        Binome binome = new Binome();
        cooperateur.computeParts(true, 5L, binome);
        assertEquals(8L, cooperateur.parts);
    }
}