package com.acme.payments.signing.pqc.agile;

/**
 * Crypto policy -- the one place to change your post-quantum signature algorithm.
 *
 * Every signing call site migrated by Qryptive routes through QryptiveCryptoProvider,
 * which reads QryptiveCryptoPolicy.SIGNATURE_ALGORITHM, so changing the value below
 * changes the algorithm everywhere with NO code migration. This file is yours:
 * Qryptive generated it; you own and maintain it.
 */
public final class QryptiveCryptoPolicy {

    private QryptiveCryptoPolicy() {
    }

    /**
     * Post-quantum signature algorithm (NIST FIPS 204 / ML-DSA).
     * Supported: "ML-DSA-44", "ML-DSA-65", "ML-DSA-87".
     */
    public static final String SIGNATURE_ALGORITHM = "ML-DSA-65";
}
