package com.acme.payments.signing;

import org.junit.jupiter.api.Test;

import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This service's own test suite is green. This test shows what happens
 * the moment the public key it now publishes reaches
 * payment-verification-service, in the other repo, which still expects
 * the RSA key it was handed when payment-confirmations was first
 * provisioned.
 *
 * This is the exact line payment-verification-service runs when it
 * loads that key: KeyFactory.getInstance("RSA").generatePublic(...).
 * Nothing in this repository's build, tests, or diff would have shown
 * this failure - it only shows up in the other team's repository, in
 * production, on the first payment confirmation this key ever signs.
 */
class CrossServiceCompatibilityTest {

    @Test
    void theOtherTeamsVerifierCanNoLongerLoadThisKey() throws Exception {
        SimulatedKmsKeyClient kms = new SimulatedKmsKeyClient();
        kms.createKey("kms-key/payment-confirmations");
        PaymentConfirmationService service = new PaymentConfirmationService(kms);

        byte[] publishedPublicKey = service.confirmationSigner().verificationPublicKey().getEncoded();

        assertThrows(java.security.spec.InvalidKeySpecException.class, () ->
                KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publishedPublicKey)));
    }
}
