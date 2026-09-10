package com.acme.payments.signing;

import java.security.PublicKey;
import java.nio.charset.StandardCharsets;

/**
 * Signs outbound payment confirmations with a key held in KMS.
 *
 * This class never generates a key and never touches a private key -
 * every signature is produced by asking {@link KmsKeyClient} to sign on
 * this key's behalf. The public key it exposes is the one that the
 * payment-verification-service (a separate repo, a separate team) has
 * on file for this key id.
 */
public class KmsPaymentSigner {

    private static final String PAYMENT_SIGNING_KEY_ID = "kms-key/payment-confirmations";

    private final KmsKeyClient kmsClient;

    public KmsPaymentSigner(KmsKeyClient kmsClient) {
        this.kmsClient = kmsClient;
    }

    public byte[] signConfirmation(String paymentReference, long amountCents) {
        String payload = paymentReference + ":" + amountCents;
        return kmsClient.sign(PAYMENT_SIGNING_KEY_ID, payload.getBytes(StandardCharsets.UTF_8));
    }

    public PublicKey verificationPublicKey() {
        return kmsClient.getPublicKey(PAYMENT_SIGNING_KEY_ID);
    }
}
