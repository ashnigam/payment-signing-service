package com.acme.payments.signing;

import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * Signs the nightly reconciliation batch file.
 *
 * The signing key is generated fresh for each batch, used immediately,
 * and never stored anywhere in this service. The whole lifecycle of
 * the key, from creation to use, happens in this one method, which is
 * what makes this flow safe to migrate on its own.
 */
public class LocalBatchSigner {

    public byte[] signBatch(String batchId, String checksum) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair keyPair = kpg.generateKeyPair();

            String payload = batchId + ":" + checksum;
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(payload.getBytes(StandardCharsets.UTF_8));
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Batch signing failed", e);
        }
    }
}
