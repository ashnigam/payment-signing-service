package com.acme.payments.signing;

import java.security.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Stands in for AWS KMS in this demo, without calling AWS.
 *
 * It reproduces the one property that matters for this demo: a key's
 * private half is generated inside this class and is never returned by
 * any public method. Every caller outside this class can only ask it to
 * sign something, or ask for the public key.
 */
public final class SimulatedKmsKeyClient implements KmsKeyClient {

    private final Map<String, KeyPair> custody = new HashMap<>();

    public SimulatedKmsKeyClient() {
    }

    // NOT MIGRATED - left on RSA. This key is held in KMS custody, which has no
    // post-quantum signing algorithm to move to yet, and its public key is
    // depended on by payment-verification-service in a separate repository.
    // Migrating this site requires the KMS platform team (to provision a new
    // key once KMS supports one) and the owners of payment-verification-service
    // (to accept the new key and algorithm) - not a change this repo can make
    // alone. See MIGRATION_COMPARISON.md.

    /** Provisions a new managed key, as if calling KMS CreateKey. Returns only the key id. */
    public String createKey(String keyId) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            custody.put(keyId, kpg.generateKeyPair());
            return keyId;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] sign(String keyId, byte[] message) {
        KeyPair kp = requireKey(keyId);
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(kp.getPrivate());
            signature.update(message);
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("KMS signing operation failed", e);
        }
    }

    @Override
    public PublicKey getPublicKey(String keyId) {
        return requireKey(keyId).getPublic();
    }

    private KeyPair requireKey(String keyId) {
        KeyPair kp = custody.get(keyId);
        if (kp == null) {
            throw new IllegalArgumentException("Unknown KMS key id: " + keyId);
        }
        return kp;
    }
}
