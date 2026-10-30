# Architettura AI Pulse

- Android nativo: Kotlin + Jetpack Compose.
- Raccolta contenuti: GitHub Actions (Python).
- Fonti: RSS/Atom pubblici e GitHub Search API.
- Distribuzione feed: file JSON statico pubblico nel repository.
- App: richiesta HTTPS al feed raw GitHub e fallback dimostrativo se non disponibile.

## Pipeline
1. GitHub Actions avvia i collector.
2. Lettura fonti pubbliche con timestamp e URL originale.
3. Normalizzazione e deduplicazione tramite hash URL.
4. Esportazione in `public/feed.json`.
5. Commit automatico del feed aggiornato.
6. App Android scarica e visualizza i contenuti.

## Speak Up
La sezione verrà attivata quando saranno individuati feed podcast/interviste affidabili. Finestra mobile di 24 ore; citazioni solo con trascrizioni accessibili e verificabili.

## Sicurezza e costi
Non sono necessarie chiavi Firebase Admin, service account o server. Il feed è pubblico: non includere dati privati. GitHub Actions e API pubbliche hanno quote/limiti; non promettere aggiornamenti al minuto esatto.
