package com.acme.payments.signing;

/**
 * Entry point used by the rest of the payments platform.
 */
public class PaymentConfirmationService {

    private final KmsPaymentSigner confirmationSigner;
    private final LocalBatchSigner batchSigner;

    public PaymentConfirmationService(KmsKeyClient kmsClient) {
        this.confirmationSigner = new KmsPaymentSigner(kmsClient);
        this.batchSigner = new LocalBatchSigner();
    }

    /** Signs a customer-facing payment confirmation. Verified by payment-verification-service. */
    public byte[] confirmPayment(String paymentReference, long amountCents) {
        return confirmationSigner.signConfirmation(paymentReference, amountCents);
    }

    /** Signs the internal nightly reconciliation batch. Verified only within this service. */
    public byte[] signNightlyBatch(String batchId, String checksum) {
        return batchSigner.signBatch(batchId, checksum);
    }

    public KmsPaymentSigner confirmationSigner() {
        return confirmationSigner;
    }

    public LocalBatchSigner batchSigner() {
        return batchSigner;
    }
}
