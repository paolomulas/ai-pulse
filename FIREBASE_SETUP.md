# Configurazione Firebase (richiede intervento del proprietario)

1. Aprire https://console.firebase.google.com/ e creare un progetto `ai-pulse` (o altro ID disponibile).
2. Selezionare il piano Spark. Non passare a Blaze.
3. Registrare un'app Android con package `com.aipulse.app`.
4. Creare Cloud Firestore in modalità production.
5. Applicare regole iniziali:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /content/{document} {
      allow read: if true;
      allow write: if false;
    }
  }
}
```

6. Creare una credenziale dedicata al collector e salvarne il JSON come GitHub Actions secret `FIREBASE_SERVICE_ACCOUNT_JSON`.
7. Salvare l'ID progetto come Actions variable `FIREBASE_PROJECT_ID`.
8. Impostare `ENABLE_FIRESTORE_PUBLISH=true` solo dopo aver testato configurazione e regole.

Non caricare mai chiavi private nel repository o in chat. Non abilitare billing/Blaze senza autorizzazione esplicita.
