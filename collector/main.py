"""AI Pulse collector: public RSS/Atom feeds and GitHub Search API."""
import argparse
import hashlib
import html
import json
import os
import re
from datetime import datetime, timedelta, timezone
from pathlib import Path
from urllib.parse import urlsplit, urlunsplit

import feedparser
import requests

ROOT = Path(__file__).parent
CONFIG = json.loads((ROOT / "sources.json").read_text(encoding="utf-8"))
HEADERS = {"User-Agent": "AIPulseCollector/0.3 (+https://github.com/paolomulas/ai-pulse)"}

def iso_now():
    return datetime.now(timezone.utc).isoformat()

def canonical(url):
    p = urlsplit(url.strip())
    return urlunsplit((p.scheme, p.netloc.lower(), p.path.rstrip("/"), p.query, ""))

def item_id(url):
    return hashlib.sha256(canonical(url).encode()).hexdigest()

def clean_text(value, limit=1800):
    value = re.sub(r"<[^>]+>", " ", value or "")
    value = html.unescape(value)
    return re.sub(r"\s+", " ", value).strip()[:limit]

def published(entry):
    for key in ("published_parsed", "updated_parsed"):
        value = entry.get(key)
        if value:
            try:
                return datetime(*value[:6], tzinfo=timezone.utc).isoformat()
            except (TypeError, ValueError):
                pass
    return None

# Creator videos are a discovery radar only. We publish only source pages
# fetched from a deliberately small allow-list of primary publishers/projects.
PRIMARY_SOURCE_DOMAINS = {
    "arxiv.org", "github.com", "huggingface.co", "openai.com", "anthropic.com",
    "deepmind.google", "blog.google", "ai.google.dev", "research.google",
    "mistral.ai", "kimi.ai", "moonshot.ai", "nvidia.com", "microsoft.com",
    "meta.com", "ai.meta.com", "x.ai", "stability.ai", "cohere.com",
    "databricks.com", "cloud.google.com", "aws.amazon.com", "developer.android.com",
    "source.android.com", "pytorch.org", "tensorflow.org", "ollama.com",
    "linuxfoundation.org", "mozilla.org", "huggingface.co"
}
PROMOTIONAL_OR_SOCIAL_DOMAINS = {
    "youtube.com", "youtu.be", "instagram.com", "facebook.com", "linkedin.com",
    "x.com", "twitter.com", "tiktok.com", "patreon.com", "rizzoaiacademy.com"
}
MAX_CREATOR_SOURCE_ITEMS = 8

def approved_primary_url(url):
    try:
        parsed = urlsplit(url)
        host = (parsed.hostname or "").lower()
        if parsed.scheme != "https" or not host or host in PROMOTIONAL_OR_SOCIAL_DOMAINS:
            return False
        if any(host == social or host.endswith("." + social) for social in PROMOTIONAL_OR_SOCIAL_DOMAINS):
            return False
        return any(host == domain or host.endswith("." + domain) for domain in PRIMARY_SOURCE_DOMAINS)
    except ValueError:
        return False

def primary_source_name(url):
    host = (urlsplit(url).hostname or "").lower().removeprefix("www.")
    labels = {
        "arxiv.org": "arXiv", "github.com": "GitHub",
        "huggingface.co": "Hugging Face", "openai.com": "OpenAI",
        "anthropic.com": "Anthropic", "deepmind.google": "Google DeepMind",
        "blog.google": "Google", "ai.google.dev": "Google AI",
        "research.google": "Google Research", "mistral.ai": "Mistral AI",
        "kimi.ai": "Kimi / Moonshot AI", "moonshot.ai": "Moonshot AI",
        "nvidia.com": "NVIDIA", "microsoft.com": "Microsoft",
        "meta.com": "Meta", "ai.meta.com": "Meta AI",
        "x.ai": "xAI", "stability.ai": "Stability AI", "cohere.com": "Cohere",
        "databricks.com": "Databricks", "ollama.com": "Ollama"
    }
    return labels.get(host, host)

