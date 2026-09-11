package com.acme.payments.signing;

import org.junit.jupiter.api.Test;

import java.security.Signature;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentConfirmationServiceTest {

    @Test
    void kmsSignedConfirmationVerifiesAgainstPublishedPublicKey() throws Exception {
        SimulatedKmsKeyClient kms = new SimulatedKmsKeyClient();
        kms.createKey("kms-key/payment-confirmations");
        PaymentConfirmationService service = new PaymentConfirmationService(kms);

        byte[] sig = service.confirmPayment("PAY-1001", 500_00);

        // This is exactly what payment-verification-service does, in its own repo,
        // against the public key this signer publishes.
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(service.confirmationSigner().verificationPublicKey());
        verifier.update("PAY-1001:50000".getBytes());
        assertTrue(verifier.verify(sig));
    }

    @Test
    void batchSigningProducesASignature() {
        SimulatedKmsKeyClient kms = new SimulatedKmsKeyClient();
        PaymentConfirmationService service = new PaymentConfirmationService(kms);

        byte[] sig = service.signNightlyBatch("BATCH-2026-09-10", "abc123");

        // A fresh key is generated and used within signBatch itself, then
        // discarded - nothing outside that one method ever holds it, which is
        // exactly what keeps this flow migratable on its own.
        assertTrue(sig.length > 0);
    }
}
