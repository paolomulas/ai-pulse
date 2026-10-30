# AI Pulse

AI Pulse è un'app Android nativa per un briefing quotidiano sull'intelligenza artificiale: notizie, repository open source e una sezione Speak Up per podcast e interviste.

## Stato
- Android: Kotlin + Jetpack Compose, package `com.aipulse.app`.
- Identità visiva editoriale chiara: avorio, terracotta, inchiostro e salvia.
- Feed unificato con filtri Tutto, News, GitHub e Speak Up.
- Collector Python per fonti RSS/Atom e GitHub Search API.
- Integrazione Firestore predisposta sulla collezione `ai_pulse_content`.
- Configurazione Firebase non ancora completata: richiede registrazione dell'app Android e credenziali di proprietà.

## Architettura a costo controllato
Riutilizzo del progetto Firebase esistente `bet-studio` sul piano Spark, senza creare un nuovo progetto e senza abilitare Blaze. GitHub Actions esegue la raccolta pianificata; Firestore conserva il feed. Entrambi i servizi hanno quote e limiti gratuiti.

## Setup
Seguire [FIREBASE_SETUP.md](FIREBASE_SETUP.md). Non caricare mai chiavi private nel repository o in chat.

## Integrità editoriale
Ogni elemento conserva fonte, URL e data. Le sintesi devono essere parafrasi originali con link alla fonte, senza riprodurre gli articoli. Speak Up deve mostrare citazioni dirette solo con trascrizioni verificabili; i feed podcast saranno aggiunti dopo la verifica delle fonti.