def fetch_primary_page(url):
    """Fetch an allow-listed primary page and extract only public page metadata/text."""
    try:
        response = requests.get(url, headers=HEADERS, timeout=18, allow_redirects=True)
        response.raise_for_status()
        final_url = response.url
        if not approved_primary_url(final_url):
            return None
        content_type = response.headers.get("content-type", "").lower()
        if "html" not in content_type and "text/" not in content_type:
            return None
        markup = response.text[:1_500_000]
        meta = {}
        for tag in re.findall(r"<meta\b[^>]*>", markup, flags=re.I):
            attrs = {
                key.lower(): html.unescape(value)
                for key, value in re.findall(r"""([a-zA-Z_:][-a-zA-Z0-9_:.]*)\s*=\s*["']([^"']*)["']""", tag)
            }
            key = (attrs.get("property") or attrs.get("name") or "").lower()
            if key in {"og:title", "twitter:title", "og:description", "description", "twitter:description"} and attrs.get("content"):
                meta[key] = clean_text(attrs["content"], 1200)
        title_match = re.search(r"<title\b[^>]*>(.*?)</title>", markup, flags=re.I | re.S)
        title = meta.get("og:title") or meta.get("twitter:title") or (clean_text(title_match.group(1), 400) if title_match else "")
        description = meta.get("og:description") or meta.get("description") or meta.get("twitter:description") or ""
        visible = re.sub(r"<(script|style|noscript|svg)\b[^>]*>.*?</\1>", " ", markup, flags=re.I | re.S)
        visible = clean_text(visible, 5000)
        evidence = (description + " " + visible).strip()[:4200]
        if not title or len(evidence) < 180:
            return None
        return {"url": canonical(final_url), "title": title, "description": description, "evidence": evidence}
    except Exception as exc:
        print(f"WARN primary source fetch {url}: {exc}")
        return None

def resolve_creator_radar(leads, existing_urls=None):
    """Turn video-discovered primary links into original-source stories; never emit videos."""
    existing = set(existing_urls or [])
    created, seen = [], set()
    for lead in leads:
        for candidate in lead.get("primarySourceCandidates", []):
            if len(created) >= MAX_CREATOR_SOURCE_ITEMS:
                break
            if not approved_primary_url(candidate):
                continue
            page = fetch_primary_page(candidate)
            if not page:
                continue
            source_url = page["url"]
            key = canonical(source_url)
            if key in existing or key in seen:
                continue
            seen.add(key)
            created.append({
                "id": item_id(source_url),
                "type": "news",
                "title": page["title"],
                "summary": page["description"] or page["evidence"][:900],
                "sourceName": primary_source_name(source_url),
                "sourceUrl": source_url,
                "publishedAt": lead.get("publishedAt"),
                "collectedAt": iso_now(),
                "sourceKind": "primary_source",
                "sourceWeight": 10,
                "sourceEvidence": page["evidence"],
                "sourceVerificationLevel": "official_primary_page_fetched",
                "tags": ["AI", "PRIMARY SOURCE"],
                "attribution": primary_source_name(source_url)
            })
    print(f"Creator radar: {len(leads)} discovery videos; {len(created)} original-source pages fetched; video posts suppressed")
    return created

def collect_rss():
    out, creator_leads = [], []
    for source in CONFIG.get("rss", []):
        try:
            feed = feedparser.parse(source["url"], request_headers=HEADERS)
            if getattr(feed, "bozo", False):
                print(f"WARN feed parse issue {source['name']}: {feed.get('bozo_exception')}")
            for entry in feed.entries[:40]:
                url = entry.get("link", "")
                if not url.startswith("https://"):
                    continue
                raw_summary = entry.get("summary", entry.get("description", "")) or ""
                summary_text = clean_text(raw_summary)
                candidate_links = list(dict.fromkeys(re.findall(r'https?://\S+', html.unescape(raw_summary))))
                candidate_links = [u.rstrip(".,);]") for u in candidate_links if urlsplit(u).netloc.lower() not in {"youtube.com", "www.youtube.com", "youtu.be"}]
                item = {
                    "id": item_id(url), "type": "news",
                    "title": clean_text(entry.get("title", "Senza titolo"), 400),
                    "summary": summary_text,
                    "sourceName": source["name"], "sourceUrl": url,
                    "publishedAt": published(entry), "collectedAt": iso_now(),
                    "sourceKind": source.get("kind", "newsroom"),
                    "sourceWeight": source.get("weight", 5),
                    "primarySourceCandidates": candidate_links[:12] if source.get("kind") == "creator_radar" else [],
                    "tags": ["AI", "CREATOR RADAR"] if source.get("kind") == "creator_radar" else ["AI"],
                    "attribution": source["name"]
                }
                if source.get("kind") == "creator_radar":
                    creator_leads.append(item)
                else:
                    out.append(item)
        except Exception as exc:
            print(f"WARN {source['name']}: {exc}")
    existing_urls = {canonical(item.get("sourceUrl", "")) for item in out if item.get("sourceUrl")}
    out.extend(resolve_creator_radar(creator_leads, existing_urls))
    return out

