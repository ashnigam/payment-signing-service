package com.acme.payments.signing;

import org.junit.jupiter.api.Test;

import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * The KMS-backed key was deliberately left unmigrated, precisely so that
 * payment-verification-service - a separate repository, owned by a
 * different team - keeps working without any change on its side. This
 * is the same line that service runs when it loads the key it was
 * handed: KeyFactory.getInstance("RSA").generatePublic(...).
 */
class CrossServiceCompatibilityTest {

    @Test
    void theOtherTeamsVerifierStillLoadsThisKeyUnchanged() {
        SimulatedKmsKeyClient kms = new SimulatedKmsKeyClient();
        kms.createKey("kms-key/payment-confirmations");
        PaymentConfirmationService service = new PaymentConfirmationService(kms);

        byte[] publishedPublicKey = service.confirmationSigner().verificationPublicKey().getEncoded();

        assertDoesNotThrow(() ->
                KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publishedPublicKey)));
    }
}
