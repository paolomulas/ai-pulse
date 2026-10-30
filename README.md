# AI Pulse

AI Pulse è un'app Android nativa per un briefing quotidiano sull'intelligenza artificiale: notizie, repository open source e una sezione Speak Up per podcast e interviste.

## Stato
- Android: Kotlin + Jetpack Compose, package `com.aipulse.app`.
- Interfaccia editoriale chiara: avorio, terracotta, inchiostro e salvia.
- Feed unificato con filtri per categoria e livelli editoriali Raw Feed, AI Curated ed Editor’s Pick.
- Localizzazione editoriale predisposta in italiano, inglese, spagnolo e cinese semplificato.
- Speak Up come room dedicata a podcast e interviste.
- Collector Python per fonti RSS/Atom e GitHub Search API.
- Feed pubblico JSON aggiornato da GitHub Actions.
- L'app usa un feed statico HTTPS; non richiede chiavi Firebase o backend dedicato.

## Costi e sicurezza
Nessun server dedicato e nessuna chiave amministrativa Firebase. La generazione AI è opzionale: senza `GEMINI_API_KEY` il collector pubblica contenuti Raw Feed; con la chiave configurata nelle GitHub Actions può generare schede multilingue tramite Gemini API. Il modello è configurabile con `GEMINI_MODEL`. Cache persistente, massimo 4 nuovi contenuti per esecuzione, tetto giornaliero predefinito di 20 contenuti e massimo 3 Editor’s Pick al giorno. Verificare quote, tariffe e condizioni d’uso Google prima di abilitare il servizio in produzione.

## Setup
Vedi [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) e [FIREBASE_SETUP.md](FIREBASE_SETUP.md). Non caricare credenziali private nel repository.

## Integrità editoriale
Ogni elemento conserva fonte, URL e data. Le schede AI sono generate esclusivamente dai metadati raccolti e devono evitare affermazioni non supportate; quando i metadati non bastano, il contenuto resta Raw Feed. Le quattro localizzazioni sono generate in un’unica richiesta e conservate in `public/editorial_cache.json`, così gli aggiornamenti successivi non consumano nuovamente token. Le citazioni dirette sono ammesse solo con trascrizioni verificabili.
