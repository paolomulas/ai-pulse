"""AI Pulse collector: public RSS/Atom feeds and GitHub Search API."""
import argparse
import hashlib
import html
import json
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

def collect_rss():
    out = []
    for source in CONFIG.get("rss", []):
        try:
            feed = feedparser.parse(source["url"], request_headers=HEADERS)
            if getattr(feed, "bozo", False):
                print(f"WARN feed parse issue {source['name']}: {feed.get('bozo_exception')}")
            for entry in feed.entries[:40]:
                url = entry.get("link", "")
                if not url.startswith("https://"):
                    continue
                out.append({
                    "id": item_id(url), "type": "news",
                    "title": clean_text(entry.get("title", "Senza titolo"), 400),
                    "summary": clean_text(entry.get("summary", entry.get("description", ""))),
                    "sourceName": source["name"], "sourceUrl": url,
                    "publishedAt": published(entry), "collectedAt": iso_now(),
                    "sourceKind": source.get("kind", "newsroom"),
                    "sourceWeight": source.get("weight", 5),
                    "tags": ["AI"], "attribution": source["name"]
                })
        except Exception as exc:
            print(f"WARN {source['name']}: {exc}")
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

def export_feed(records, destination):
    records.sort(key=lambda item: item.get("publishedAt") or item.get("collectedAt") or "", reverse=True)
    payload = {"generatedAt": iso_now(), "count": len(records), "items": records}
    target = Path(destination)
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\\n", encoding="utf-8")
    print(f"Exported {len(records)} records to {target}")

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--validate-only", action="store_true")
    parser.add_argument("--export", metavar="PATH")
    args = parser.parse_args()
    for source in CONFIG.get("rss", []):
        assert source.get("name") and source.get("url", "").startswith("https://")
    print(f"Configuration OK: {len(CONFIG.get('rss', []))} RSS feeds; {len(CONFIG.get('github_queries', []))} GitHub queries")
    if args.validate_only:
        return
    records = collect_rss() + collect_github()
    print(f"Collected {len(records)} items")
    if args.export:
        export_feed(records, args.export)
    else:
        parser.error("Use --export PATH to write the public feed")

if __name__ == "__main__":
    main()
