package org.sqq.registration;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqq.registration.repository.PartTransactionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class PartTransactionTest {

    @Inject
    PartTransactionRepository partTransactionRepository;

    private CooperateurV2 testCooperateur;

    @BeforeEach
    @Transactional
    void setUp() {
        testCooperateur = new CooperateurV2();
        testCooperateur.genre = Genre.MONSIEUR;
        testCooperateur.prenom = "Test";
        testCooperateur.nom = "User";
        testCooperateur.telephone = "0600000000";
        testCooperateur.email = "test-" + System.currentTimeMillis() + "@test.com";
        testCooperateur.adresse = "1 rue Test";
        testCooperateur.ville = "TestVille";
        testCooperateur.codePostal = "59000";
        testCooperateur.acceptationDesStatus = true;
        testCooperateur.persist();
    }

    @Test
    @Transactional
    void shouldReturnZeroForNoTransactions() {
        Long total = partTransactionRepository.computeTotalParts(testCooperateur);
        assertEquals(0L, total);
    }

    @Test
    @Transactional
    void shouldComputeSingleInitialPurchase() {
        PartTransaction tx = new PartTransaction();
        tx.cooperateur = testCooperateur;
        tx.transactionType = PartTransactionType.INITIAL_PURCHASE;
        tx.parts = 5L;
        tx.persist();

        Long total = partTransactionRepository.computeTotalParts(testCooperateur);
        assertEquals(5L, total);
    }

    @Test
    @Transactional
    void shouldComputeMultipleSameTypeTransactions() {
        PartTransaction tx1 = new PartTransaction();
        tx1.cooperateur = testCooperateur;
        tx1.transactionType = PartTransactionType.ADDITIONAL_PURCHASE;
        tx1.parts = 3L;
        tx1.persist();

        PartTransaction tx2 = new PartTransaction();
        tx2.cooperateur = testCooperateur;
        tx2.transactionType = PartTransactionType.ADDITIONAL_PURCHASE;
        tx2.parts = 2L;
        tx2.persist();

        Long total = partTransactionRepository.computeTotalParts(testCooperateur);
        assertEquals(5L, total);
    }

    @Test
    @Transactional
    void shouldComputeMixedTransactions() {
        PartTransaction initial = new PartTransaction();
        initial.cooperateur = testCooperateur;
        initial.transactionType = PartTransactionType.INITIAL_PURCHASE;
        initial.parts = 10L;
        initial.persist();

        PartTransaction additional = new PartTransaction();
        additional.cooperateur = testCooperateur;
        additional.transactionType = PartTransactionType.ADDITIONAL_PURCHASE;
        additional.parts = 5L;
        additional.persist();

        Long total = partTransactionRepository.computeTotalParts(testCooperateur);
        assertEquals(15L, total);
    }

    @Test
    @Transactional
    void shouldHandleRefunds() {
        PartTransaction initial = new PartTransaction();
        initial.cooperateur = testCooperateur;
        initial.transactionType = PartTransactionType.INITIAL_PURCHASE;
        initial.parts = 10L;
        initial.persist();

        PartTransaction refund = new PartTransaction();
        refund.cooperateur = testCooperateur;
        refund.transactionType = PartTransactionType.REFUND;
        refund.parts = -3L;
        refund.reason = "Partial refund";
        refund.persist();

        Long total = partTransactionRepository.computeTotalParts(testCooperateur);
        assertEquals(7L, total);
    }

    @Test
    @Transactional
    void shouldHandleAdjustments() {
        PartTransaction initial = new PartTransaction();
        initial.cooperateur = testCooperateur;
        initial.transactionType = PartTransactionType.INITIAL_PURCHASE;
        initial.parts = 5L;
        initial.persist();

        PartTransaction adjustment = new PartTransaction();
        adjustment.cooperateur = testCooperateur;
        adjustment.transactionType = PartTransactionType.ADJUSTMENT;
        adjustment.parts = 2L;
        adjustment.reason = "Correction";
        adjustment.persist();

        Long total = partTransactionRepository.computeTotalParts(testCooperateur);
        assertEquals(7L, total);
    }

    @Test
    @Transactional
    void shouldReturnZeroWhenFullyRefunded() {
        PartTransaction initial = new PartTransaction();
        initial.cooperateur = testCooperateur;
        initial.transactionType = PartTransactionType.INITIAL_PURCHASE;
        initial.parts = 5L;
        initial.persist();

        PartTransaction refund = new PartTransaction();
        refund.cooperateur = testCooperateur;
        refund.transactionType = PartTransactionType.REFUND;
        refund.parts = -5L;
        refund.reason = "Full refund";
        refund.persist();

        Long total = partTransactionRepository.computeTotalParts(testCooperateur);
        assertEquals(0L, total);
    }

    @Test
    @Transactional
    void shouldHandleManyTransactions() {
        for (int i = 0; i < 100; i++) {
            PartTransaction tx = new PartTransaction();
            tx.cooperateur = testCooperateur;
            tx.transactionType = PartTransactionType.ADDITIONAL_PURCHASE;
            tx.parts = 1L;
            tx.persist();
        }

        Long total = partTransactionRepository.computeTotalParts(testCooperateur);
        assertEquals(100L, total);
    }

    @Test
    @Transactional
    void shouldComputeByIdMethod() {
        PartTransaction tx = new PartTransaction();
        tx.cooperateur = testCooperateur;
        tx.transactionType = PartTransactionType.INITIAL_PURCHASE;
        tx.parts = 7L;
        tx.persist();

        Long total = partTransactionRepository.computeTotalPartsById(testCooperateur.id);
        assertEquals(7L, total);
    }

    @Test
    @Transactional
    void shouldNotMixUpCooperateurs() {
        CooperateurV2 otherCooperateur = new CooperateurV2();
        otherCooperateur.genre = Genre.MADAME;
        otherCooperateur.prenom = "Other";
        otherCooperateur.nom = "User";
        otherCooperateur.telephone = "0611111111";
        otherCooperateur.email = "other-" + System.currentTimeMillis() + "@test.com";
        otherCooperateur.adresse = "2 rue Other";
        otherCooperateur.ville = "OtherVille";
        otherCooperateur.codePostal = "59001";
        otherCooperateur.acceptationDesStatus = true;
        otherCooperateur.persist();

        PartTransaction tx1 = new PartTransaction();
        tx1.cooperateur = testCooperateur;
        tx1.transactionType = PartTransactionType.INITIAL_PURCHASE;
        tx1.parts = 10L;
        tx1.persist();

        PartTransaction tx2 = new PartTransaction();
        tx2.cooperateur = otherCooperateur;
        tx2.transactionType = PartTransactionType.INITIAL_PURCHASE;
        tx2.parts = 5L;
        tx2.persist();

        Long total1 = partTransactionRepository.computeTotalParts(testCooperateur);
        Long total2 = partTransactionRepository.computeTotalParts(otherCooperateur);

        assertEquals(10L, total1);
        assertEquals(5L, total2);
    }
}
