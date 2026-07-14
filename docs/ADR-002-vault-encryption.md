# ADR-002: AES-256-GCM via Android Keystore for Medical Vault encryption

## Status
Accepted

## Context
FR-5.2 requires the Medical Record Vault to store data encrypted at rest, with no
plaintext medical data ever written to disk. Two realistic options: SQLCipher (encrypts
the entire Room database file) or field-level encryption using the Android Keystore
before data reaches Room.

## Decision
Field-level encryption: `VaultCipher` encrypts each record's content with AES-256-GCM
using a key generated and held entirely inside the Android Keystore (the raw key material
never exists in application memory or on disk). `VaultRepositoryImpl` encrypts before
`dao.upsert()` and decrypts after `dao.observeAll()` — Room and Firestore only ever see
ciphertext.

## Consequences
**Positive:**
- No dependency on a third-party native library (SQLCipher ships a native `.so` per ABI,
  which complicates the build and increases APK size).
- Encryption key is hardware-backed on devices with a StrongBox/TEE, and is
  non-exportable — even a rooted-device file extraction yields ciphertext only.
- Encryption logic is small, auditable, and independently unit-testable.

**Negative / tradeoffs:**
- Only vault *content* is encrypted, not table/column names or record counts (an attacker
  with file access could see "3 vault records exist" even without reading them). Full-disk
  encryption via SQLCipher would hide this too — judged an acceptable tradeoff for v1
  given the significantly simpler build and no native dependency.
- Field-level encryption means SQL queries can't filter/search on encrypted content — the
  vault currently returns all records and filters cross-in-memory instead of via SQL
  WHERE clauses. Fine at expected personal-use data volumes (dozens of records, not
  millions).
