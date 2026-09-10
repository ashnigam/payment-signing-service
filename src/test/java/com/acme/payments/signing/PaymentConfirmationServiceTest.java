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
    void batchSignatureVerifiesLocally() throws Exception {
        SimulatedKmsKeyClient kms = new SimulatedKmsKeyClient();
        PaymentConfirmationService service = new PaymentConfirmationService(kms);

        byte[] sig = service.signNightlyBatch("BATCH-2026-09-10", "abc123");

        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(service.batchSigner().publicKey());
        verifier.update("BATCH-2026-09-10:abc123".getBytes());
        assertTrue(verifier.verify(sig));
    }
}
