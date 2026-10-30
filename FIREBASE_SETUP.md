# AI Pulse: configurazione tecnica

## Architettura scelta (senza credenziali amministrative)

AI Pulse usa GitHub Actions per raccogliere feed RSS/Atom e repository GitHub e pubblicare un file JSON statico in `public/feed.json`. L'app Android legge il feed pubblico da GitHub.

URL feed:
`https://raw.githubusercontent.com/paolomulas/ai-pulse/main/public/feed.json`

Questa soluzione evita:
- chiavi private Firebase Admin;
- accesso amministrativo al progetto condiviso con Bet-Studio;
- server dedicati e servizi cloud a pagamento.

## Firebase

L'app Android è stata registrata nel progetto Firebase esistente, ma la lettura del feed non dipende da Firestore. Le regole Firestore pubblicate per `ai_pulse_content` restano isolate e non sono usate dal collector. Non generare service account key e non attivare Blaze.

Il file `google-services.json` scaricato non è necessario per la versione attuale: non inserirlo nel repository pubblico.

## Aggiornamenti automatici

GitHub Actions esegue il collector ogni 30 minuti e aggiorna `public/feed.json`. È possibile avviare manualmente il workflow dalla scheda Actions. La pianificazione GitHub può subire ritardi e non garantisce l'esecuzione al minuto esatto.

## Fonti e limiti editoriali

RSS/Atom e GitHub Search API pubblica. Speak Up resta vuota finché non vengono individuati feed podcast e fonti verificabili. Non inventare citazioni. Conservare link e attribuzione e produrre sintesi originali, senza ripubblicare articoli.