def collect_podcasts():
    """Collect recent episodes from curated, public podcast RSS feeds."""
    out = []
    for show in CONFIG.get("podcast_feeds", []):
        try:
            feed = feedparser.parse(show["url"], request_headers=HEADERS)
            if getattr(feed, "bozo", False):
                print(f"WARN podcast feed parse issue {show['name']}: {feed.get('bozo_exception')}")
            image = feed.feed.get("image", {}).get("href", "")
            for entry in feed.entries[:12]:
                url = entry.get("link", "")
                if not url.startswith("https://"):
                    continue
                enclosures = entry.get("enclosures", [])
                audio_url = next((e.get("href", "") for e in enclosures if e.get("href", "").startswith("https://")), "")
                duration = entry.get("itunes_duration", "")
                out.append({
                    "id": item_id(url), "type": "speak_up",
                    "title": clean_text(entry.get("title", "Episodio senza titolo"), 400),
                    "summary": clean_text(entry.get("summary", entry.get("description", ""))),
                    "sourceName": show["name"], "showName": show["name"],
                    "sourceUrl": url, "audioUrl": audio_url,
                    "publishedAt": published(entry), "collectedAt": iso_now(),
                    "duration": clean_text(str(duration), 30),
                    "language": show.get("language", "en"),
                    "podcastCategory": show.get("category", "frontier"),
                    "podcastDescription": show.get("description", ""),
                    "coverImage": image, "tags": ["AI", "PODCAST"],
                    "attribution": show["name"]
                })
        except Exception as exc:
            print(f"WARN podcast {show['name']}: {exc}")
    return out

def collect_github():
    out, seen = [], set()
    since = (datetime.now(timezone.utc) - timedelta(days=30)).date().isoformat()
    for term in CONFIG.get("github_queries", []):
        try:
            response = requests.get(
                "https://api.github.com/search/repositories",
                params={"q": f"{term} pushed:>{since}", "sort": "updated", "order": "desc", "per_page": 20},
                headers=HEADERS, timeout=20
            )
            response.raise_for_status()
            for repo in response.json().get("items", []):
                url = repo.get("html_url")
                if not url or url in seen:
                    continue
                seen.add(url)
                out.append({
                    "id": item_id(url), "type": "github",
                    "title": repo.get("full_name", repo.get("name", "Repository")),
                    "summary": clean_text(repo.get("description") or "Repository open source"),
                    "sourceName": "GitHub", "sourceUrl": url,
                    "publishedAt": repo.get("created_at"), "collectedAt": iso_now(),
                    "stars": repo.get("stargazers_count", 0), "forks": repo.get("forks_count", 0),
                    "pushedAt": repo.get("pushed_at"), "language": repo.get("language"),
                    "tags": ["AI", "OPEN SOURCE"], "attribution": repo.get("full_name")
                })
        except Exception as exc:
            print(f"WARN GitHub query {term}: {exc}")
    return out

# Editorial schema v3: five localized magazine-style versions plus long-form detail fields.
EDITORIAL_CACHE_PATH = ROOT.parent / "public" / "editorial_cache.json"
EDITORIAL_MODEL = os.getenv("GEMINI_MODEL", "gemini-3.5-flash-lite")
EDITORIAL_MAX_PER_RUN = int(os.getenv("EDITORIAL_MAX_PER_RUN", "4"))
EDITORIAL_DAILY_LIMIT = int(os.getenv("EDITORIAL_DAILY_LIMIT", "20"))
EDITORIAL_PICK_DAILY_LIMIT = int(os.getenv("EDITORIAL_PICK_DAILY_LIMIT", "3"))
EDITORIAL_LANGUAGES = {
    "it": "Italiano",
    "en": "English",
    "es": "Español",
    "zh-CN": "简体中文",
    "de": "Deutsch"
}

