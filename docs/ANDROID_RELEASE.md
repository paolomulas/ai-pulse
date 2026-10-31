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

If this is the first Play release, create a dedicated upload key on a trusted local Windows machine. Keep a secure offline backup of the JKS file and its passwords:

```powershell
keytool -genkeypair -v -keystore ai-pulse-upload.jks -keyalg RSA -keysize 2048 -validity 10000 -alias ai-pulse-upload
$bytes = [System.IO.File]::ReadAllBytes("$PWD\ai-pulse-upload.jks")
[Convert]::ToBase64String($bytes) | Set-Clipboard
```

Add these repository Actions secrets in GitHub (Settings → Secrets and variables → Actions):
- `ANDROID_UPLOAD_KEYSTORE_BASE64`: the Base64 value copied to the clipboard
- `ANDROID_KEY_ALIAS`: `ai-pulse-upload` (or the alias selected above)
- `ANDROID_KEYSTORE_PASSWORD`: the keystore password
- `ANDROID_KEY_PASSWORD`: the key password

Do not paste passwords or the Base64 keystore into issues, commits, or chat. If an existing upload key has already been registered in Play Console, reuse that key; do not generate a replacement without following Google's upload-key reset process.

After adding the secrets, run the Android build workflow again. Confirm the release AAB is signed before uploading it to Play Console.

## Google Play preparation
- Use the public privacy policy: `https://paolomulas.github.io/ai-pulse/privacy.html`
- Complete the Data safety form from the actual shipped build; the current native app has no account, ads, analytics, tracking SDK, or personal-data collection feature and requests only Internet access.
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
