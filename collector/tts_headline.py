"""Generate one multilingual, cached AI Pulse lead-story narration (one-time assets)."""
import base64
import hashlib
import json
import os
import re
from pathlib import Path

import requests

ROOT = Path(__file__).resolve().parent.parent
FEED = ROOT / "public" / "feed.json"
AUDIO_DIR = ROOT / "public" / "audio"
MODEL = "gemini-3.8-flash-lite-tts"
API_KEY = os.getenv("GEMINI_API_KEY")
TARGET_ID = "a86d24c59c826150d16deb117d5d2c5c4ade94e1abd42be5bfff46adde0bd560"
LANGUAGES = ("it", "en", "es", "zh-CN", "de")
STYLE = {
    "it": "Parla in italiano naturale, con voce da narratore di una rivista di divulgazione tecnologica: tono autorevole, chiaro e coinvolgente, ritmo moderato, dizione precisa, senza enfasi pubblicitaria.",
    "en": "Speak in natural English like a thoughtful technology magazine narrator: clear, confident and engaging, moderate pace, precise diction, no promotional exaggeration.",
    "es": "Habla en español natural, como narrador de una revista de divulgación tecnológica: tono claro, seguro y atractivo, ritmo moderado, dicción precisa, sin exageraciones promocionales.",
    "zh-CN": "请使用自然流畅的普通话，以科技杂志解说员的风格朗读：清晰、自信、有吸引力，语速适中，吐字准确，不要夸张宣传。",
    "de": "Sprich in natürlichem Deutsch wie ein Sprecher eines Technologiemagazins: klar, souverän und interessant, moderates Tempo, präzise Aussprache, ohne werbliche Übertreibung.",
}

def main():
    if not API_KEY:
        raise SystemExit("GEMINI_API_KEY is required")
    feed = json.loads(FEED.read_text(encoding="utf-8"))
    items = feed.get("items", [])
    item = next((x for x in items if x.get("id") == TARGET_ID), None)
    if not item:
        raise SystemExit("Configured AI Pulse lead story not found")
    if item.get("editorial_level") not in ("ai_curated", "editors_pick"):
        raise SystemExit("Lead story must be editorially curated before TTS generation")
    editorial = item.get("editorial") or {}
    audio_map = item.setdefault("ttsAudioByLanguage", {})
    generated = []
    for lang in LANGUAGES:
        localized = editorial.get(lang) or {}
        if not all(localized.get(k) for k in ("title", "summary", "why_it_matters")):
            raise SystemExit(f"Missing complete editorial text for {lang}")
        if audio_map.get(lang):
            continue
        transcript = ". ".join(localized[k].strip() for k in ("title", "summary", "why_it_matters") if localized.get(k))
        digest = hashlib.sha256(transcript.encode("utf-8")).hexdigest()
        payload = {
            "model": MODEL,
            "input": [{
                "type": "user_input",
                "content": [{
                    "type": "text",
                    "text": transcript,
                    "annotations": [{"type": "speech_metadata", "style": STYLE[lang]}],
                }],
            }],
            "response_format": {"type": "audio"},
            "generation_config": {"speech_config": [{"voice": "Kore"}]},
        }
        response = requests.post(
            "https://generativelanguage.googleapis.com/v1beta/interactions",
            headers={"x-goog-api-key": API_KEY, "Content-Type": "application/json"},
            json=payload,
            timeout=180,
        )
        response.raise_for_status()
        data = response.json()
        encoded = next((
            part.get("data")
            for step in data.get("steps", []) if step.get("type") == "model_output"
            for part in step.get("content", []) if part.get("type") == "audio" and part.get("data")
        ), None)
        if not encoded:
            raise RuntimeError(f"Gemini returned no audio for {lang}")
        wav = base64.b64decode(encoded)
        if not wav.startswith(b"RIFF") or b"WAVE" not in wav[:16]:
            raise RuntimeError(f"Gemini returned an invalid WAV for {lang}")
        suffix = re.sub(r"[^a-z0-9-]", "-", lang.lower())
        filename = f"{TARGET_ID}-{suffix}.wav"
        AUDIO_DIR.mkdir(parents=True, exist_ok=True)
        (AUDIO_DIR / filename).write_bytes(wav)
        audio_map[lang] = f"https://raw.githubusercontent.com/paolomulas/ai-pulse/main/public/audio/{filename}"
        generated.append({"language": lang, "file": filename, "bytes": len(wav), "sha256": digest})
    item["ttsModel"] = MODEL
    item["ttsVoice"] = "Kore"
    item["ttsGeneratedLanguages"] = sorted(audio_map.keys())
    FEED.write_text(json.dumps(feed, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({"story_id": TARGET_ID, "generated": generated, "available_languages": sorted(audio_map.keys())}, ensure_ascii=False))

if __name__ == "__main__":
    main()
