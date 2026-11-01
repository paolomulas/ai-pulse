# AI Pulse — Google Play submission pack

## Store listing (Italian)

**App name:** AI Pulse

**Short description (max 80 characters):**  
Notizie, ricerca e strumenti per capire l'evoluzione dell'intelligenza artificiale.

**Full description:**

AI Pulse è un briefing editoriale dedicato all'intelligenza artificiale e alle trasformazioni che sta portando nella ricerca, nel lavoro e nell'industria digitale.

Un unico spazio per seguire:
- **News:** sviluppi, annunci e notizie dal mondo dell'AI.
- **Open Source:** progetti, repository e strumenti da esplorare.
- **Speak Up:** podcast e conversazioni su ricerca, prodotti e nuove idee.

Le schede editoriali propongono sintesi e contesto per orientarsi tra i temi più rilevanti. Ogni contenuto indica la propria fonte e permette di aprire la pubblicazione originale: AI Pulse non sostituisce la consultazione delle fonti.

**Funzioni principali**
- Feed aggiornato tramite connessione Internet.
- Filtri per News, GitHub e Speak Up.
- Filtri per lingua e livello editoriale.
- Collegamenti diretti alle fonti originali.
- Nessun account richiesto e nessuna pubblicità nell'app.

Alcune sintesi editoriali sono realizzate con il supporto di strumenti di intelligenza artificiale. Le fonti originali restano il riferimento per verificare informazioni, contesto e aggiornamenti.

AI Pulse è un progetto indipendente e non è affiliato alle organizzazioni o alle testate citate.

## Suggested Play Console metadata

- **Category:** News & Magazines (verify against the final product and Console taxonomy).
- **Ads:** No.
- **App access:** All functionality available without login; no special access instructions.
- **Target audience:** Not designed specifically for children; select adult age ranges consistent with the product and the actual content.
- **Privacy policy URL:** https://paolomulas.github.io/ai-pulse/privacy.html
- **Public contact URL:** https://paolomulas.github.io/ai-pulse/contact.html
- **Editorial guidelines:** https://paolomulas.github.io/ai-pulse/editorial-guidelines.html
- **Contact email:** paolomulas@gmail.com (must be confirmed by the developer).
- **Website:** https://paolomulas.github.io/ai-pulse/
- **Package:** com.aipulse.app
- **Current version:** 0.3.0 (versionCode 3)
- **Target SDK:** 36 (Android 16).

## News and Magazine declaration

The app is likely in scope because it presents news/magazine content from multiple third-party sources and includes AI-assisted editorial summaries. In Play Console:
- Declare it as a news/magazine app if prompted.
- Declare it as an aggregator if the majority of in-app content comes from third-party publishers.
- Identify the correct legal entity / parent entity from the developer account. Do not guess this field.
- Provide the public contact URL above and confirm the contact email.
- Main content focus likely includes Science and technology, Education and research, Business, and possibly News and current affairs. Select only categories actually represented by the feed.
- Declare no app-branded YouTube channel unless one is genuinely operated for AI Pulse. Do not list Simone Rizzo's channel as an AI Pulse channel.
- The app must clearly identify the original publisher/source for third-party items; the current Android cards show the source name and open the source URL.
- Do not claim formal editorial standards, memberships, awards, or journalistic certifications unless they exist and can be documented.

## Data safety — review before submitting

The Android manifest currently requests only INTERNET. The native app has no account system, ad SDK, analytics SDK, location permission, contacts permission, camera, microphone, or file access. The current native app keeps UI filters in transient session state; it does not persist preferences, saved stories or reading history locally.

However, the app requests a public feed from GitHub over HTTPS. The network provider necessarily receives connection metadata, including the source IP address; Google Play's Data safety guidance explicitly includes approximate location inferred from IP addresses in its location data definitions. Review GitHub's actual logging/retention and determine whether the applicable Play Console answer is an ephemeral/service-provider case or whether approximate location must be declared. Do not automatically select "no data collected" without resolving this point.

## Required assets and final checks

- Play listing icon: 512 × 512 PNG (separate from launcher icon).
- Feature graphic: 1024 × 500 PNG.
- At least two genuine phone screenshots showing the real installed app; do not use mockups that misrepresent functionality.
- Verify privacy and contact URLs are live and public after GitHub Pages deployment.
- Verify email address and developer/legal entity details.
- Install and test the signed release build on a physical Android device.
- Complete content rating and news/magazine declarations.
- Upload a signed AAB and complete Play Console review.

## Account-dependent release gate

For personal developer accounts created after 13 November 2023, Google requires a closed test with at least 12 testers opted in continuously for 14 days before production access can be requested. Confirm account type and account creation date in Play Console; this requirement does not apply universally to every account.
