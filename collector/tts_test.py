"""Generate one cached Italian AI Pulse voice sample with Gemini TTS."""
import base64
import hashlib
import json
import os
from datetime import datetime, timezone
from pathlib import Path

import requests

ROOT = Path(__file__).resolve().parent.parent
FEED = ROOT / "public" / "feed.json"
STATE = ROOT / "public" / "tts_state.json"
AUDIO_DIR = ROOT / "public" / "audio"
MODEL = "gemini-3.8-flash-lite-tts"
API_KEY = os.getenv("GEMINI_API_KEY")

def main():
    if not API_KEY:
        raise SystemExit("GEMINI_API_KEY is required")
    today = datetime.now(timezone.utc).date().isoformat()
    try:
        state = json.loads(STATE.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        state = {}
    if state.get("date") == today and state.get("generated"):
        print("Daily TTS test already generated")
        return

    feed = json.loads(FEED.read_text(encoding="utf-8"))
    items = feed.get("items", [])
    candidates = [
        x for x in items
        if x.get("type") == "news"
        and x.get("editorial_level") in ("ai_curated", "editors_pick")
        and x.get("editorial", {}).get("it")
        and not x.get("ttsAudioUrl")
    ]
    candidates.sort(key=lambda x: (
        0 if x.get("editorial_level") == "editors_pick" else 1,
        -int(x.get("relevance_score", 0)),
        x.get("publishedAt") or ""
    ))
    if not candidates:
        print("No eligible AI Curated Italian news item")
        return

    item = candidates[0]
    it = item["editorial"]["it"]
    transcript = ". ".join(filter(None, [
        it.get("title", ""), it.get("summary", ""), it.get("why_it_matters", "")
    ]))[:5000]
    transcript_hash = hashlib.sha256(transcript.encode("utf-8")).hexdigest()
    payload = {
        "model": MODEL,
        "input": [{
            "type": "user_input",
            "content": [{
                "type": "text",
                "text": transcript,
                "annotations": [{
                    "type": "speech_metadata",
                    "style": "Speak in clear, natural Italian, with a calm, confident, polished technology-news presenter delivery. Moderate pace, precise diction, subtle warmth, no exaggerated enthusiasm."
                }]
            }]
        }],
        "response_format": {"type": "audio"},
        "generation_config": {"speech_config": [{"voice": "Kore"}]}
    }
    response = requests.post(
        "https://generativelanguage.googleapis.com/v1beta/interactions",
        headers={"x-goog-api-key": API_KEY, "Content-Type": "application/json"},
        json=payload, timeout=180
    )
    response.raise_for_status()
    data = response.json()
    audio = next((
        p.get("data") for step in data.get("steps", [])
        if step.get("type") == "model_output"
        for p in step.get("content", [])
        if p.get("type") == "audio" and p.get("data")
    ), None)
    if not audio:
        raise RuntimeError("Gemini returned no audio payload")
    wav = base64.b64decode(audio)
    if not wav.startswith(b"RIFF") or b"WAVE" not in wav[:16]:
        raise RuntimeError("Gemini response is not a WAV file")

    AUDIO_DIR.mkdir(parents=True, exist_ok=True)
    filename = f"{item['id']}-it.wav"
    (AUDIO_DIR / filename).write_bytes(wav)
    item["ttsAudioUrl"] = f"https://raw.githubusercontent.com/paolomulas/ai-pulse/main/public/audio/{filename}"
    item["ttsModel"] = MODEL
    item["ttsVoice"] = "Kore"
    item["ttsLanguage"] = "it"
    item["ttsContentHash"] = transcript_hash
    item["ttsGeneratedAt"] = datetime.now(timezone.utc).isoformat()
    item["ttsBytes"] = len(wav)
    FEED.write_text(json.dumps(feed, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    STATE.write_text(json.dumps({
        "date": today, "generated": True, "item_id": item["id"],
        "model": MODEL, "voice": "Kore", "language": "it", "bytes": len(wav),
        "content_sha256": transcript_hash, "filename": filename
    }, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Generated {filename}: {len(wav)} bytes")

if __name__ == "__main__":
    main()
