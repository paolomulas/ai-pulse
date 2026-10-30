# Architettura AI Pulse

- Android nativo: Kotlin + Jetpack Compose.
- Database: Cloud Firestore, piano Spark.
- Raccolta: GitHub Actions schedulato, senza server sempre acceso.
- Fonti: RSS/Atom, GitHub REST API pubblica, feed podcast da validare.
- Distribuzione: Google Play.

## Pipeline
1. GitHub Actions avvia i collector.
2. Lettura di fonti pubbliche con timestamp e URL originale.
3. Normalizzazione e deduplicazione tramite hash URL.
4. Ranking futuro basato su freschezza, pertinenza e affidabilità della fonte.
5. Pubblicazione su Firestore.
6. App Android in sola lettura.

## Speak Up
Finestra mobile di 24 ore basata sulla pubblicazione effettiva dell'episodio/intervista. Citazioni solo con trascrizioni accessibili e verificabili; altrimenti solo metadati e sintesi della descrizione.

## Sicurezza e costi
Nessuna chiave privata nell'app. Credenziali del collector solo in GitHub Actions Secrets. Firestore rules in sola lettura pubblica per i contenuti pubblicati. Non passare da Firebase Spark a Blaze senza consenso esplicito. Quote e limiti dei servizi gratuiti vanno monitorati.
