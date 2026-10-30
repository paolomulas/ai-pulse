# AI Pulse

AI Pulse è un'app Android nativa per un briefing quotidiano sull'intelligenza artificiale: notizie, repository open source e una sezione Speak Up per podcast e interviste.

## Stato
- Android: Kotlin + Jetpack Compose, package `com.aipulse.app`.
- Interfaccia editoriale chiara: avorio, terracotta, inchiostro e salvia.
- Feed unificato con filtri Tutto, News, GitHub e Speak Up.
- Collector Python per fonti RSS/Atom e GitHub Search API.
- Feed pubblico JSON aggiornato da GitHub Actions.
- L'app usa un feed statico HTTPS; non richiede chiavi Firebase o backend dedicato.

## Costi e sicurezza
Nessun server dedicato, nessuna API AI a pagamento e nessuna chiave amministrativa Firebase. GitHub Actions ha quote e limiti di utilizzo; i tempi schedulati possono subire ritardi. Firebase rimane sul piano Spark e non è necessario per la distribuzione del feed.

## Setup
Vedi [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) e [FIREBASE_SETUP.md](FIREBASE_SETUP.md). Non caricare credenziali private nel repository.

## Integrità editoriale
Ogni elemento conserva fonte, URL e data. Le sintesi devono essere parafrasi originali con link alla fonte, senza riprodurre gli articoli. Speak Up mostrerà citazioni dirette solo con trascrizioni verificabili.
