# AI Pulse

AI Pulse è una prima base di applicazione Android nativa per un briefing quotidiano sull'intelligenza artificiale, con notizie, repository open source e una sezione Speak Up dedicata a podcast e interviste.

## Stato
- Android: Kotlin + Jetpack Compose, package `com.aipulse.app`.
- Identità visiva: interfaccia editoriale chiara, palette avorio/terracotta/inchiostro/salvia.
- Sezioni previste: Tutto, News, GitHub, Speak Up.
- Collector iniziale: RSS/Atom e GitHub Search API.
- Firebase non ancora collegato: i contenuti dell'app sono segnaposto dimostrativi.
- Build Android non ancora verificata con Android Studio/Gradle.

## Costi e infrastruttura
Obiettivo: nessun server dedicato e nessuna API generativa a pagamento. Raccolta pianificata tramite GitHub Actions e persistenza su Firebase Firestore Spark. Entrambi hanno quote/limiti: non attivare Firebase Blaze o servizi a pagamento senza approvazione esplicita.

## Repository
La cartella `app/` contiene il progetto Android; `collector/` contiene il collector Python; `.github/workflows/` contiene il workflow schedulato.

## Setup
Consultare [FIREBASE_SETUP.md](FIREBASE_SETUP.md) prima di configurare Firebase. Non caricare mai chiavi private o file di credenziali nel repository.

## Integrità editoriale
I contenuti devono mantenere titolo, fonte, URL e data di pubblicazione. Le sintesi devono essere parafrasi originali con link alla fonte; non riprodurre articoli. Speak Up non deve attribuire citazioni senza trascrizioni verificabili.
