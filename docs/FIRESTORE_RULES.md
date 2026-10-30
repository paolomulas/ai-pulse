# Firestore: regole per AI Pulse

AI Pulse usa la collezione `ai_pulse_content` nel progetto Firebase condiviso con Bet-Studio.

## Regola da aggiungere

Nel blocco già esistente:

```
service cloud.firestore {
  match /databases/{database}/documents {
    // altre regole già presenti
  }
}
```

aggiungere:

```
match /ai_pulse_content/{document} {
  allow read: if true;
  allow write: if false;
}
```

## Attenzione alle regole esistenti

Firestore combina le autorizzazioni di tutti i blocchi `match` applicabili con una logica OR. Una regola specifica con `allow write: if false` NON annulla un'eventuale regola più ampia già presente che consenta le scritture, ad esempio:

```
match /{document=**} {
  allow read, write: if true;
}
```

Per questo motivo non sostituire le regole attuali e non pubblicare modifiche finché non abbiamo verificato il contenuto completo della scheda Rules. Dobbiamo mantenere il comportamento di Bet-Studio e impedire scritture client nella collezione AI Pulse.

Il collector usa il Firebase Admin SDK con service account: le regole Firestore client non si applicano alle sue scritture.