def load_editorial_cache():
    try:
        return json.loads(EDITORIAL_CACHE_PATH.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError):
        return {"items": {}, "daily": {}}

def editorial_fingerprint(item):
    source = {
        "title": item.get("title", ""),
        "summary": item.get("summary", ""),
        "sourceName": item.get("sourceName", ""),
        "sourceUrl": item.get("sourceUrl", ""),
        "stars": item.get("stars"),
        "language": item.get("language")
    }
    if item.get("sourceKind") == "primary_source":
        source["sourceEvidence"] = item.get("sourceEvidence", "")
    return hashlib.sha256(json.dumps(source, sort_keys=True, ensure_ascii=False).encode()).hexdigest()

def gemini_editorial(item, api_key):
    source = {
        "type": item.get("type"),
        "title": item.get("title"),
        "description": item.get("summary"),
        "publisher": item.get("sourceName"),
        "url": item.get("sourceUrl"),
        "stars": item.get("stars"),
        "programming_language": item.get("language"),
        "updated_at": item.get("pushedAt") or item.get("publishedAt"),
        "primary_source_evidence": item.get("sourceEvidence", "")
    }
    is_podcast = item.get("type") == "speak_up"
    role = "You are the senior audio editor of AI Pulse. Present this podcast episode to readers." if is_podcast else "You are the senior editor of AI Pulse, an independent AI technology publication."
    is_primary_source = item.get("sourceKind") == "primary_source"
    task = ("Create an engaging magazine-style episode presentation: explain the themes, context and potential implications, identify guests only when named in metadata, and tell readers what they can expect to learn. Never imply that you listened to the audio; use only supplied metadata." if is_podcast else ("Write an original short news presentation based on the fetched primary-source page. The creator/video is discovery context only: do not mention, quote, summarize, link to, or attribute the news to that creator. Explain the actual announcement/project in plain language, then why it matters. Do not claim independent testing or facts absent from the supplied primary-page evidence." if is_primary_source else "Create a concise magazine-style news presentation. Explain what happened, why it matters and what to watch next. Stay strictly within the supplied evidence."))
    prompt = f""" {role}
Use ONLY the supplied source metadata. Never invent features, benchmarks, adoption, quotes, dates or claims.
If the description is insufficient, say so and keep the presentation cautious.
{task}
Write natural localized versions in all five languages:
Italian (it), English (en), Spanish (es), Simplified Chinese (zh-CN), German (de).
Do not merely translate mechanically: preserve facts and adapt idiom and technical terminology.
Return ONLY valid JSON with this exact shape:
{{
 "editorial": {{
  "it": {{"title":"...", "summary":"...", "why_it_matters":"...", "extended_analysis":"...", "context":"...", "key_takeaways":["...","...","..."], "highlights":["...","..."]}},
  "en": {{"title":"...", "summary":"...", "why_it_matters":"...", "extended_analysis":"...", "context":"...", "key_takeaways":["...","...","..."], "highlights":["...","..."]}},
  "es": {{"title":"...", "summary":"...", "why_it_matters":"...", "extended_analysis":"...", "context":"...", "key_takeaways":["...","...","..."], "highlights":["...","..."]}},
  "zh-CN": {{"title":"...", "summary":"...", "why_it_matters":"...", "extended_analysis":"...", "context":"...", "key_takeaways":["...","...","..."], "highlights":["...","..."]}},
  "de": {{"title":"...", "summary":"...", "why_it_matters":"...", "extended_analysis":"...", "context":"...", "key_takeaways":["...","...","..."], "highlights":["...","..."]}}
 }},
 "relevance_score": 0,
 "editor_pick": false,
 "editor_pick_reason": "short reason in Italian"
}}
Editorial rules:
- title: informative, vivid and clear, never clickbait; max 90 characters.
- summary: the main news presentation. Use 2-3 clear, engaging sentences, normally 260-420 characters, in accessible popular-science magazine journalism.
- why_it_matters: 1-2 meaningful sentences, normally 160-280 characters.
- extended_analysis: an optional-to-read magazine explainer of 2-3 short paragraphs (roughly 600-950 characters), with context, implications and what to watch next.
- Never include or recommend the discovery video as a publication link. The published source URL is the primary source page.
- context: 2-3 sentences explaining the wider technical, economic or social context without unsupported claims.
- key_takeaways: 3 concise and useful points.
- highlights: 2 or 3 concise bullets, only facts supported by metadata.
- Avoid generic openings, repeated conclusions and promotional language. Write for an intelligent non-specialist, with the clarity and curiosity of a science magazine.
- editor_pick is true only if the item is unusually relevant, novel or useful to AI practitioners; be selective. For podcast episodes it must always be false.
- relevance_score is an integer from 0 to 100.
- Distinguish project claims from verified facts. Do not imply independent testing.
SOURCE METADATA:
{json.dumps(source, ensure_ascii=False)}
"""
    endpoint = f"https://generativelanguage.googleapis.com/v1beta/models/{EDITORIAL_MODEL}:generateContent"
    response = requests.post(
        endpoint,
        params={"key": api_key},
        json={
            "contents": [{"parts": [{"text": prompt}]}],
            "generationConfig": {"responseMimeType": "application/json", "maxOutputTokens": 5200}
        },
        headers=HEADERS,
        timeout=60
    )
    response.raise_for_status()
    data = response.json()
    text = data["candidates"][0]["content"]["parts"][0]["text"]
    result = json.loads(text)
    editorial = result.get("editorial", {})
    if not all(lang in editorial for lang in EDITORIAL_LANGUAGES):
        raise ValueError("Gemini response is missing one or more required languages")
    for lang in EDITORIAL_LANGUAGES:
        localized = editorial[lang]
        if not localized.get("title") or not localized.get("summary") or not localized.get("why_it_matters") or not localized.get("extended_analysis") or not localized.get("context"):
            raise ValueError(f"Incomplete editorial content for {lang}")
    return result

