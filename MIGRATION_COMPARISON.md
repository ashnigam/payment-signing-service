# Two migrations of the same code

Same starting point (`main`), two different attempts at a post-quantum migration, one
question: does the result still work in production?

## What a context-free migration does — pull request #2 (`naive-pqc-migration`)

Ask an assistant to "migrate this service to post-quantum cryptography" with no other context,
and it does the reasonable-looking thing: it finds every `KeyPairGenerator.getInstance("RSA")`
and `Signature.getInstance("SHA256withRSA")` in the codebase and swaps them for their ML-DSA-65
equivalents. That includes `SimulatedKmsKeyClient`, which stands in for AWS KMS — and there it's
wrong, because nothing at the text level distinguishes "a key we generate and hold" from "a key
we're pretending to hold custody of on someone else's behalf."

The result: compiles, its own tests pass, and a third test —
`CrossServiceCompatibilityTest` — proves the actual damage: the public key this service now
publishes can no longer even be *parsed* as an RSA key by `payment-verification-service`, a
completely different repository this migration never opened.

Every line in this branch is real, hand-written code, built to faithfully reproduce that
failure mode — not a captured transcript from a specific named tool.

## What the real Qryptive product does — pull request #4 (`pqc-fix/repo-migration`)

This one isn't hand-written. It's the unedited output of the actual Qryptive GitHub App,
triggered from the dashboard against this exact code.

- `LocalBatchSigner` — migrated, through a generated facade (`QryptiveCryptoProvider`, reading
  its algorithm from a one-line `QryptiveCryptoPolicy`), so a future move from ML-DSA-65 to
  ML-DSA-87 is a policy edit, not a re-migration.
- `SimulatedKmsKeyClient` — left alone, with the exact reason in the pull request body: the key's
  origin can't be proven local in this file, and migrating it in place would break verification
  against the existing key.

The pull request also says things nobody asked it to say: that ML-DSA-65 signatures run about
3,300 bytes against RSA-2048's 256, so anything sized for the old signature needs a second look;
and that no verifier was found anywhere in the scanned code for the key it did migrate, so
whoever checks that signature elsewhere needs to upgrade too.

Its build is red right now — on purpose. The migrated file needs one dependency
(`org.bouncycastle:bcprov-jdk18on`) that the tool deliberately did not add to the build file
itself; it named the exact line to add, in the pull request body, and left the rest to a human.

## The actual point

A dependency map tells you where a key is used. It doesn't tell you which of those uses you're
allowed to change on your own — that only comes from actually tracing how the cryptography is
used, not just where it's called. And a tool honest enough to leave a build red with the fix
named is worth more than one that ships something broken and green.
