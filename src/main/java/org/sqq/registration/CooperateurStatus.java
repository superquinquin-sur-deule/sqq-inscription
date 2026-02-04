package org.sqq.registration;

public enum CooperateurStatus {
    PAYMENT_PENDING("Paiement en cours"),
    PAID("Payé"),
    PROCESSED("Traité"),
    ARCHIVED("Archivé");

    private final String label;
    
    CooperateurStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
