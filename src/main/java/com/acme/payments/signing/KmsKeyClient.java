package com.acme.payments.signing;

import java.security.PublicKey;

/**
 * Thin client over a KMS-style key custody service.
 *
 * The private key for {@code keyId} lives entirely inside the KMS. It is
 * generated there, it is used there for every signing operation, and it
 * never crosses this boundary in any form. This interface has no method
 * that returns a private key, on purpose - that omission is the whole
 * security property.
 */
public interface KmsKeyClient {

    /** Signs {@code message} using the private key held for {@code keyId}. The key itself never leaves the KMS. */
    byte[] sign(String keyId, byte[] message);

    /** Returns the public key for {@code keyId}, safe to hand to any verifier. */
    PublicKey getPublicKey(String keyId);
}
