# Android release checklist — AI Pulse

## Build
- Package: `com.aipulse.app`
- Version: `0.3.0` (`versionCode = 3`)
- Compile / target SDK: Android 16, API 36
- Minimum SDK: API 26
- CI workflow: `.github/workflows/android-build.yml`
- CI artifacts: debug APK and release AAB (14-day retention)

## Upload-key signing

The release workflow signs the AAB only when an upload keystore is configured. Never commit the keystore or passwords to Git.

A dedicated first-release upload key has been generated for this unpublished app. Use the private JKS and password files delivered separately in the `ai-pulse-signing-package.zip` handoff. The package contains the JKS, Base64 payload, alias instructions and password file. Keep the JKS and password in secure offline backups; never commit them to Git.

Add these repository Actions secrets in GitHub (Settings → Secrets and variables → Actions):
- `ANDROID_UPLOAD_KEYSTORE_BASE64`: complete contents of `ANDROID_UPLOAD_KEYSTORE_BASE64.txt`
- `ANDROID_KEY_ALIAS`: `ai-pulse-upload`
- `ANDROID_KEYSTORE_PASSWORD`: contents of `PASSWORD-KEYSTORE.txt`
- `ANDROID_KEY_PASSWORD`: same value as `ANDROID_KEYSTORE_PASSWORD`

The workflow now validates that either all four signing secrets are present or none are present. When configured, it validates the keystore alias before building and verifies the resulting AAB signature. A partial configuration fails the workflow instead of silently producing an unsigned release bundle.

Do not paste passwords or the Base64 keystore into issues, commits, or chat. If an existing upload key has already been registered in Play Console, reuse that key; do not generate a replacement without following Google's upload-key reset process.

After adding the secrets, run the Android build workflow again. Confirm the release AAB is signed before uploading it to Play Console.

## Google Play preparation
- Use the public privacy policy: `https://paolomulas.github.io/ai-pulse/privacy.html`
- Use the public publisher contact page: `https://paolomulas.github.io/ai-pulse/contact.html`
- Complete the Data safety form from the actual shipped build. The native app has no account, ads, analytics or tracking SDK and requests only Internet access; however, GitHub receives normal network connection metadata, including the source IP. Review Google's approximate-location-by-IP guidance and the provider's retention before deciding whether any data type must be declared.
- Complete the content rating questionnaire.
- Provide real phone screenshots, app icon and store listing assets.
- Verify the developer contact details and app access declarations.
- Start with internal testing, then closed testing if required by the developer account.

For personal Play Console accounts created after 13 November 2023, Google currently requires at least 12 testers opted into a closed test continuously for 14 days before production access can be requested. Confirm the account type and creation date in Play Console.

## Editorial integrity
- Simone Rizzo's YouTube channel is a discovery radar only.
- Never publish, embed, redistribute, or link to Simone's videos as AI Pulse content.
- Extract candidate references from the channel metadata, fetch and validate the original primary-source page, and publish only an original AI Pulse news presentation linked to that primary source.
- Speak Up podcast entries must retain their original publisher links; AI Pulse must not claim to have listened to or transcribed episodes unless that actually occurred.
