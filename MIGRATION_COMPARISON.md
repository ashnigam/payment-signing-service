# Two migrations of the same code

Same starting point (`main`), two different post-quantum migrations, one question: does the
result still work in production?

## What a context-free migration does — `naive-pqc-migration`

Ask an assistant to "migrate this service to post-quantum cryptography" with no other context,
and it does the reasonable-looking thing: it finds every `KeyPairGenerator.getInstance("RSA")`
and `Signature.getInstance("SHA256withRSA")` in the codebase and swaps them for their ML-DSA-65
equivalents. That includes `LocalBatchSigner`, where it's exactly the right call. It also includes
`SimulatedKmsKeyClient`, which stands in for AWS KMS — and there it's wrong, because nothing at
the text level distinguishes "a key we generate and hold" from "a key we're pretending to hold
custody of on someone else's behalf."

The result:

- Compiles.
- Its own two unit tests pass.
- A third test, `CrossServiceCompatibilityTest`, proves the actual damage: the public key this
  service now publishes can no longer even be *parsed* as an RSA key by
  `payment-verification-service` — a completely different repository, owned by a different team,
  that was handed the original public key out of band and has no way to know it changed. That
  team's payment confirmation checks start failing the moment this ships, for reasons nothing in
  this repo's CI would ever catch.
- Separately, and just as fatal on its own: AWS KMS does not support ML-DSA signing keys. This
  migration could not be deployed against the real KMS it's supposed to be calling, even setting
  the verifier problem aside.

Every line in this branch is real, compiling code — not a strawman. It's what you get from
treating every RSA call site as interchangeable, because the code itself never told the migration
which ones are safe to touch alone.

## What a migration that understands the system does — `qryptive-correct-migration`

- `LocalBatchSigner` — migrated to ML-DSA-65. Self-contained, no external dependents, safe to
  change on its own.
- `KmsPaymentSigner` / `SimulatedKmsKeyClient` — left as RSA, with a clear note explaining why:
  the key lives in KMS, KMS has no post-quantum signing algorithm yet, and a separate repository
  depends on the exact key and algorithm this one publishes today. Migrating this site requires
  coordinating with that KMS platform team and with whoever owns
  `payment-verification-service` — not a decision this repo can make alone.

Nothing in this branch breaks. Nothing in this branch pretends the KMS-backed flow was handled
when it wasn't.

## The actual point

The difference isn't code quality. Both branches are well-written Java. The difference is that
one migration knows where a key lives, who else depends on it, and what that means for the order
and scope of changes — and the other doesn't, because that information was never visible in the
file being edited. That's the thing worth asking about.
