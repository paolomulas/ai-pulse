# Firebase setup per AI Pulse (riutilizzo del progetto esistente)

AI Pulse userà il progetto Firebase già esistente `bet-studio`, piano Spark. Non creare un secondo progetto e non attivare Blaze.

## 1. Registrare AI Pulse come seconda app Android

Nel progetto Firebase `bet-studio`:
1. Dalla panoramica progetto, cliccare **Aggiungi app** e scegliere Android.
2. Package Android: `com.aipulse.app` (deve corrispondere esattamente).
3. Nickname facoltativo: `AI Pulse`.
4. SHA-1 non necessario per la lettura pubblica iniziale.
5. Scaricare `google-services.json` e collocarlo in `app/google-services.json` nel progetto locale. Non caricarlo nel repository: è escluso da `.gitignore`.
6. Non abilitare Authentication, Analytics o altri prodotti per ora.

## 2. Firestore: verificare che esista

Nel menu **Firestore Database**:
- Se il database esiste già, NON crearne un altro.
- Se non esiste, creare il database in modalità produzione, scegliendo una regione UE disponibile (preferibilmente europe-west).
- Prima di modificare le regole, copiare e conservare quelle attuali: il progetto è condiviso con Bet-Studio.

Aggiungere al blocco `match /databases/{database}/documents` una regola dedicata, senza cancellare o sostituire le regole esistenti:

```
match /ai_pulse_content/{document} {
  allow read: if true;
  allow write: if false;
}
```

Questa regola rende pubblica in sola lettura soltanto la collezione AI Pulse. Le regole esistenti di Bet-Studio devono rimanere invariate. Le scritture del collector usano Admin SDK e non sono autorizzate da queste regole client.

## 3. Credenziale del collector (passaggio proprietario)

Per pubblicare automaticamente i contenuti, creare in Google Cloud Console un service account dedicato al progetto `bet-studio`, con ruolo minimo **Cloud Datastore User** (ruolo predefinito che consente accesso Firestore). Generare una chiave JSON.

Nel repository GitHub `paolomulas/ai-pulse`, aprire Settings → Secrets and variables → Actions:
- Secret `FIREBASE_SERVICE_ACCOUNT_JSON`: contenuto completo del JSON della chiave.
- Variable `FIREBASE_PROJECT_ID`: ID effettivo del progetto Firebase (non il nome visualizzato).
- Variable `ENABLE_FIRESTORE_PUBLISH`: valore `true`, soltanto dopo avere verificato database, regole e credenziale.

Non inviare mai la chiave privata in chat e non commetterla nel repository. Se la chiave viene esposta, revocarla immediatamente.

## 4. Primo test

Da Actions → AI Pulse collector → Run workflow, eseguire prima la validazione. Solo dopo la configurazione delle credenziali si può abilitare la pubblicazione.

## Limiti e costi

Restare sul piano Firebase Spark. Non cliccare upgrade/Blaze e non inserire una carta. Firestore e GitHub Actions hanno quote e limiti gratuiti; l'uso va monitorato. La pubblicazione automatica è disattivata finché il proprietario non configura esplicitamente i segreti.