def enrich_records(records):
    api_key = os.getenv("GEMINI_API_KEY") or os.getenv("GOOGLE_API_KEY")
    cache = load_editorial_cache()
    cache_items = cache.setdefault("items", {})
    today = datetime.now(timezone.utc).date().isoformat()
    daily = cache.setdefault("daily", {})
    if daily.get("date") != today:
        daily.update({"date": today, "count": 0, "picks": 0})
    if not api_key:
        for item in records:
            item.setdefault("editorial_level", "raw_feed")
            item.setdefault("editorial_status", "raw")
        print("Gemini disabled: publishing Raw Feed only (set GEMINI_API_KEY to enable AI Curated)")
        return records, False

    candidates = []
    for item in records:
        # Creator-radar items are discovery leads, not verified editorial sources.
        # Keep them raw until primary-source research is completed; never promote
        # a video title/description alone into an AI Pulse feature.
        if item.get("sourceKind") == "creator_radar" and item.get("primarySourceReview") != "verified":
            item["editorial_level"] = "raw_feed"
            item["editorial_status"] = "pending_primary_source_review"
            continue
        fingerprint = editorial_fingerprint(item)
        cached = cache_items.get(item["id"])
        if cached and cached.get("fingerprint") == fingerprint and cached.get("editorial") and cached["editorial"].get("editorial_version", 0) >= 3 and all(lang in cached["editorial"].get("editorial", {}) and cached["editorial"]["editorial"][lang].get("extended_analysis") and cached["editorial"]["editorial"][lang].get("context") for lang in EDITORIAL_LANGUAGES):
            item.update(cached["editorial"])
        elif cached and cached.get("fingerprint") == fingerprint and cached.get("retry_after"):
            try:
                if datetime.fromisoformat(cached["retry_after"]) > datetime.now(timezone.utc):
                    continue
            except ValueError:
                pass
            candidates.append((item, fingerprint))
        else:
            candidates.append((item, fingerprint))

    # Reserve half of the existing run budget for Speak Up so episode cards
    # progressively receive localized editorial introductions without raising quotas.
    by_type = {"news": [], "github": [], "speak_up": []}
    for candidate in candidates:
        item_type = candidate[0].get("type")
        if item_type in by_type:
            by_type[item_type].append(candidate)

    # Give priority to original-source stories discovered through the creator radar.
    # Their creator videos themselves never enter the publication queue.
    by_type["news"].sort(
        key=lambda pair: 0 if pair[0].get("sourceKind") == "primary_source" else 1
    )

    remaining_daily = max(0, EDITORIAL_DAILY_LIMIT - int(daily.get("count", 0)))
    remaining_run = min(EDITORIAL_MAX_PER_RUN, remaining_daily)
    podcast_slots = min(len(by_type["speak_up"]), (remaining_run + 1) // 2)
    selected = by_type["speak_up"][:podcast_slots]

    non_podcast = []
    while by_type["news"] or by_type["github"]:
        for item_type in ("news", "github"):
            if by_type[item_type]:
                non_podcast.append(by_type[item_type].pop(0))
    selected.extend(non_podcast[:max(0, remaining_run - len(selected))])
    candidates_to_process = selected

    generated = 0
    cache_changed = False
    for item, fingerprint in candidates_to_process:
        try:
            result = gemini_editorial(item, api_key)
            is_pick = item.get("type") != "speak_up" and bool(result.get("editor_pick")) and int(result.get("relevance_score", 0)) >= 85 and int(daily.get("picks", 0)) < EDITORIAL_PICK_DAILY_LIMIT
            level = "editors_pick" if is_pick else "ai_curated"
            enriched = {
                "editorial": result["editorial"],
                "editorial_level": level,
                "editorial_status": "verified",
                "editorial_model": EDITORIAL_MODEL,
                "editorial_version": 4,
                "relevance_score": max(0, min(100, int(result.get("relevance_score", 0)))),
                "editor_pick_reason": result.get("editor_pick_reason", "") if is_pick else ""
            }
            item.update(enriched)
            cache_items[item["id"]] = {"fingerprint": fingerprint, "editorial": enriched}
            daily["count"] = int(daily.get("count", 0)) + 1
            if is_pick:
                daily["picks"] = int(daily.get("picks", 0)) + 1
            generated += 1
            cache_changed = True
        except Exception as exc:
            print(f"WARN Gemini editorial for {item.get('id')}: {exc}")
            cache_items[item["id"]] = {
                "fingerprint": fingerprint,
                "retry_after": (datetime.now(timezone.utc) + timedelta(hours=6)).isoformat()
            }
            cache_changed = True
            item.setdefault("editorial_level", "raw_feed")
            item.setdefault("editorial_status", "raw")
    for item in records:
        item.setdefault("editorial_level", "raw_feed")
        item.setdefault("editorial_status", "raw")
    print(f"Editorial: {generated} generated, {len(candidates) - generated} pending/failed, run {len(candidates_to_process)}/{remaining_run}, daily {daily.get('count', 0)}/{EDITORIAL_DAILY_LIMIT}")
    if cache_changed or not EDITORIAL_CACHE_PATH.exists():
        EDITORIAL_CACHE_PATH.parent.mkdir(parents=True, exist_ok=True)
        EDITORIAL_CACHE_PATH.write_text(json.dumps(cache, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        cache_changed = True
    return records, cache_changed

def export_feed(records, destination):
    records.sort(key=lambda item: item.get("publishedAt") or item.get("collectedAt") or "", reverse=True)
    payload = {"generatedAt": iso_now(), "count": len(records), "items": records}
    target = Path(destination)
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Exported {len(records)} records to {target}")

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--validate-only", action="store_true")
    parser.add_argument("--export", metavar="PATH")
    args = parser.parse_args()
    for source in CONFIG.get("rss", []) + CONFIG.get("podcast_feeds", []):
        assert source.get("name") and source.get("url", "").startswith("https://")
    print(f"Configuration OK: {len(CONFIG.get('rss', []))} news feeds; {len(CONFIG.get('podcast_feeds', []))} podcast feeds; {len(CONFIG.get('github_queries', []))} GitHub queries")
    if args.validate_only:
        return
    records = collect_rss() + collect_podcasts() + collect_github()
    records, cache_changed = enrich_records(records)
    print(f"Collected {len(records)} items")
    if args.export:
        export_feed(records, args.export)
    else:
        parser.error("Use --export PATH to write the public feed")

if __name__ == "__main__":
    main()
