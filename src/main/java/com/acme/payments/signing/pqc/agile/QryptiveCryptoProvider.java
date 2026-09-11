package com.acme.payments.signing.pqc.agile;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Crypto provider -- algorithm-agile signing facade (BouncyCastle).
 *
 * SELECTS the signature algorithm from QryptiveCryptoPolicy; it never implements
 * cryptography. Every primitive comes from the BouncyCastle provider (self-registered
 * below, idempotently). To change the algorithm, edit
 * QryptiveCryptoPolicy.SIGNATURE_ALGORITHM -- never this file. This file is yours:
 * Qryptive generated it; you own and maintain it.
 */
public final class QryptiveCryptoProvider {

    static {
        if (java.security.Security.getProvider("BC") == null) {
            java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

    private QryptiveCryptoProvider() {
    }

    /** Algorithm-agile {@link Signature} for the policy's post-quantum algorithm. */
    public static Signature signature() throws NoSuchAlgorithmException {
        try {
            return Signature.getInstance(QryptiveCryptoPolicy.SIGNATURE_ALGORITHM, "BC");
        } catch (NoSuchProviderException e) {
            throw new NoSuchAlgorithmException("BouncyCastle provider not available", e);
        }
    }

    /** Algorithm-agile {@link KeyPairGenerator} for the policy's post-quantum algorithm. */
    public static KeyPairGenerator signatureKeyPairGenerator() throws NoSuchAlgorithmException {
        try {
            return KeyPairGenerator.getInstance(QryptiveCryptoPolicy.SIGNATURE_ALGORITHM, "BC");
        } catch (NoSuchProviderException e) {
            throw new NoSuchAlgorithmException("BouncyCastle provider not available", e);
        }
    }
}
