# payment-signing-service

A small, deliberately realistic demo service, built to answer one question a CISO asked:
*"What's actually unique about your product?"*

It signs two things:

- **Payment confirmations**, using a key that lives in AWS KMS. The private key never leaves
  KMS; this service only ever calls `sign()` on it. The public key was handed to the
  fraud/settlement team in [`payment-verification-service`](../payment-verification-service) — a
  different repository, a different team — when the key was first provisioned.
- **Nightly reconciliation batches**, using a key generated fresh and used within one method,
  never stored anywhere.

Both are RSA today. Both need to move to post-quantum signing eventually. But they are not the
same problem, and that's the point of this repo.

## Two pull requests, one comparison

| Pull request | What it represents |
|---|---|
| [#2](https://github.com/ashnigam/payment-signing-service/pull/2) | What a context-free "migrate this to post-quantum crypto" pass produces — hand-built, disclosed as such. |
| [#4](https://github.com/ashnigam/payment-signing-service/pull/4) | The unedited output of the real Qryptive GitHub App, run against this exact code. |

See [`MIGRATION_COMPARISON.md`](MIGRATION_COMPARISON.md) for the full write-up, including why
#2 compiles and passes its own tests while still breaking a repository it never opens, and why
#4's build is red on purpose.

## A note on honesty

Pull request #2 is not a captured transcript from any specific named tool. It's a faithful,
hand-built reproduction of the pattern a context-blind migration actually produces. Pull request
#4 is the one thing in this repo that isn't hand-built at all — it's real, unedited output from
the deployed product, including the parts that don't look flattering (a refusal, a red build,
a dependency it chose not to add for you).
