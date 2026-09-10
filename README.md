# payment-signing-service

A small, deliberately realistic demo service, built to answer one question a CISO asked:
*"What's actually unique about your product?"*

It signs two things:

- **Payment confirmations**, using a key that lives in AWS KMS. The private key never leaves
  KMS; this service only ever calls `sign()` on it. The public key was handed to the
  fraud/settlement team in [`payment-verification-service`](../payment-verification-service) — a
  different repository, a different team — when the key was first provisioned.
- **Nightly reconciliation batches**, using a key this service generates and holds itself.
  Nobody else verifies against it.

Both are RSA today. Both need to move to post-quantum signing eventually. But they are not the
same problem, and that's the point of this repo.

## Three branches, one comparison

| Branch | What it represents |
|---|---|
| `main` | The starting point. Classical RSA, both flows working, tested. |
| `naive-pqc-migration` | What a context-free "migrate this to post-quantum crypto" pass produces. |
| `qryptive-correct-migration` | What a migration that understands custody and cross-repo dependents produces. |

See [`MIGRATION_COMPARISON.md`](MIGRATION_COMPARISON.md) for the full write-up, including why the
naive branch compiles, passes its own tests, and still breaks production.

## A note on honesty

The `naive-pqc-migration` branch is not a captured transcript from any specific named tool. It's
a faithful, hand-built reproduction of the pattern this kind of context-blind migration actually
produces. Nothing here is a strawman: every line in that branch is exactly what you'd get from
asking an assistant to migrate this file with no knowledge of who else depends on the key.
