# AI Pulse — Audio asset policy

## Product rule

Generated narration is a **premium editorial asset**, not a default transformation of every feed item. Audio must be selective, durable and inexpensive to operate.

## Eligibility

- Only news items with editorial level `editors_pick` or `ai_curated` are eligible.
- `editors_pick` has priority over `ai_curated`; within each tier, higher relevance scores are preferred.
- Raw Feed items and podcast episodes are not candidates for Gemini TTS. Podcast playback continues to use the original publisher audio URL.

## Generation and quota protection

- Generation is manual via GitHub Actions (`workflow_dispatch`).
- The current generator creates at most one Italian audio asset per UTC day.
- Playback never calls Gemini and never regenerates an asset.
- No scheduled TTS workflow, automatic retries, paid billing, or fallback TTS provider is enabled.
- If the free quota is exhausted, stop. Do not retry repeatedly or switch to a paid tier.
- Check the active model limits in Google AI Studio before increasing generation volume. A zero-price tier is not an unlimited tier.

## Persistence and metadata

- Store generated WAV files under `public/audio/`.
- Link the asset from its feed item through `ttsAudioUrl`.
- Store model, voice, language, content SHA-256, generation timestamp and byte size on the feed item.
- Keep a daily state record in `public/tts_state.json` to prevent duplicate same-day generation.
- Reuse an existing asset; never generate audio at playback time.

## Editorial and technical constraints

- Narration is built only from the localized editorial title, summary and why-it-matters fields.
- Voice: Gemini TTS, model `gemini-3.8-flash-lite-tts`, voice `Kore` for the current Italian sample.
- Keep the generated asset separate from the editorial text so the TTS provider can be replaced without changing the editorial pipeline.
- Do not claim that podcast audio has been listened to or analyzed when only feed metadata was used.

## Storage discipline

Audio files are binary assets and can grow the Git repository quickly. Keep the selection narrow, avoid multiple versions for unchanged content, and review repository size before expanding languages or volume. If audio volume grows materially, move binary assets to object storage/CDN rather than treating the Git repository as an unlimited media host.

## Current operating mode

**Manual, one asset/day, Italian only.** Broader language coverage and any automated production schedule require a separate quota and storage review.
