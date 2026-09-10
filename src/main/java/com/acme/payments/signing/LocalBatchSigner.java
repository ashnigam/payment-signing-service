package com.acme.payments.signing;

import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * Signs the nightly reconciliation batch file.
 *
 * Unlike {@link KmsPaymentSigner}, this key is generated and held right
 * here - nothing external owns it, and nothing outside this service
 * verifies against it. It is a self-contained signing flow, which is
 * exactly what makes it safe to migrate on its own.
 */
public class LocalBatchSigner {

    private final KeyPair keyPair;

    public LocalBatchSigner() {
        try {
            // Migrated to post-quantum: this key is generated and held right here,
            // with no external custody and no cross-service verifier to coordinate
            // with, so it's safe to move on its own. ML-DSA-65 replaces RSA.
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("ML-DSA-65");
            this.keyPair = kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] signBatch(String batchId, String checksum) {
        try {
            String payload = batchId + ":" + checksum;
            Signature signature = Signature.getInstance("ML-DSA-65");
            signature.initSign(keyPair.getPrivate());
            signature.update(payload.getBytes(StandardCharsets.UTF_8));
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Batch signing failed", e);
        }
    }

    public PublicKey publicKey() {
        return keyPair.getPublic();
    }
}
